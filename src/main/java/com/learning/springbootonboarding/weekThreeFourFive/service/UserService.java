package com.learning.springbootonboarding.weekThreeFourFive.service;

import com.learning.springbootonboarding.weekThreeFourFive.dto.CreateUserDto;
import com.learning.springbootonboarding.weekThreeFourFive.dto.TokenResponse;
import com.learning.springbootonboarding.weekThreeFourFive.dto.UserResponse;
import com.learning.springbootonboarding.weekThreeFourFive.model.User;
import com.learning.springbootonboarding.weekThreeFourFive.model.UserIdentity;
import com.learning.springbootonboarding.weekThreeFourFive.repository.UserIdentityRepository;
import com.learning.springbootonboarding.weekThreeFourFive.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserIdentityRepository userIdentityRepository;
    private final KeycloakAdminService keycloakAdminService;
    private final CurrentUserService currentUserService;

    @Value("${keycloak.admin.issuer}")
    private String keycloakIssuer;

    @Transactional
    public UserResponse createUser(CreateUserDto request) {
        String email = request.email().trim();

        // 1. Check whether email already exists locally
        if (userRepository.findByEmailIgnoreCase(email).isPresent()) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "User already exists with this email"
            );
        }

        // 2. Create user in Keycloak first and capture its UUID (sub)
        String keycloakSub = keycloakAdminService.createKeycloakUser(request);

        try {
            // 3. Save user in PostgreSQL (no password column needed)
            User user = new User(
                    email,
                    request.firstName().trim(),
                    request.lastName().trim()
            );
            User savedUser = userRepository.save(user);

            // 4. Link Keycloak identity in user_identities (passwordHash = null)
            UserIdentity identity = new UserIdentity(
                    savedUser,
                    "KEYCLOAK",
                    keycloakIssuer,
                    keycloakSub
            );
            userIdentityRepository.save(identity);

            // 5. Fetch immediate user JWT via Direct Access Grants (onboarding-client)
            TokenResponse tokens = keycloakAdminService.fetchUserToken(email, request.password());

            return new UserResponse(
                    savedUser.getId(),
                    savedUser.getEmail(),
                    savedUser.getFirstName(),
                    savedUser.getLastName(),
                    savedUser.getStatus(),
                    savedUser.getCreatedAt(),
                    tokens
            );
        } catch (Exception ex) {
            // Rollback Keycloak user if Postgres insert or token fetch fails
            keycloakAdminService.deleteKeycloakUser(keycloakSub);
            throw ex;
        }
    }

    public List<UserResponse> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(user -> new UserResponse(
                        user.getId(),
                        user.getEmail(),
                        user.getFirstName(),
                        user.getLastName(),
                        user.getStatus(),
                        user.getCreatedAt(),
                        null // tokens are null when listing users
                ))
                .toList();
    }

}
