package com.learning.springbootonboarding.config;

import com.learning.springbootonboarding.constants.ApiEndPoints;
import com.learning.springbootonboarding.filter.TokenBlacklistFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.server.resource.web.authentication.BearerTokenAuthenticationFilter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, TokenBlacklistFilter tokenBlacklistFilter)
            throws Exception {

        http
                // 1. Use Customizer.withDefaults() to wire up the corsConfigurationSource bean below
                .cors(Customizer.withDefaults())
                .csrf(AbstractHttpConfigurer::disable)

                .sessionManagement(session ->
                        session.sessionCreationPolicy(
                                SessionCreationPolicy.STATELESS
                        )
                )

                .authorizeHttpRequests(auth -> auth
                        // 2. Allow browser preflight OPTIONS requests globally
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .requestMatchers(
                                "/v3/api-docs/**",
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                ApiEndPoints.HEALTH_V1
                        ).permitAll()
                        .requestMatchers(
                                HttpMethod.POST,
                                ApiEndPoints.AUTH_SIGNUP_V1,
                                ApiEndPoints.AUTH_LOGIN_V1,
                                ApiEndPoints.AUTH_REFRESH_TOKEN
                        ).permitAll()

                        .anyRequest().authenticated()
                )

                .oauth2ResourceServer(oauth2 ->
                        oauth2.jwt(jwt -> {})
                )
                // Block req if (user did logout/ refresh token expired) because accessToken still may valid till its expiry
                .addFilterAfter(tokenBlacklistFilter, BearerTokenAuthenticationFilter.class);

        return http.build();
    }

    // 3. Define your allowed frontend origin, methods, and headers
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of("http://localhost:5173"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("Authorization", "Content-Type", "X-Requested-With"));
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}
