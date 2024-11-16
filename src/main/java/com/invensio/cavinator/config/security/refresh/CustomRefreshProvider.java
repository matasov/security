package com.invensio.cavinator.config.security.refresh;

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

import com.invensio.cavinator.config.security.login.CustomAuthenticationToken;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class CustomRefreshProvider implements AuthenticationProvider {

    private final UserDetailsService userDetailsService;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        CustomRefreshToken customToken = (CustomRefreshToken) authentication;
        UserDetails userDetails = userDetailsService.loadUserByUsername(String.valueOf(customToken.getPrincipal()));
        CustomAuthenticationToken authenticatedToken = new CustomAuthenticationToken(customToken.getAuthRequest());
        authenticatedToken.setAuthenticated(true);
        authenticatedToken.setDetails(userDetails);

        return authenticatedToken;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return CustomAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
