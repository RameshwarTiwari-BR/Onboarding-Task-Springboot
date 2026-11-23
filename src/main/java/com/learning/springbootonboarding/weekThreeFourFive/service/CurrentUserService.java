package com.learning.springbootonboarding.weekThreeFourFive.service;

import com.learning.springbootonboarding.weekThreeFourFive.model.User;
import com.learning.springbootonboarding.weekThreeFourFive.model.UserIdentity;
import com.learning.springbootonboarding.weekThreeFourFive.model.UserStatus;
import com.learning.springbootonboarding.weekThreeFourFive.repository.UserIdentityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;


import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CurrentUserService {

    private final UserIdentityRepository userIdentityRepository;
//    private final TokenBlackListService tokenBlackListService;

    public User getCurrentUser() {
        Jwt jwt = getJwt();

//        if (tokenBlackListService.isBlackListed(jwt.getId())) {
//            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Token has been revoked");
//        }  if anyone service not calling this method- bypass access token immediate check

        String issuer = jwt.getIssuer().toString();
        String subject = jwt.getSubject();

        UserIdentity identity = userIdentityRepository
                .findByIssuerAndSubject(issuer, subject)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED,
                        "Authenticated identity is not linked to a local user"
                ));

        User user = identity.getUser();
        if (!UserStatus.ACTIVE.equals(user.getStatus())) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "User account is disabled"
            );
        }

        return user;
    }

    public UUID getCurrentUserId() {
        return getCurrentUser().getId();
    }

    private Jwt getJwt() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof Jwt jwt)) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "No valid JWT authentication found"
            );
        }
        return jwt;
    }
}
