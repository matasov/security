package com.invensio.cavinator.config.security.util;

import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.invensio.cavinator.config.security.login.CustomAuthenticationToken;
import com.invensio.cavinator.config.security.util.server.CompanyServerSettingsModel;
import com.invensio.cavinator.config.security.util.user.CompanyUserAuthenticationToken;
import com.invensio.cavinator.config.security.util.user.UserGrantedAuthority;
import com.invensio.cavinator.db.models.security.model.ContactAuthDto;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JOSEObjectType;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jose.crypto.RSASSAVerifier;
import com.nimbusds.jose.shaded.gson.internal.LinkedTreeMap;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;

import lombok.RequiredArgsConstructor;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import java.util.Calendar;

@Component
@RequiredArgsConstructor
public class JoseJwtUtil implements JwtUtil {
    private final KeyLoader keyLoader;
    private final ObjectMapper objectMapper;

    @Override
    public String encodeAccessToken(Authentication authResult, CompanyServerSettingsModel companySettings)
            throws NoSuchAlgorithmException, InvalidKeySpecException, IOException, JOSEException {
        CustomAuthenticationToken customToken = (CustomAuthenticationToken) authResult;
        ContactAuthDetails contactAuthDetails = (ContactAuthDetails) customToken.getDetails();
        return encodeAccessToken(contactAuthDetails, companySettings);
    }

    @Override
    public String encodeAccessToken(ContactAuthDetails contactAuthDetails, CompanyServerSettingsModel companySettings)
            throws NoSuchAlgorithmException, InvalidKeySpecException, IOException, JOSEException {
        JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
                .subject(contactAuthDetails.getUsername())
                .issuer(companySettings.getCompanyName())
                .expirationTime(calculateExpirationDate(companySettings.getAccessTokenExpirationTime()))
                .claim("roles", contactAuthDetails.getAuthorities())
                .claim("currentOwnerRecordId", contactAuthDetails.getCurrentOwnerRecordId().toString())
                .claim("domainKey", contactAuthDetails.getDomain())
                .claim("authLink", contactAuthDetails.getAuthLink())
                .claim("user", contactAuthDetails.getUser())
                .build();
        return signToken(claimsSet);
    }

    @Override
    public String encodeRefreshToken(Authentication authResult, CompanyServerSettingsModel companySettings)
            throws NoSuchAlgorithmException, InvalidKeySpecException, IOException, JOSEException {
        CustomAuthenticationToken customToken = (CustomAuthenticationToken) authResult;
        ContactAuthDetails contactAuthDetails = (ContactAuthDetails) customToken.getDetails();
        JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
                .subject(String.valueOf(authResult.getPrincipal()))
                .issuer(companySettings.getCompanyName())
                .claim("currentOwnerRecordId", contactAuthDetails.getCurrentOwnerRecordId().toString())
                .claim("domainKey", contactAuthDetails.getDomain())
                .claim("dynamicRoleId", customToken.getAuthRequest().getDynamicRoleId())
                .expirationTime(calculateExpirationDate(companySettings.getRefreshTokenExpirationTime()))
                .build();
        return signToken(claimsSet);
    }

    private String signToken(JWTClaimsSet claimsSet)
            throws NoSuchAlgorithmException, InvalidKeySpecException, IOException, JOSEException {
        RSAPrivateKey privateKey = keyLoader.loadPrivateKey();
        SignedJWT signedJWT = new SignedJWT(
                new JWSHeader.Builder(JWSAlgorithm.RS256).type(JOSEObjectType.JWT).build(),
                claimsSet);

        JWSSigner signer = new RSASSASigner(privateKey);
        signedJWT.sign(signer);

        return signedJWT.serialize();
    }

    @Override
    public boolean validateToken(String token)
            throws NoSuchAlgorithmException, InvalidKeySpecException, IOException, ParseException, JOSEException {
        RSAPublicKey publicKey = keyLoader.loadPublicKey();
        SignedJWT signedJWT = SignedJWT.parse(token);
        JWSVerifier verifier = new RSASSAVerifier(publicKey);
        return signedJWT.verify(verifier);
    }

    @Override
    public String getUsernameFromToken(String token) throws ParseException {
        SignedJWT signedJWT = SignedJWT.parse(token);
        return signedJWT.getJWTClaimsSet().getSubject();
    }

    @Override
    public JWTClaimsSet getClaimsFromToken(String token) throws ParseException {
        SignedJWT signedJWT = SignedJWT.parse(token);
        return signedJWT.getJWTClaimsSet();
    }

    @Override
    public RSAPublicKey loadPublicKey() throws NoSuchAlgorithmException, InvalidKeySpecException, IOException {
        return keyLoader.loadPublicKey();
    }

    public Date calculateExpirationDate(Integer expirationTime) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.SECOND, expirationTime);
        return calendar.getTime();
    }

    @Override
    public AbstractAuthenticationToken retreiveAuthToken(JWTClaimsSet claims) throws ParseException {
        ContactAuthDto user = objectMapper.convertValue(claims.getClaim("user"), ContactAuthDto.class);
        UUID currentOwnerRecordId = UUID.fromString(String.valueOf(claims.getClaim("currentOwnerRecordId")));
        String domain = (String) claims.getClaim("domainKey");
        String authLink = (String) claims.getClaim("authLink");
        ContactAuthDetails userDetails = new ContactAuthDetails(user, currentOwnerRecordId, domain, authLink);
        String username = claims.getSubject();
        List<LinkedTreeMap<String, Object>> roles = (List<LinkedTreeMap<String, Object>>) claims.getClaim("roles");
        List<GrantedAuthority> authorities = roles.stream().map(this::mapToGrantedAuthority).toList();
        CompanyUserAuthenticationToken customToken = new CompanyUserAuthenticationToken(username, authorities);
        customToken.setDetails(userDetails);
        return customToken;
    }

    private GrantedAuthority mapToGrantedAuthority(LinkedTreeMap<String, Object> grantsItem) {
        return new UserGrantedAuthority(UUID.fromString(String.valueOf(grantsItem.get("id"))),
                grantsItem.get("name").toString());
    }

    @Override
    public String retreiveSignedAuthToken(String token)
            throws ParseException, NoSuchAlgorithmException, InvalidKeySpecException, IOException, JOSEException {
        JWTClaimsSet claimsSet = getClaimsFromToken(token);
        return signToken(claimsSet);
    }

}
