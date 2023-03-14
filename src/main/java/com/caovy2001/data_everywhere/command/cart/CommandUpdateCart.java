package com.caovy2001.data_everywhere.command.cart;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommandUpdateCart {
    private String id;
    private List<String> datasetCollectionIds;
    private List<String> cartItemIds;
}
