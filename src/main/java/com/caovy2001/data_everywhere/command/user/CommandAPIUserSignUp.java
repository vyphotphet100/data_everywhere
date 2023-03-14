package com.caovy2001.data_everywhere.command.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CommandAPIUserSignUp {
    private String username;
    private String password;
    private String confirmPassword;
    private String fullName;
    private String birthday;
    private String address;

}
