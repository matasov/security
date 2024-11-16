package com.invensio.cavinator.config.security;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
public class CustomErrorController implements ErrorController {

    @RequestMapping("/error")
    public ResponseEntity<Map<String, Object>> handleError(HttpServletRequest request) throws Throwable {

        // Optional: Log other attributes
        // Enumeration<String> attributeNames = request.getAttributeNames();
        // while (attributeNames.hasMoreElements()) {
        //     String attributeName = attributeNames.nextElement();
        //     Object attributeValue = request.getAttribute(attributeName);
        //     System.out.println("attribute [" + attributeName + "]: " + attributeValue);
        // }
        riseEndpointException(request);
        HttpStatus status;
        Map<String, Object> response = new LinkedHashMap<>();
        Throwable throwable = (Throwable) request.getAttribute(RequestDispatcher.ERROR_EXCEPTION);
        if (throwable != null) {
            status = HttpStatus.FORBIDDEN;
            UUID errorId = UUID.randomUUID();
            log.error("Error ID: " + errorId.toString(), throwable);

            response.put("id", errorId.toString());
            response.put("message", throwable.getMessage());
            response.put("type", throwable.getClass().getSimpleName());
        } else {
            status = HttpStatus.INTERNAL_SERVER_ERROR;
            response.put("id", UUID.randomUUID().toString());
            response.put("message", String.valueOf(request.getAttribute("jakarta.servlet.error.message")));
        }
        return ResponseEntity.status(status).body(response);
    }

    private void riseEndpointException(HttpServletRequest request) throws NoResourceFoundException {
        Object exceptionAttribute = request.getAttribute("org.springframework.web.servlet.DispatcherServlet.EXCEPTION");

        if (exceptionAttribute != null && String.valueOf(exceptionAttribute)
                .contains("org.springframework.web.servlet.resource.NoResourceFoundException")) {
            String message = String.valueOf(exceptionAttribute);
            message = message.substring(message.indexOf("No static resource") + "No static resource ".length(),
                    message.length());
            throw new NoResourceFoundException(HttpMethod.GET, message);
        }
        exceptionAttribute = request.getAttribute("jakarta.servlet.error.message");
        if (exceptionAttribute != null && String.valueOf(exceptionAttribute)
                .contains("Forbidden")) {
            Object uri = request.getAttribute("jakarta.servlet.error.request_uri");
            throw new NoResourceFoundException(HttpMethod.GET, String.valueOf(uri));
        }
    }
}
