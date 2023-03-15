package com.caovy2001.data_everywhere.service.cart_item;

import com.caovy2001.data_everywhere.command.cart.CommandAddCart;
import com.caovy2001.data_everywhere.command.cart.CommandUpdateCart;
import com.caovy2001.data_everywhere.command.cart_item.CommandAPIAddCartItem;
import com.caovy2001.data_everywhere.command.cart_item.CommandGetListCartItem;
import com.caovy2001.data_everywhere.command.cart_item.CommandRemoveCartItem;
import com.caovy2001.data_everywhere.command.dataset_collection.CommandGetListDatasetCollection;
import com.caovy2001.data_everywhere.constant.ExceptionConstant;
import com.caovy2001.data_everywhere.entity.CartEntity;
import com.caovy2001.data_everywhere.entity.CartItemEntity;
import com.caovy2001.data_everywhere.entity.DatasetCollectionEntity;
import com.caovy2001.data_everywhere.model.pagination.Paginated;
import com.caovy2001.data_everywhere.repository.CartItemRepository;
import com.caovy2001.data_everywhere.service.BaseService;
import com.caovy2001.data_everywhere.service.cart.ICartService;
import com.caovy2001.data_everywhere.service.dataset_collection.IDatasetCollectionService;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Component
@Slf4j
public class CartItemService extends BaseService implements ICartItemServiceAPI, ICartItemService {

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private IDatasetCollectionService datasetCollectionService;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private ICartService cartService;

    @Override
    public CartItemEntity addCartItem(@NonNull CommandAPIAddCartItem command) throws Exception {
        if (StringUtils.isAnyBlank(command.getUserId(), command.getDatasetCollectionId())) {
            throw new Exception(ExceptionConstant.missing_param);
        }

        long _countDataset = datasetCollectionService.countById(command.getDatasetCollectionId());
        if (_countDataset == 0) {
            throw new Exception("dataset_not_exist");
        }

        long _countExist = cartItemRepository.countByUserIdAndDatasetCollectionIdAndTransactionId(command.getUserId(), command.getDatasetCollectionId(), null);
        if (_countExist > 0) {
            throw new Exception("dataset_exist_in_cart");
        }

        CartItemEntity cartItemEntity = CartItemEntity.builder()
                .userId(command.getUserId())
                .datasetCollectionId(command.getDatasetCollectionId())
                .build();
        cartItemEntity = cartItemRepository.insert(cartItemEntity);

        // Thêm vào bảng Cart
        CartEntity cartEntity = cartService.findByUserId(command.getUserId());
        if (cartEntity == null) {
            cartService.add(CommandAddCart.builder()
                    .userId(command.getUserId())
                    .cartItemIds(List.of(cartItemEntity.getId()))
                    .datasetCollectionIds(List.of(cartItemEntity.getDatasetCollectionId()))
                    .checkExistByUserId(false)
                    .build());
        } else {
            cartEntity.getCartItemIds().add(cartItemEntity.getId());
            cartEntity.getDatasetCollectionIds().add(cartItemEntity.getDatasetCollectionId());
            cartService.update(CommandUpdateCart.builder()
                    .id(cartEntity.getId())
                    .cartItemIds(cartEntity.getCartItemIds())
                    .datasetCollectionIds(cartEntity.getDatasetCollectionIds())
                    .build());
        }

        return cartItemEntity;
    }

    @Override
    public Paginated<CartItemEntity> getPaginatedList(@NonNull CommandGetListCartItem command) throws Exception {
        if (StringUtils.isBlank(command.getUserId())) {
            throw new Exception(ExceptionConstant.missing_param);
        }

        if (command.getPage() <= 0) {
            throw new Exception("invalid_page");
        }

        Query query = this.buildQueryGetList(command);
        if (query == null) {
            return new Paginated<>(new ArrayList<>(), command.getPage(), command.getSize(), 0);
        }

        long total = mongoTemplate.count(query, CartItemEntity.class);
        if (total == 0L) {
            return new Paginated<>(new ArrayList<>(), command.getPage(), command.getSize(), 0);
        }

        PageRequest pageRequest = PageRequest.of(command.getPage() - 1, command.getSize());
        if (command.getSort() != null && command.getSort().getDirection() != null) {
            pageRequest = pageRequest.withSort(Sort.by(command.getSort().getDirection(), command.getSort().getField()));
        }
        query.with(pageRequest);
        List<CartItemEntity> cartItemEntities = mongoTemplate.find(query, CartItemEntity.class);
        this.setViewForPaginatedList(cartItemEntities, command);
        return new Paginated<>(cartItemEntities, command.getPage(), command.getSize(), total);
    }

    @Override
    public boolean removeCartItem(@NonNull CommandRemoveCartItem command) throws Exception {
        if (StringUtils.isBlank(command.getUserId()) || CollectionUtils.isEmpty(command.getCartItemIds())) {
            throw new Exception(ExceptionConstant.missing_param);
        }

        List<CartItemEntity> cartItemEntities = this.getList(CommandGetListCartItem.builder()
                .userId(command.getUserId())
                .purchased(false)
                .ids(command.getCartItemIds())
                .build());

        if (CollectionUtils.isEmpty(cartItemEntities)) {
            throw new Exception("cart_item_not_exist");
        }

        cartItemRepository.deleteAllById(cartItemEntities.stream().map(CartItemEntity::getId).toList());
        return true;
    }

    private void setViewForPaginatedList(List<CartItemEntity> cartItemEntities, CommandGetListCartItem command) {
        if (CollectionUtils.isEmpty(cartItemEntities) || command == null) {
            return;
        }

        if (BooleanUtils.isFalse(command.isHasDatasetCollection())) {
            return;
        }

        for (CartItemEntity cartItemEntity : cartItemEntities) {
            if (BooleanUtils.isTrue(command.isHasDatasetCollection())) {
                cartItemEntity.setDatasetCollection(datasetCollectionService.getById(cartItemEntity.getDatasetCollectionId()));
            }
        }
    }

    private Query buildQueryGetList(CommandGetListCartItem command) {
        Query query = new Query();
        Criteria criteria = new Criteria();
        List<Criteria> orCriteriaList = new ArrayList<>();
        List<Criteria> andCriteriaList = new ArrayList<>();

        andCriteriaList.add(Criteria.where("user_id").is(command.getUserId()));

        if (StringUtils.isNotBlank(command.getKeyword())) {
            CartEntity cartEntity = cartService.findByUserId(command.getUserId());
            if (cartEntity == null) {
                return null;
            }

            CommandGetListDatasetCollection commandGetListDatasetCollection = CommandGetListDatasetCollection.builder()
                    .ids(cartEntity.getDatasetCollectionIds())
                    .returnFields(List.of("id"))
                    .keyword(command.getKeyword())
                    .build();
            List<String> datasetCollectionEntityIds = datasetCollectionService.getList(commandGetListDatasetCollection)
                    .stream().map(DatasetCollectionEntity::getId).toList();

            if (CollectionUtils.isEmpty(datasetCollectionEntityIds)) {
                return null;
            }
            andCriteriaList.add(Criteria.where("dataset_collection_id").in(datasetCollectionEntityIds));
        }

        if (BooleanUtils.isTrue(command.isPurchased())) {
            andCriteriaList.add(Criteria.where("transaction_id").ne(null));
        } else {
            andCriteriaList.add(Criteria.where("transaction_id").is(null));
        }

        if (CollectionUtils.isNotEmpty(command.getIds())) {
            andCriteriaList.add(Criteria.where("id").in(command.getIds()));
        }

        if (CollectionUtils.isNotEmpty(orCriteriaList)) {
            criteria.orOperator(orCriteriaList);
        }
        if (CollectionUtils.isNotEmpty(andCriteriaList)) {
            criteria.andOperator(andCriteriaList);
        }

        query.addCriteria(criteria);
        if (CollectionUtils.isNotEmpty(command.getReturnFields())) {
            query.fields().include(Arrays.copyOf(command.getReturnFields().toArray(), command.getReturnFields().size(), String[].class));
        }
        return query;
    }

    @Override
    public List<CartItemEntity> getList(CommandGetListCartItem command) {
        if (StringUtils.isBlank(command.getUserId())) {
            log.error("[getList]: " + ExceptionConstant.missing_param);
            return null;
        }

        Query query = this.buildQueryGetList(command);
        if (query == null) {
            return null;
        }

        if (command.getSort() != null && command.getSort().getDirection() != null) {
            query.with(Sort.by(command.getSort().getDirection(), command.getSort().getField()));
        }

        return mongoTemplate.find(query, CartItemEntity.class);
    }

    @Override
    public List<CartItemEntity> updateMany(List<CartItemEntity> cartItemEntities) {
        return cartItemRepository.saveAll(cartItemEntities);
    }

    @Override
    public long countPurchasedByUserIdAndDatasetCollectionId(String userId, String datasetCollectionId) {
        List<Criteria> andCriteriaOpt = new ArrayList<>();
        andCriteriaOpt.add(Criteria.where("user_id").is(userId));
        andCriteriaOpt.add(Criteria.where("dataset_collection_id").is(datasetCollectionId));
        andCriteriaOpt.add(Criteria.where("transaction_id").ne(null));
        Criteria criteria = new Criteria();
        criteria.andOperator(andCriteriaOpt);
        Query query = new Query();
        query.addCriteria(criteria);

        return mongoTemplate.count(query, CartItemEntity.class);
    }
}























