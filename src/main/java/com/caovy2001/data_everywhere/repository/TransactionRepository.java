package com.caovy2001.data_everywhere.repository;

import com.caovy2001.data_everywhere.entity.TransactionEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface TransactionRepository extends MongoRepository<TransactionEntity, String> {
}
