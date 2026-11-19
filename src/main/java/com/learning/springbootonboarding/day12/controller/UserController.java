package com.learning.springbootonboarding.day12.controller;


import com.learning.springbootonboarding.constants.ApiEndPoints;
import com.learning.springbootonboarding.day12.dto.CreateUserDto;
import com.learning.springbootonboarding.day12.dto.UserResponse;
import com.learning.springbootonboarding.day12.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping(ApiEndPoints.USERS_V1)
public class UserController {
    private final UserService userService;


    @PostMapping()
    public ResponseEntity<UserResponse> createUser(
            @Valid
            @RequestBody CreateUserDto request
            ){
        UserResponse created = userService.createUser(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }
}
