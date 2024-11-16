package com.invensio.cavinator.config.security;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/public/api/v2")
public class PublicController {
    @GetMapping("/test-error")
    public String testError() {
        throw new RuntimeException("This is a test exception");
    }
}
