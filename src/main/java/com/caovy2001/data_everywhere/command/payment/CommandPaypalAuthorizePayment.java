package com.caovy2001.data_everywhere.command.payment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CommandPaypalAuthorizePayment {
    private String userId;
    private List<String> cartItemIds;
}
