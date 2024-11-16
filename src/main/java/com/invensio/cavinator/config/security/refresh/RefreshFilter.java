package com.invensio.cavinator.config.security.refresh;

import java.io.IOException;
import java.io.PrintWriter;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.text.ParseException;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.invensio.cavinator.config.security.login.AuthRequest;
import com.invensio.cavinator.config.security.login.CustomAuthenticationFailureHandler;
import com.invensio.cavinator.config.security.login.CustomAuthenticationToken;
import com.invensio.cavinator.config.security.util.JwtUtil;
import com.invensio.cavinator.config.security.util.server.CompanyServerSettingsModel;
import com.invensio.cavinator.config.security.util.server.CompanyServerSettingsService;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jwt.JWTClaimsSet;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RefreshFilter extends AbstractAuthenticationProcessingFilter {
    private final JwtUtil jwtUtil;
    private final CompanyServerSettingsService companyServerSettingsService;
    private final ObjectMapper objectMapper;

    public RefreshFilter(AuthenticationManager authenticationManager, JwtUtil jwtUtil,
            CompanyServerSettingsService companyServerSettingsService, ObjectMapper objectMapper) {
        super("/auth/refresh");
        this.jwtUtil = jwtUtil;
        this.companyServerSettingsService = companyServerSettingsService;
        this.objectMapper = objectMapper;
        setAuthenticationManager(authenticationManager);
        setAuthenticationFailureHandler(new CustomAuthenticationFailureHandler());
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationException, IOException, ServletException {
        RefreshRequest refreshTokenRequest = objectMapper.readValue(request.getInputStream(), RefreshRequest.class);
        String refreshToken = refreshTokenRequest.getRefresh();

        try {
            if (jwtUtil.validateToken(refreshToken)) {
                JWTClaimsSet claims = jwtUtil.getClaimsFromToken(refreshToken);
                String username = claims.getSubject();
                if (username != null) {
                    String domainKey = claims.getStringClaim("domainKey");
                    String currentOwnerRecordId = claims.getStringClaim("currentOwnerRecordId");
                    String dynamicRoleId = claims.getStringClaim("dynamicRoleId");
                    AuthRequest authRequest = new AuthRequest(domainKey, username, null, dynamicRoleId, null);
                    return getAuthenticationManager()
                            .authenticate(new CustomRefreshToken(username, null, authRequest, currentOwnerRecordId));
                }
            }
        } catch (NoSuchAlgorithmException | InvalidKeySpecException | AuthenticationException | IOException
                | ParseException | JOSEException e) {
            e.printStackTrace();
            throw new ServletException("Invalid refresh token");
        }
        throw new ServletException("Invalid refresh token");
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
