package com.caovy2001.data_everywhere.model.payment;

import com.caovy2001.data_everywhere.entity.CartItemEntity;
import com.caovy2001.data_everywhere.entity.TransactionEntity;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PaymentPaypalResponse {
    private String approvalLink;
    private TransactionEntity transaction;
    private List<CartItemEntity> cartItems;
}
