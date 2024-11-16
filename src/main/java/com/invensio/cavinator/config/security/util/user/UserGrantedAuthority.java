package com.invensio.cavinator.config.security.util.user;

import org.springframework.security.core.GrantedAuthority;
import java.util.UUID;

public class UserGrantedAuthority implements GrantedAuthority {

    private UUID id;
    private String name;

    public UserGrantedAuthority(UUID id, String name) {
        this.id = id;
        this.name = name;
    }

    @Override
    public String getAuthority() {
        return """
                {
                    "id": "%s",
                    "name": "%s"
                }
                """.formatted(id, name);
    }

}
