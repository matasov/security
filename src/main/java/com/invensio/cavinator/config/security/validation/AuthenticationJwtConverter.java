package com.invensio.cavinator.config.security.validation;

import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

import com.invensio.cavinator.config.security.util.JwtUtil;
import com.nimbusds.jwt.JWTClaimsSet;

import lombok.RequiredArgsConstructor;

import java.text.ParseException;

import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.AbstractAuthenticationToken;

@Component
@RequiredArgsConstructor
public class AuthenticationJwtConverter implements Converter<Jwt, AbstractAuthenticationToken> {
    private final JwtUtil jwtUtil;

    @Override
    public AbstractAuthenticationToken convert(@NonNull Jwt source) {
        try {
            JWTClaimsSet claims = jwtUtil.getClaimsFromToken(source.getTokenValue());
            return jwtUtil.retreiveAuthToken(claims);
        } catch (ParseException e) {
            throw new DecodingAuthenticationException("Error jwt token processing: " + e.getMessage());
        }

    }
}
