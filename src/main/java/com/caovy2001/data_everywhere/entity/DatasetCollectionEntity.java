package com.caovy2001.data_everywhere.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.index.TextIndexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Document("dataset_collection")
public class DatasetCollectionEntity {
    @Id
    private String id;

    @Field("name")
    private String name;

    @Field("short_description")
    private String shortDescription;

    @Field("description")
    private String description;

    @Field("picture")
    private String picture;

    @Field("preview")
    private String preview;

    @Field("amount")
    private long amount;

    @Field("download_path")
    private String downloadPath;

    @Field("dataset_category_id")
    private String datasetCategoryId;

    @Transient
    private Boolean purchased;

    @Transient
    private List<DatasetItemEntity> datasetItems;
}














