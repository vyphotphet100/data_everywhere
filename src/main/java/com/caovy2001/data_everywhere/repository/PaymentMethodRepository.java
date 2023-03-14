package com.caovy2001.data_everywhere.repository;

import com.caovy2001.data_everywhere.entity.PaymentMethodEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface PaymentMethodRepository extends MongoRepository<PaymentMethodEntity, String> {
    PaymentMethodEntity findByCode(String code);
}
