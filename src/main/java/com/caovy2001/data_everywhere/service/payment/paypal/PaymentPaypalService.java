package com.caovy2001.data_everywhere.service.payment.paypal;

import com.caovy2001.data_everywhere.command.cart_item.CommandGetListCartItem;
import com.caovy2001.data_everywhere.command.dataset_collection.CommandGetListDatasetCollection;
import com.caovy2001.data_everywhere.command.payment.CommandPaypalAuthorizePayment;
import com.caovy2001.data_everywhere.command.payment.CommandPaypalExecutePayment;
import com.caovy2001.data_everywhere.constant.Constant;
import com.caovy2001.data_everywhere.constant.ExceptionConstant;
import com.caovy2001.data_everywhere.entity.CartItemEntity;
import com.caovy2001.data_everywhere.entity.DatasetCollectionEntity;
import com.caovy2001.data_everywhere.entity.PaymentMethodEntity;
import com.caovy2001.data_everywhere.entity.TransactionEntity;
import com.caovy2001.data_everywhere.enumeration.EPaymentMethod;
import com.caovy2001.data_everywhere.model.payment.PaymentPaypalResponse;
import com.caovy2001.data_everywhere.service.BaseService;
import com.caovy2001.data_everywhere.service.cart_item.ICartItemService;
import com.caovy2001.data_everywhere.service.dataset_collection.IDatasetCollectionService;
import com.caovy2001.data_everywhere.service.jedis.IJedisService;
import com.caovy2001.data_everywhere.service.payment_method.IPaymentMethodService;
import com.caovy2001.data_everywhere.service.transaction.ITransactionService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.paypal.api.payments.*;
import com.paypal.base.rest.APIContext;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

@Component
public class PaymentPaypalService extends BaseService implements IPaymentPaypalServiceAPI, IPaymentPaypalService {
    @Autowired
    private ICartItemService cartItemService;

    @Autowired
    private IDatasetCollectionService datasetCollectionService;

    @Autowired
    private IJedisService jedisService;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private IPaymentMethodService paymentMethodService;

    @Autowired
    private ITransactionService transactionService;

    @Override
    public PaymentPaypalResponse authorizePayment(CommandPaypalAuthorizePayment command) throws Exception {
        if (StringUtils.isBlank(command.getUserId()) || CollectionUtils.isEmpty(command.getCartItemIds())) {
            throw new Exception(ExceptionConstant.missing_param);
        }

        //region Tính tổng đơn hàng
        List<CartItemEntity> cartItemEntities = cartItemService.getList(CommandGetListCartItem.builder().userId(command.getUserId()).ids(command.getCartItemIds()).purchased(false).returnFields(List.of("id", "dataset_collection_id")).build());
        if (CollectionUtils.isEmpty(cartItemEntities)) {
            throw new Exception("cart_items_empty");
        }

        List<String> datasetCollectionIds = cartItemEntities.stream().map(CartItemEntity::getDatasetCollectionId).toList();
        if (CollectionUtils.isEmpty(datasetCollectionIds)) {
            throw new Exception("dataset_collection_ids_empty");
        }

        List<DatasetCollectionEntity> datasetCollectionEntities = datasetCollectionService.getList(CommandGetListDatasetCollection.builder().ids(datasetCollectionIds).build());

        if (CollectionUtils.isEmpty(datasetCollectionEntities)) {
            throw new Exception("dataset_collections_empty");
        }

        AtomicLong atomicTotal = new AtomicLong(0L);
        datasetCollectionEntities.forEach(d -> atomicTotal.addAndGet(d.getAmount()));
        long total = atomicTotal.get();
        //endregion

        //region Tạo Payer
        Payer payer = new Payer();
        payer.setPaymentMethod("paypal");
        payer.setPayerInfo(new PayerInfo());
        //endregion

        //region Set redirect url
        RedirectUrls redirectUrls = new RedirectUrls();
        redirectUrls.setCancelUrl("http://127.0.0.1:5500/payment/paypal/cancel");
        redirectUrls.setReturnUrl("http://127.0.0.1:5500/payment/paypal/review_payment");
        //endregion

        //region Create transaction
        Details details = new Details();
        Amount amount = new Amount();
        amount.setCurrency("USD");
        amount.setTotal(String.valueOf(total));
        amount.setDetails(details);

        Transaction transaction = new Transaction();
        transaction.setAmount(amount);
        transaction.setDescription("Test order 1");

        ItemList itemList = new ItemList();
        itemList.setItems(new ArrayList<>());
        for (DatasetCollectionEntity datasetCollectionEntity : datasetCollectionEntities) {
            Item item = new Item();
            item.setCurrency("USD");
            item.setName(datasetCollectionEntity.getName());
            item.setPrice(String.valueOf(datasetCollectionEntity.getAmount()));
            item.setQuantity("1");
            itemList.getItems().add(item);
        }
        transaction.setItemList(itemList);
        //endregion

        //region create request
        Payment requestPayment = new Payment();
        requestPayment.setTransactions(List.of(transaction));
        requestPayment.setRedirectUrls(redirectUrls);
        requestPayment.setPayer(payer);
        requestPayment.setIntent("authorize");

        APIContext apiContext = new APIContext(Constant.Paypal.CLIENT_ID, Constant.Paypal.CLIENT_SECRET, Constant.Paypal.MODE);
        Payment approvedPayment = requestPayment.create(apiContext);

        String paymentJedisKey = Constant.JedisPrefix.userIdPrefix_ + command.getUserId() +
                Constant.JedisPrefix.COLON +
                Constant.JedisPrefix.Paypal.paymentIdPrefix_ + approvedPayment.getId();
        List<String> cartItemIds = cartItemEntities.stream().map(CartItemEntity::getId).toList();
        Map<String, Object> paymentJedisValue = new HashMap<>();
        paymentJedisValue.put("cartItemIds", cartItemIds);
        jedisService.setWithExpired(paymentJedisKey, objectMapper.writeValueAsString(paymentJedisValue), 60 * 60 * 24); // 1 day
        //endregion

        AtomicReference<String> approvalLinkAtomic = new AtomicReference<String>();
        approvedPayment.getLinks().stream().filter(l -> "approval_url".equalsIgnoreCase(l.getRel())).findFirst().ifPresent(l -> {
            approvalLinkAtomic.set(l.getHref());
        });
        return PaymentPaypalResponse.builder().approvalLink(approvalLinkAtomic.get()).build();
    }

    @Override
    public PaymentPaypalResponse executePayment(CommandPaypalExecutePayment command) throws Exception {
        if (StringUtils.isAnyBlank(command.getUserId(), command.getPaymentId(), command.getPayerID())) {
            throw new Exception(ExceptionConstant.missing_param);
        }

        String paymentJedisKey = Constant.JedisPrefix.userIdPrefix_ + command.getUserId() +
                Constant.JedisPrefix.COLON +
                Constant.JedisPrefix.Paypal.paymentIdPrefix_ + command.getPaymentId();
        String paymentJedisValueStr = jedisService.get(paymentJedisKey);
        if (StringUtils.isBlank(paymentJedisValueStr)) {
            throw new Exception("payment_null");
        }

        Map<String, Object> paymentJedisValue = objectMapper.readValue(paymentJedisValueStr, Map.class);
        if (paymentJedisValue == null ||
                paymentJedisValue.isEmpty() ||
                paymentJedisValue.get("cartItemIds") == null) {
            throw new Exception("payment_process_fail");
        }

        List<String> cartItemIds = objectMapper.convertValue(paymentJedisValue.get("cartItemIds"), List.class);
        if (cartItemIds == null || CollectionUtils.isEmpty(cartItemIds)) {
            throw new Exception("payment_process_fail");
        }

        List<CartItemEntity> cartItemEntities = cartItemService.getList(CommandGetListCartItem.builder()
                .userId(command.getUserId())
                .ids(cartItemIds)
                .purchased(false)
                .build());
        if (CollectionUtils.isEmpty(cartItemEntities)) {
            throw new Exception("payment_process_fail");
        }

        // Execute payment
        PaymentExecution paymentExecution = new PaymentExecution();
        paymentExecution.setPayerId(command.getPayerID());
        Payment payment = new Payment().setId(command.getPaymentId());
        APIContext apiContext = new APIContext(Constant.Paypal.CLIENT_ID, Constant.Paypal.CLIENT_SECRET, Constant.Paypal.MODE);

        payment = payment.execute(apiContext, paymentExecution);
        if (CollectionUtils.isEmpty(payment.getTransactions())) {
            throw new Exception("payment_process_fail");
        }

        // Lấy PAYPAL payment method
        PaymentMethodEntity paymentMethodEntity = paymentMethodService.findByCode(EPaymentMethod.PAYPAL.name());

        // Tạo transaction
        TransactionEntity transactionEntity = TransactionEntity.builder()
                .userId(command.getUserId())
                .amount(Float.valueOf(payment.getTransactions().get(0).getAmount().getTotal()).longValue())
                .paymentMethodId(paymentMethodEntity.getId())
                .build();

        transactionEntity = transactionService.add(transactionEntity);
        if (transactionEntity == null) {
            throw new Exception("cannot_create_transaction");
        }

        for (CartItemEntity cartItemEntity: cartItemEntities) {
            cartItemEntity.setTransactionId(transactionEntity.getId());
        }
        cartItemEntities = cartItemService.updateMany(cartItemEntities);
        if (CollectionUtils.isEmpty(cartItemEntities)) {
            throw new Exception("cannot_update_cart_items");
        }

        return PaymentPaypalResponse.builder()
                .transaction(transactionEntity)
                .cartItems(cartItemEntities)
                .build();
    }
}




























