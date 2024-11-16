package com.invensio.cavinator.config.security.login;

import org.springframework.security.web.authentication.AuthenticationFailureHandler;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.AuthenticationException;
import java.io.IOException;
import java.io.PrintWriter;

public class CustomAuthenticationFailureHandler implements AuthenticationFailureHandler {

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
            AuthenticationException exception) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");

        String errorMessage;

        if (exception instanceof BadCredentialsException) {
            errorMessage = "Invalid username or password";
        } else if (exception instanceof LockedException) {
            errorMessage = "Account locked";
        } else if (exception instanceof DisabledException) {
            errorMessage = "Account disabled";
        } else {
            errorMessage = "Authentication failed";
        }

        PrintWriter writer = response.getWriter();
        writer.write("{\"error\": \"" + errorMessage + "\"}");
        writer.flush();
    }
}
