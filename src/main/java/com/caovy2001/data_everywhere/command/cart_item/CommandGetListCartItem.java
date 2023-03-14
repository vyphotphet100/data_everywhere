package com.caovy2001.data_everywhere.command.cart_item;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CommandGetListCartItem {
    private String userId;
    private int size = 0;
    private int page = 0;
    private String keyword;
    private List<String> ids;
    private List<String> returnFields;

    @Builder.Default
    private boolean purchased = true;

    @Builder.Default
    private boolean hasDatasetCollection = false;
}
