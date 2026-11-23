package com.learning.springbootonboarding.day12.service;

import com.learning.springbootonboarding.day12.dto.CreateUserDto;
import com.learning.springbootonboarding.day12.dto.TokenResponse;
import com.learning.springbootonboarding.day12.dto.UserResponse;
import com.learning.springbootonboarding.day12.model.User;
import com.learning.springbootonboarding.day12.model.UserIdentity;
import com.learning.springbootonboarding.day12.repository.UserIdentityRepository;
import com.learning.springbootonboarding.day12.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserIdentityRepository userIdentityRepository;
    private final KeycloakAdminService keycloakAdminService;

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
}
