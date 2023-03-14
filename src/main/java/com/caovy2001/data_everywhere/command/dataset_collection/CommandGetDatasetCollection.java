package com.caovy2001.data_everywhere.command.dataset_collection;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CommandGetDatasetCollection {
    private String userId;
    private String id;
    @Builder.Default
    private boolean checkPurchased = false;

    @Builder.Default
    private boolean hasDatasetItems = false;

}
