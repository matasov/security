package com.invensio.cavinator.config.security.util.server;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CompanyServerSettingsModel {
    private UUID companyId;
    private String companyName;
    private Integer accessTokenExpirationTime;
    private Integer refreshTokenExpirationTime;
}
