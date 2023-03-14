package com.caovy2001.data_everywhere.command.cart_item;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CommandAPIAddCartItem {
    private String userId;
    private String datasetCollectionId;
}
