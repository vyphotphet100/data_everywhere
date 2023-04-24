package com.caovy2001.data_everywhere.command.dataset_collection;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CommandGetListDatasetCollection {
    private String userId;
    private int page = 0;
    private int size = 0;
    private String keyword;
    private List<String> ids;
    private String datasetCategoryId;
    @Builder.Default
    private boolean purchased = false;

    private List<String> returnFields;
}
