package com.invensio.cavinator.config.security.util;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.filter.CorsFilter;

import com.invensio.cavinator.config.security.login.AuthenticationFilter;
import com.invensio.cavinator.config.security.login.CustomAuthenticationManager;
import com.invensio.cavinator.config.security.refresh.CustomRefreshManager;
import com.invensio.cavinator.config.security.refresh.RefreshFilter;
import com.invensio.cavinator.config.security.util.server.CompanyServerSettingsService;
import com.invensio.cavinator.config.security.validation.CustomJwtAuthenticationManager;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.fasterxml.jackson.databind.ObjectMapper;

@Slf4j
@Configuration
@EnableWebSecurity(debug = true)
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

        public static final String[] URLExceptions = {
                        "/api/v2/public/request", "/api/auth/recovery/request", "/api/auth/recovery/password",
                        // dynamic role
                        "/admin/v2/dynamicroles", "/admin/v2/dynamicroles/users/*",
                        "/admin/v2/dynamicroles/users/*/domains/*",
                        "/admin/v2/dynamicroles/domains/*",
                        // actuator
                        "/actuator/health/websocketProbe", "/actuator/health/startupProbe",
                        //
                        "/error" };

        private final SimpleCorsFilter corsFiter;
        private final CustomAuthenticationManager customAuthenticationManager;
        private final JwtUtil jwtUtil;
        private final CustomRefreshManager customRefreshManager;
        @Qualifier("customAuthenticationEntryPoint")
        private final AuthenticationEntryPoint authEntryPoint;
        private final CompanyServerSettingsService companyServerSettingsService;
        private final CustomJwtAuthenticationManager customJwtAuthenticationManager;

        @Bean
        @Order(4)
        public SecurityFilterChain refreshSecurityFilterChain(HttpSecurity http) throws Exception {
                RefreshFilter authenticationFilter = new RefreshFilter(
                                customRefreshManager, jwtUtil, companyServerSettingsService, new ObjectMapper());
                http.securityMatchers(matchers -> matchers.requestMatchers("/auth/refresh"))
                                .csrf(csrf -> csrf.disable())
                                .authorizeHttpRequests(authorize -> authorize
                                                .requestMatchers("/auth/refresh").permitAll())
                                .exceptionHandling(Customizer.withDefaults())
                                .sessionManagement(session -> session
                                                .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                                .addFilterAt(authenticationFilter, UsernamePasswordAuthenticationFilter.class)
                                .addFilterAt(corsFiter, CorsFilter.class);
                return http.build();
        }

        @Bean
        @Order(3)
        public SecurityFilterChain authSecurityFilterChain(HttpSecurity http) throws Exception {
                AuthenticationFilter authenticationFilter = new AuthenticationFilter(
                                customAuthenticationManager, jwtUtil, companyServerSettingsService);
                http.securityMatchers(matchers -> matchers.requestMatchers("/auth/login"))
                                .csrf(csrf -> csrf.disable())
                                .authorizeHttpRequests(authorize -> authorize
                                                .requestMatchers("/auth/login").permitAll())
                                .exceptionHandling(Customizer.withDefaults())
                                .sessionManagement(session -> session
                                                .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                                .addFilterAt(authenticationFilter, UsernamePasswordAuthenticationFilter.class)
                                .addFilterAt(corsFiter, CorsFilter.class);
                return http.build();
        }

        @Bean
        @Order(1)
        public SecurityFilterChain publicSecurityFilterChain(HttpSecurity http) throws Exception {
                http
                                .securityMatchers(matchers -> matchers.requestMatchers(URLExceptions))
                                .authorizeHttpRequests(authorize -> authorize
                                                .requestMatchers(URLExceptions).permitAll())
                                .oauth2ResourceServer(oauth2ResourceServer -> oauth2ResourceServer
                                                .authenticationEntryPoint(authEntryPoint)
                                                .jwt(jwt -> jwt.authenticationManager(customJwtAuthenticationManager)))
                                .sessionManagement(session -> session
                                                .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                                .exceptionHandling(Customizer.withDefaults());
                return http.build();
        }

        @Bean
        @Order(2)
        public SecurityFilterChain apiSecurityFilterChain(HttpSecurity http) throws Exception {
                http
                                .securityMatchers(matchers -> matchers.requestMatchers("/api/**"))
                                .authorizeHttpRequests(authorize -> authorize
                                                .anyRequest().authenticated())
                                .oauth2ResourceServer(oauth2ResourceServer -> oauth2ResourceServer
                                                .authenticationEntryPoint(authEntryPoint)
                                                .jwt(jwt -> jwt.authenticationManager(customJwtAuthenticationManager)))
                                .sessionManagement(session -> session
                                                .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                                .exceptionHandling(Customizer.withDefaults());
                return http.build();
        }

}
