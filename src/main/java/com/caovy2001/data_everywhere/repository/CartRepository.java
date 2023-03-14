package com.caovy2001.data_everywhere.repository;

import com.caovy2001.data_everywhere.entity.CartEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface CartRepository extends MongoRepository<CartEntity, String> {
    CartEntity findByUserId(String userId);
}
