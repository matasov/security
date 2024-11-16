package com.invensio.cavinator.config.security.util.server;

import java.util.UUID;

import org.springframework.stereotype.Service;

@Service
public class ConstCompanyServerSettingsService implements CompanyServerSettingsService {
    @Override
    public CompanyServerSettingsModel getCompanyServerSettings(String domainKey) {
        return new CompanyServerSettingsModel(UUID.fromString("b0b08f3d-7950-424a-8b98-c7a4eef2a26c"), "TestCompany",
                60, 3600);
    }

}
