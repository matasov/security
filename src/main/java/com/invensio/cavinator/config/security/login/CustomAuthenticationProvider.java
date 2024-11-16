package com.invensio.cavinator.config.security.login;

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class CustomAuthenticationProvider implements AuthenticationProvider {

    private final UserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        CustomAuthenticationToken customToken = (CustomAuthenticationToken) authentication;
        AuthRequest authRequest = customToken.getAuthRequest();

        UserDetails userDetails = userDetailsService.loadUserByUsername(authRequest.getLogin());

        if (userDetails == null || !passwordMatches(authRequest.getPassword(), userDetails.getPassword())) {
            throw new BadCredentialsException("Invalid credentials");
        }

        CustomAuthenticationToken authenticatedToken = new CustomAuthenticationToken(authRequest);
        authenticatedToken.setAuthenticated(true);
        authenticatedToken.setDetails(userDetails);

        return authenticatedToken;
    }

    private boolean passwordMatches(String rawPassword, String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return CustomAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
