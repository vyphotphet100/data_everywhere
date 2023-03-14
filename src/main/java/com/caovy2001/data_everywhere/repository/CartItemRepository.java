package com.caovy2001.data_everywhere.repository;

import com.caovy2001.data_everywhere.entity.CartItemEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface CartItemRepository extends MongoRepository<CartItemEntity, String> {
    long countByUserIdAndDatasetCollectionIdAndTransactionId(String userId, String datasetCollectionId, String transactionId);
}
