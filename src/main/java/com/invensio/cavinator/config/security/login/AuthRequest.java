package com.invensio.cavinator.config.security.login;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthRequest {
    private String domainKey;
    private String login;
    private String password;
    private String dynamicRoleId;
    private String confirmationCode;
}
