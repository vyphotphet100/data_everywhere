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

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Document("dataset_item")
public class DatasetItemEntity {
    @Id
    private String id;

    @Field("dataset_collection_id")
    @Indexed
    private String datasetCollectionId;

    @Field("name")
    private String name;

    @Field("path")
    private String path;
}
