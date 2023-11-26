package com.caovy2001.data_everywhere.repository;

import com.caovy2001.data_everywhere.entity.UserEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UserRepository extends MongoRepository<UserEntity, String> {
    UserEntity findByUsernameAndPassword(String username, String password);
    UserEntity findByUsername(String username);
    long countByUsername(String username);
}
