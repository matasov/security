package com.invensio.cavinator.config.security;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;

@Slf4j
@RestController
@RequestMapping("/api/v2")
public class CheckController {

    @GetMapping("check")
    public ResponseEntity<Map<String, Object>> requestSynchroPost() {
        log.info("Authentication: {}", SecurityContextHolder.getContext().getAuthentication());
        log.info("Authentication class: {}", SecurityContextHolder.getContext().getAuthentication().getClass());
        return ResponseEntity.ok(Map.of("status", "ok"));
    }

}
