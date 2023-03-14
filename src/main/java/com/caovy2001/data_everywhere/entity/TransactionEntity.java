package com.caovy2001.data_everywhere.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Document("transaction")
public class TransactionEntity {
    @Id
    private String id;

    @Field("user_id")
    @Indexed
    private String userId;

    @Field("payment_method_id")
    private String paymentMethodId;

    @Field("amount")
    private Long amount;
}
