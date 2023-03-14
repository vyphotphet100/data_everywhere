package com.caovy2001.data_everywhere.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Document("user")
public class UserEntity {
    @Id
    private String id;

    @Indexed
    @Field("username")
    private String username;

    @Field("password")
    private String password;

    @Field("full_name")
    private String fullName;

    @Field("birthday")
    private String birthday;

    @Field("address")
    private String address;

    @Field("avatar")
    private String avatar;

    @Transient
    private String token;
}
