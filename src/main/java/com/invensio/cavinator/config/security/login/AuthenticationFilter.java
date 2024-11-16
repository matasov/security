package com.invensio.cavinator.config.security.login;

import java.io.IOException;
import java.io.PrintWriter;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.invensio.cavinator.config.security.util.JwtUtil;
import com.invensio.cavinator.config.security.util.server.CompanyServerSettingsModel;
import com.invensio.cavinator.config.security.util.server.CompanyServerSettingsService;
import com.nimbusds.jose.JOSEException;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AuthenticationFilter extends AbstractAuthenticationProcessingFilter {

    private final JwtUtil jwtUtil;
    private final CompanyServerSettingsService companyServerSettingsService;

    public AuthenticationFilter(AuthenticationManager authenticationManager, JwtUtil jwtUtil,
            CompanyServerSettingsService companyServerSettingsService) {
        super("/auth/login");
        this.jwtUtil = jwtUtil;
        this.companyServerSettingsService = companyServerSettingsService;
        setAuthenticationManager(authenticationManager);
        setAuthenticationFailureHandler(new CustomAuthenticationFailureHandler());
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationException, IOException, ServletException {
        AuthRequest authRequest = new ObjectMapper().readValue(request.getInputStream(), AuthRequest.class);
        CustomAuthenticationToken authToken = new CustomAuthenticationToken(authRequest);
        return getAuthenticationManager().authenticate(authToken);
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
            Authentication authResult) throws IOException, ServletException {
        CustomAuthenticationToken authToken = (CustomAuthenticationToken) authResult;
        AuthRequest authRequest = authToken.getAuthRequest();
        CompanyServerSettingsModel settings = companyServerSettingsService
                .getCompanyServerSettings(authRequest.getDomainKey());
        String accessToken = generateBearerToken(authResult, settings);
        String refreshToken = generateRefreshToken(authResult, settings);

        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType("application/json");

        PrintWriter writer = response.getWriter();
        writer.write("{\"access_token\": \"" + accessToken + "\", \"refresh_token\": \"" + refreshToken + "\"}");
        writer.flush();
    }

    private String generateBearerToken(Authentication authResult, CompanyServerSettingsModel settings) {
        try {
            return jwtUtil.encodeAccessToken(authResult, settings);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException | IOException | JOSEException e) {
            e.printStackTrace();
            return null;
        }
    }

    private String generateRefreshToken(Authentication authResult, CompanyServerSettingsModel settings) {
        try {
            return jwtUtil.encodeRefreshToken(authResult, settings);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException | IOException | JOSEException e) {
            e.printStackTrace();
            return null;
        }
    }

}
