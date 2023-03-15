package com.caovy2001.data_everywhere.command.cart_item;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommandRemoveCartItem {
    private String userId;
    private List<String> cartItemIds;
}
