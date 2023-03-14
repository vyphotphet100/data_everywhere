package com.caovy2001.data_everywhere.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.Index;

import javax.annotation.PostConstruct;

@Configuration
public class MongoConfig {
    @Autowired
    private MongoTemplate mongoTemplate;

    @PostConstruct
    public void initIndexes() {
        mongoTemplate.indexOps("user")
                .ensureIndex(new Index()
                        .on("username", Sort.Direction.ASC));

        mongoTemplate.indexOps("transaction")
                .ensureIndex(new Index()
                        .on("user_id", Sort.Direction.ASC));

        mongoTemplate.indexOps("dataset_item")
                .ensureIndex(new Index()
                        .on("dataset_collection_id", Sort.Direction.ASC));

        mongoTemplate.indexOps("cart_item")
                .ensureIndex(new Index()
                        .on("user_id", Sort.Direction.ASC));

        mongoTemplate.indexOps("cart")
                .ensureIndex(new Index()
                        .on("user_id", Sort.Direction.ASC));
    }
}
