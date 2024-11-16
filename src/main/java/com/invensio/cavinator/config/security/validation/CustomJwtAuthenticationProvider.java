package com.invensio.cavinator.config.security.validation;

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.BearerTokenAuthenticationToken;
import org.springframework.stereotype.Component;
import com.invensio.cavinator.config.security.util.user.CompanyUserAuthenticationToken;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class CustomJwtAuthenticationProvider implements AuthenticationProvider {
    private final JwtDecoder jwtDecoder;
    private final AuthenticationJwtConverter authenticationJwtConverter;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        validateAuthentication(authentication);
        String token = ((BearerTokenAuthenticationToken) authentication).getToken();
        Jwt jwt = jwtDecoder.decode(token);
        return authenticationJwtConverter.convert(jwt);
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return CompanyUserAuthenticationToken.class.isAssignableFrom(authentication);
    }

    private void validateAuthentication(Authentication authentication) {
        if (authentication == null) {
            throw new DecodingAuthenticationException("Authentication is null");
        }
        if (authentication.getPrincipal() == null) {
            throw new DecodingAuthenticationException("Principal is null");
        }
        if (!(authentication instanceof BearerTokenAuthenticationToken)) {
            throw new DecodingAuthenticationException("Unsupported authentication type");
        }
    }

}
