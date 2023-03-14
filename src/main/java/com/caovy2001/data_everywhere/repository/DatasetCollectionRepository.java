package com.caovy2001.data_everywhere.repository;

import com.caovy2001.data_everywhere.entity.DatasetCollectionEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface DatasetCollectionRepository extends MongoRepository<DatasetCollectionEntity, String> {
    long countById(String id);
}
