package com.caovy2001.data_everywhere.repository;

import com.caovy2001.data_everywhere.entity.DatasetItemEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface DatasetItemRepository extends MongoRepository<DatasetItemEntity, String> {
    List<DatasetItemEntity> findAllByDatasetCollectionId(String datasetCollectionId);
}
