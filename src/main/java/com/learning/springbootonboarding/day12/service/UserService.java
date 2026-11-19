package com.learning.springbootonboarding.day12.service;

import com.learning.springbootonboarding.day12.dto.CreateUserDto;
import com.learning.springbootonboarding.day12.dto.UserResponse;
import com.learning.springbootonboarding.day12.model.User;
import com.learning.springbootonboarding.day12.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    @Transactional
    public UserResponse createUser(CreateUserDto request) {

        // Check whether email already exists
        if (userRepository.findByEmailIgnoreCase(request.email()).isPresent()) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "User already exists with this email"
            );
        }

        // Convert DTO into Entity
        User user = new User(
                request.email().trim(),
                request.firstName().trim(),
                request.lastName().trim()
        );

        // Save user in database
        User savedUser = userRepository.save(user);

        // Convert Entity into Response DTO
        return new UserResponse(
                savedUser.getId(),
                savedUser.getEmail(),
                savedUser.getFirstName(),
                savedUser.getLastName(),
                savedUser.getStatus(),
                savedUser.getCreatedAt()
        );
    }
}