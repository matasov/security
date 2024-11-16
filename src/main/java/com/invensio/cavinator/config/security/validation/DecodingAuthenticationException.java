package com.invensio.cavinator.config.security.validation;

public class DecodingAuthenticationException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public DecodingAuthenticationException(String message) {
        super(message);
    }

    public DecodingAuthenticationException(String message, Throwable cause) {
        super(message, cause);
    }

}
