package com.learning.springbootonboarding.day12.controller;

import com.learning.springbootonboarding.constants.ApiEndPoints;
import com.learning.springbootonboarding.day12.dto.CreateUserDto;
import com.learning.springbootonboarding.day12.dto.LoginRequest;
import com.learning.springbootonboarding.day12.dto.TokenResponse;
import com.learning.springbootonboarding.day12.dto.UserResponse;
import com.learning.springbootonboarding.day12.service.KeycloakAdminService;
import com.learning.springbootonboarding.day12.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.learning.springbootonboarding.constants.ApiEndPoints.AUTH_LOGIN_V1;

@RestController
@RequiredArgsConstructor
public class AuthController {

    private final KeycloakAdminService keycloakAdminService;
    private final UserService userService;

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
}
