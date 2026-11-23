package com.learning.springbootonboarding.config;

import com.learning.springbootonboarding.constants.ApiEndPoints;
import com.learning.springbootonboarding.filter.TokenBlacklistFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.server.resource.web.authentication.BearerTokenAuthenticationFilter;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, TokenBlacklistFilter tokenBlacklistFilter)
            throws Exception {

        http
                .csrf(AbstractHttpConfigurer::disable)

                .sessionManagement(session ->
                        session.sessionCreationPolicy(
                                SessionCreationPolicy.STATELESS
                        )
                )

                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/v3/api-docs/**",
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                ApiEndPoints.HEALTH_V1
                        ).permitAll()
                        .requestMatchers(
                                HttpMethod.POST, ApiEndPoints.AUTH_SIGNUP_V1, ApiEndPoints.AUTH_LOGIN_V1
                        ).permitAll()

                        .anyRequest().authenticated()
                )

                .oauth2ResourceServer(oauth2 ->
                        oauth2.jwt(jwt -> {})
                )
//                Block req if (user did logout/ refersh token expired) because accesstoken still may valid till its expiry
                .addFilterAfter(tokenBlacklistFilter, BearerTokenAuthenticationFilter.class);

        return http.build();
    }
}