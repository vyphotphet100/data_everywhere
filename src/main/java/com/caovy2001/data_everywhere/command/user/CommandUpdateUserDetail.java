package com.caovy2001.data_everywhere.command.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CommandUpdateUserDetail {
    private String userId;
    private String fullName;
    private String birthday;
    private String address;
    private String currentPassword;
    private String newPassword;
    private String confirmPassword;
}
