package com.invensio.cavinator.config.security.util;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.bouncycastle.openssl.PEMKeyPair;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;
import lombok.RequiredArgsConstructor;

import java.security.*;
import java.security.spec.*;
import org.springframework.core.io.*;

@Component
@RequiredArgsConstructor
public class LocalKeyLoader implements KeyLoader {
    private final ResourceLoader resourceLoader;

    @Value("${jwt.private-key-location}")
    private String privateKeyLocation;

    @Value("${jwt.public-key-location}")
    private String publicKeyLocation;

    @Override
    public RSAPrivateKey loadPrivateKey() throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
        Resource resource = resourceLoader.getResource(privateKeyLocation);
        try (InputStreamReader keyReader = new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8);
                PEMParser pemParser = new PEMParser(keyReader)) {
            Object object = pemParser.readObject();
            JcaPEMKeyConverter converter = new JcaPEMKeyConverter();

            if (object instanceof PEMKeyPair) {
                // Handle PKCS#1 format
                PEMKeyPair pemKeyPair = (PEMKeyPair) object;
                KeyPair keyPair = converter.getKeyPair(pemKeyPair);
                return (RSAPrivateKey) keyPair.getPrivate();
            } else if (object instanceof PrivateKeyInfo) {
                // Handle PKCS#8 format
                return (RSAPrivateKey) converter.getPrivateKey((PrivateKeyInfo) object);
            } else {
                throw new IllegalArgumentException("Unsupported private key format");
            }
        }
    }

    @Override
    public RSAPublicKey loadPublicKey() throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
        Resource resource = resourceLoader.getResource(publicKeyLocation);
        try (InputStreamReader keyReader = new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8);
                PEMParser pemParser = new PEMParser(keyReader)) {

            SubjectPublicKeyInfo publicKeyInfo = SubjectPublicKeyInfo.getInstance(pemParser.readObject());
            JcaPEMKeyConverter converter = new JcaPEMKeyConverter();
            return (RSAPublicKey) converter.getPublicKey(publicKeyInfo);
        }
    }

}
