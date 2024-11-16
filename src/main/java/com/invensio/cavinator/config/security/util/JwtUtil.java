package com.invensio.cavinator.config.security.util;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.text.ParseException;

import com.invensio.cavinator.config.security.util.server.CompanyServerSettingsModel;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jwt.JWTClaimsSet;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.Authentication;

public interface JwtUtil {
        String encodeAccessToken(Authentication authResult, CompanyServerSettingsModel companySettings)
                        throws NoSuchAlgorithmException, InvalidKeySpecException, IOException, JOSEException;

        String encodeAccessToken(ContactAuthDetails contactAuthDetails, CompanyServerSettingsModel companySettings)
                        throws NoSuchAlgorithmException, InvalidKeySpecException, IOException, JOSEException;

        String encodeRefreshToken(Authentication authResult, CompanyServerSettingsModel companySettings)
                        throws NoSuchAlgorithmException, InvalidKeySpecException, IOException, JOSEException;

        AbstractAuthenticationToken retreiveAuthToken(JWTClaimsSet claims) throws ParseException;

        String retreiveSignedAuthToken(String token) throws ParseException, NoSuchAlgorithmException,
                        InvalidKeySpecException, IOException, JOSEException;

        boolean validateToken(String token)
                        throws NoSuchAlgorithmException, InvalidKeySpecException, IOException, ParseException,
                        JOSEException;

        String getUsernameFromToken(String token) throws ParseException;

        JWTClaimsSet getClaimsFromToken(String token) throws ParseException;

        RSAPublicKey loadPublicKey() throws NoSuchAlgorithmException, InvalidKeySpecException, IOException;
}
