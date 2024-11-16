package com.invensio.cavinator.config.security.validation;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.stereotype.Component;

import com.invensio.cavinator.config.security.util.JwtUtil;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class AuthenticationJwtDecoder implements JwtDecoder {
    private final JwtUtil jwtUtil;

    @Override
    public Jwt decode(String token) throws JwtException {
        try {
            return NimbusJwtDecoder.withPublicKey(jwtUtil.loadPublicKey()).build()
                    .decode(token);
        } catch (NoSuchAlgorithmException e) {
            throw new DecodingAuthenticationException("NoSuchAlgorithmException in JWT parser", e);
        } catch (InvalidKeySpecException e) {
            throw new DecodingAuthenticationException("InvalidKeySpecException in JWT parser", e);
        } catch (IOException e) {
            throw new DecodingAuthenticationException("IOException in JWT parser", e);
        }
    }

}
