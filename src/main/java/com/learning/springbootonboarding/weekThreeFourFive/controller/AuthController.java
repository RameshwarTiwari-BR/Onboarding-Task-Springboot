package com.learning.springbootonboarding.weekThreeFourFive.controller;

import com.learning.springbootonboarding.constants.ApiEndPoints;
import com.learning.springbootonboarding.weekThreeFourFive.dto.*;
import com.learning.springbootonboarding.weekThreeFourFive.service.KeycloakAdminService;
import com.learning.springbootonboarding.weekThreeFourFive.service.TokenBlackListService;
import com.learning.springbootonboarding.weekThreeFourFive.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class AuthController {

    private final KeycloakAdminService keycloakAdminService;
    private final UserService userService;
    private final TokenBlackListService tokenBlackListService;

    @PostMapping(ApiEndPoints.AUTH_LOGIN_V1)
    public ResponseEntity<TokenResponse> login(@Valid @RequestBody LoginRequest request) {
        TokenResponse tokens = keycloakAdminService.fetchUserToken(
                request.email(),
                request.password()
        );
        return ResponseEntity.ok(tokens);
    }


    @PostMapping(ApiEndPoints.AUTH_SIGNUP_V1)
    public ResponseEntity<UserResponse> createUser(
            @Valid
            @RequestBody CreateUserDto request
    ){
        UserResponse created = userService.createUser(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PostMapping(ApiEndPoints.AUTH_LOGOUT_V1)
    public ResponseEntity<Void> logoutUser(
            @AuthenticationPrincipal Jwt jwt,
            @Valid
            @RequestBody LogoutRequest request
    ){
        keycloakAdminService.LogoutUser(request.refreshToken());
        if (jwt != null) {
            tokenBlackListService.blackList(jwt.getId(), jwt.getExpiresAt());
        }
        return  ResponseEntity.noContent().build();
    }
}
