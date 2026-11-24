package com.learning.springbootonboarding.weekThreeFourFive.service;

import com.learning.springbootonboarding.weekThreeFourFive.dto.CreateUserDto;
import com.learning.springbootonboarding.weekThreeFourFive.dto.TokenResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.web.client.OAuth2ClientHttpRequestInterceptor;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import org.springframework.web.server.ResponseStatusException;

import java.net.URI;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class KeycloakAdminService {

    private final RestClient adminRestClient;
    private final RestClient publicRestClient;
    private final String realm;
    private final String loginClientId;

    public KeycloakAdminService(
            OAuth2AuthorizedClientManager authorizedClientManager,
            @Value("${keycloak.admin.base-url}") String baseUrl,
            @Value("${keycloak.admin.realm}") String realm,
            @Value("${keycloak.admin.issuer}") String issuer,
            @Value("${keycloak.admin.login-client-id}") String loginClientId) {

        this.realm = realm;
        this.loginClientId = loginClientId;

        var oauth2Interceptor = new OAuth2ClientHttpRequestInterceptor(authorizedClientManager);
        oauth2Interceptor.setClientRegistrationIdResolver(request -> "keycloak-admin");

        // Authenticated client for Admin REST API calls
        this.adminRestClient = RestClient.builder()
                .baseUrl(baseUrl)
                .requestInterceptor(oauth2Interceptor)
                .build();

        // Unauthenticated client for user token exchange
        this.publicRestClient = RestClient.builder()
                .baseUrl(issuer)
                .build();
    }


    public String createKeycloakUser(CreateUserDto dto) {
        String email = dto.email().trim();

        Map<String, Object> payload = Map.of(
                "username", email,
                "email", email,
                "firstName", dto.firstName().trim(),
                "lastName", dto.lastName().trim(),
                "enabled", true,
                "emailVerified", true,
                "credentials", List.of(Map.of(
                        "type", "password",
                        "value", dto.password(),
                        "temporary", false  // user do not have to re-reset password after just signup
                ))
        );

        try {
            URI location = adminRestClient.post()
                    .uri("/admin/realms/{realm}/users", realm)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(payload)
                    .retrieve()
                    .toBodilessEntity()
                    .getHeaders()
                    .getLocation();

            if (location == null) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_GATEWAY,
                        "Keycloak created the user but did not return a Location header"
                );
            }

            String path = location.getPath();
            return path.substring(path.lastIndexOf('/') + 1);

        } catch (HttpClientErrorException.Conflict ex) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "User already exists in Keycloak with this email"
            );
        }
    }

    public void deleteKeycloakUser(String keycloakUserId) {
        if (keycloakUserId == null || keycloakUserId.isBlank()) {
            return;
        }
        try {
            adminRestClient.delete()
                    .uri("/admin/realms/{realm}/users/{id}", realm, keycloakUserId)
                    .retrieve()
                    .toBodilessEntity();
        } catch (Exception ex) {
            throw ex;
        }
    }

//    Logout
    public void LogoutUser(String refreshToken){
        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("client_id", loginClientId);
        formData.add("refresh_token", refreshToken.trim());

        try {
            publicRestClient.post().uri("/protocol/openid-connect/logout")
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .body(formData)
                    .retrieve()
                    .toBodilessEntity();
        } catch (HttpClientErrorException.BadRequest ex) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Invalid or already expired refresh token");
        };
    }


    public TokenResponse fetchUserToken(String email, String password) {
        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("grant_type", "password");
        formData.add("client_id", loginClientId);
        formData.add("username", email.trim());
        formData.add("password", password);

        try {
            return publicRestClient.post()
                    .uri("/protocol/openid-connect/token")
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .body(formData)
                    .retrieve()
                    .body(TokenResponse.class);
        } catch (HttpClientErrorException.Unauthorized | HttpClientErrorException.BadRequest ex) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Invalid email or password"
            );
        }
    }

    public TokenResponse refreshToken(String refreshToken) {
        if (refreshToken == null || refreshToken.isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Refresh token is required"
            );
        }

        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("grant_type", "refresh_token");
        formData.add("client_id", loginClientId);
        formData.add("refresh_token", refreshToken.trim());

        try {
            return publicRestClient.post()
                    .uri("/protocol/openid-connect/token")
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .body(formData)
                    .retrieve()
                    .body(TokenResponse.class);
        } catch (HttpClientErrorException.Unauthorized | HttpClientErrorException.BadRequest ex) {
            log.debug("Keycloak token refresh failed: {}", ex.getResponseBodyAsString());
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Refresh token expired or invalid, please login again"
            );
        }
    }


}
