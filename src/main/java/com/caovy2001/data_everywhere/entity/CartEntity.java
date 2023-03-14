package com.caovy2001.data_everywhere.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@Document("cart")
public class CartEntity {
    @Id
    private String id;

    @Field("user_id")
    @Indexed
    private String userId;

    @Field("dataset_collection_ids")
    private List<String> datasetCollectionIds;

    @Field("cart_item_ids")
    private List<String> cartItemIds;
}
