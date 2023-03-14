package com.caovy2001.data_everywhere.service.payment.paypal;

import com.caovy2001.data_everywhere.command.payment.CommandPaypalAuthorizePayment;
import com.caovy2001.data_everywhere.command.payment.CommandPaypalExecutePayment;
import com.caovy2001.data_everywhere.entity.CartItemEntity;
import com.caovy2001.data_everywhere.model.payment.PaymentPaypalResponse;
import com.caovy2001.data_everywhere.service.IBaseService;

import java.util.List;

public interface IPaymentPaypalServiceAPI extends IBaseService {

    PaymentPaypalResponse authorizePayment(CommandPaypalAuthorizePayment command) throws Exception;

    PaymentPaypalResponse executePayment(CommandPaypalExecutePayment command) throws Exception;

    List<CartItemEntity> getCartItemsByPaymentIdForAPI(String id, String paymentId) throws Exception;
}
