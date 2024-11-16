package com.invensio.cavinator.config.security.util.user;

import java.util.List;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

public class CompanyUserAuthenticationToken extends AbstractAuthenticationToken {

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        CompanyUserAuthenticationToken that = (CompanyUserAuthenticationToken) obj;
        return username.equals(that.username);
    }

    private String username;

    public CompanyUserAuthenticationToken(String username, List<GrantedAuthority> authorities) {
        super(authorities);
        this.username = username;
        setAuthenticated(true);
    }

    @Override
    public Object getCredentials() {
        return null;
    }

    @Override
    public Object getPrincipal() {
        return username;
    }

}
