package com.caovy2001.data_everywhere.repository;

import com.caovy2001.data_everywhere.entity.DatasetCategoryEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DatasetCategoryRepository extends MongoRepository<DatasetCategoryEntity, String> {
}
