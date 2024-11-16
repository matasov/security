package com.invensio.cavinator.config.security.login;

import org.springframework.security.authentication.AbstractAuthenticationToken;

public class CustomAuthenticationToken extends AbstractAuthenticationToken {
    private final AuthRequest authRequest;

    public CustomAuthenticationToken(AuthRequest authRequest) {
        super(null);
        this.authRequest = authRequest;
        setAuthenticated(false);
    }

    @Override
    public Object getCredentials() {
        return authRequest.getPassword();
    }

    @Override
    public Object getPrincipal() {
        return authRequest.getLogin();
    }

    public AuthRequest getAuthRequest() {
        return authRequest;
    }

}
