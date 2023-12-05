package com.caovy2001.data_everywhere.service.dataset_collection;

import com.caovy2001.data_everywhere.command.cart_item.CommandGetListCartItem;
import com.caovy2001.data_everywhere.command.dataset_collection.CommandGetDatasetCollection;
import com.caovy2001.data_everywhere.command.dataset_collection.CommandGetListDatasetCollection;
import com.caovy2001.data_everywhere.command.user.CommandGetListUser;
import com.caovy2001.data_everywhere.constant.Constant;
import com.caovy2001.data_everywhere.constant.ExceptionConstant;
import com.caovy2001.data_everywhere.entity.CartItemEntity;
import com.caovy2001.data_everywhere.entity.DatasetCollectionEntity;
import com.caovy2001.data_everywhere.entity.UserEntity;
import com.caovy2001.data_everywhere.model.FileResponse;
import com.caovy2001.data_everywhere.model.pagination.Paginated;
import com.caovy2001.data_everywhere.repository.DatasetCollectionRepository;
import com.caovy2001.data_everywhere.service.BaseService;
import com.caovy2001.data_everywhere.service.cart_item.ICartItemService;
import com.caovy2001.data_everywhere.service.dataset_item.IDatasetItemService;
import com.caovy2001.data_everywhere.service.user.IUserService;
import com.caovy2001.data_everywhere.service.user.enumeration.UserServicePack;
import lombok.NonNull;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

@Component
public class DatasetCollectionService extends BaseService implements IDatasetCollectionService, IDatasetCollectionServiceAPI {
    @Autowired
    private DatasetCollectionRepository datasetCollectionRepository;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private ICartItemService cartItemService;

    @Autowired
    private IDatasetItemService datasetItemService;

    @Autowired
    private IUserService userService;

    @Override
    public Paginated<DatasetCollectionEntity> getPaginatedList(@NonNull CommandGetListDatasetCollection command) throws Exception {
        if (command.getPage() <= 0 || command.getSize() < 0) {
            throw new Exception("invalid_page_or_size");
        }

        Query query = this.buildQueryGetList(command);
        if (query == null) {
            return new Paginated<>(new ArrayList<>(), command.getPage(), command.getSize(), 0);
        }

        long total = mongoTemplate.count(query, DatasetCollectionEntity.class);
        if (total == 0L) {
            return new Paginated<>(new ArrayList<>(), command.getPage(), command.getSize(), 0);
        }

        query.with(PageRequest.of(command.getPage() - 1, command.getSize()));
        List<DatasetCollectionEntity> datasetCollectionEntities = mongoTemplate.find(query, DatasetCollectionEntity.class);
        return new Paginated<>(datasetCollectionEntities, command.getPage(), command.getSize(), total);
    }

    private Query buildQueryGetList(@NonNull CommandGetListDatasetCollection command) {
        Query query = new Query();
        Criteria criteria = new Criteria();
        List<Criteria> orCriteriaList = new ArrayList<>();
        List<Criteria> andCriteriaList = new ArrayList<>();

        if (StringUtils.isNotBlank(command.getKeyword())) {
            orCriteriaList.add(Criteria.where("name").regex(command.getKeyword(), "i"));
            orCriteriaList.add(Criteria.where("short_description").regex(command.getKeyword(), "i"));
        }

        if (BooleanUtils.isTrue(command.isPurchased())) {
            if (StringUtils.isNotBlank(command.getUserId())) {
                List<CartItemEntity> cartItemEntities = cartItemService.getList(CommandGetListCartItem.builder()
                        .userId(command.getUserId())
                        .purchased(true)
                        .build());
                if (CollectionUtils.isEmpty(cartItemEntities)) {
                    return null;
                }

                List<String> datasetCollectionIds = new ArrayList<>(new HashSet<>(cartItemEntities.stream().map(CartItemEntity::getDatasetCollectionId).toList()));
                if (CollectionUtils.isEmpty(datasetCollectionIds)) {
                    return null;
                }

                andCriteriaList.add(Criteria.where("id").in(datasetCollectionIds));
            }
        }

        if (StringUtils.isNotBlank(command.getDatasetCategoryId())) {
            andCriteriaList.add(Criteria.where("dataset_category_id").is(command.getDatasetCategoryId()));
        }

        if (CollectionUtils.isNotEmpty(orCriteriaList)) {
            criteria.orOperator(orCriteriaList);
        }

        if (CollectionUtils.isNotEmpty(andCriteriaList)) {
            criteria.andOperator(andCriteriaList);
        }
        query.addCriteria(criteria);
        return query;
    }

    @Override
    public DatasetCollectionEntity getById(CommandGetDatasetCollection command) throws Exception {
        if (StringUtils.isBlank(command.getId())) {
            throw new Exception(ExceptionConstant.missing_param);
        }

        DatasetCollectionEntity datasetCollectionEntity = datasetCollectionRepository.findById(command.getId()).orElse(null);
        if (datasetCollectionEntity == null) {
            throw new Exception("dataset_not_exist");
        }

        if (BooleanUtils.isTrue(command.isCheckPurchased())) {
            if (StringUtils.isNotBlank(command.getUserId())) {
                List<UserEntity> userEntities = userService.getList(CommandGetListUser.builder()
                        .id(command.getUserId())
                        .build());
                if (CollectionUtils.isEmpty(userEntities)) {
                    throw new Exception(ExceptionConstant.error_occur);
                }

                if (UserServicePack.PREMIUM.equals(userEntities.get(0).getCurrentServicePack())) {
                    datasetCollectionEntity.setPurchased(true);
                } else {
                    long _countPurchased = cartItemService.countPurchasedByUserIdAndDatasetCollectionId(command.getUserId(), command.getId());
                    if (_countPurchased > 0) {
                        datasetCollectionEntity.setPurchased(true);
                    }
                }
            }
        }

        this.setView(datasetCollectionEntity, command);
        return datasetCollectionEntity;
    }

    private void setView(@NonNull DatasetCollectionEntity datasetCollectionEntity, @NonNull CommandGetDatasetCollection command) {
        if (BooleanUtils.isTrue(command.isHasDatasetItems()) &&
                BooleanUtils.isTrue(datasetCollectionEntity.getPurchased())) {
            datasetCollectionEntity.setDatasetItems(datasetItemService.findByDatasetCollectionId(datasetCollectionEntity.getId()));
        }
    }

    @Override
    public DatasetCollectionEntity getById(String datasetCollectionId) {
        return datasetCollectionRepository.findById(datasetCollectionId).orElse(null);
    }

    @Override
    public FileResponse getPreviewById(String id) throws Exception {
        if (StringUtils.isBlank(id)) {
            throw new Exception("id_null");
        }

        DatasetCollectionEntity datasetCollectionEntity = datasetCollectionRepository.findById(id).orElse(null);
        if (datasetCollectionEntity == null) {
            throw new Exception("dataset_null");
        }

        if (StringUtils.isBlank(datasetCollectionEntity.getPreview())) {
            throw new Exception("path_preview_file_null");
        }

        return FileResponse.builder()
                .path(Constant.prefixPathFile + datasetCollectionEntity.getPreview())
                .build();
    }

    @Override
    public long countById(String id) {
        return datasetCollectionRepository.countById(id);
    }

    @Override
    public List<DatasetCollectionEntity> getList(@NonNull CommandGetListDatasetCollection command) {
        Query query = new Query();
        Criteria criteria = new Criteria();
        List<Criteria> orCriteriaList = new ArrayList<>();
        List<Criteria> andCriteriaList = new ArrayList<>();

        if (StringUtils.isNotBlank(command.getKeyword())) {
            orCriteriaList.add(Criteria.where("name").regex(command.getKeyword(), "i"));
            orCriteriaList.add(Criteria.where("short_description").regex(command.getKeyword(), "i"));
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

        return mongoTemplate.find(query, DatasetCollectionEntity.class);
    }
}
