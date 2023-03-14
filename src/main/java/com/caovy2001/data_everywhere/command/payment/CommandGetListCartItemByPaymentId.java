package com.caovy2001.data_everywhere.command.payment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommandGetListCartItemByPaymentId {
    private String userId;
    private String paymentId;
}
