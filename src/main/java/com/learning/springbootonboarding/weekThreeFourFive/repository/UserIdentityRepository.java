package com.learning.springbootonboarding.day12.repository;

import com.learning.springbootonboarding.day12.model.UserIdentity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserIdentityRepository extends JpaRepository<UserIdentity, UUID> {

    <T> Optional<UserIdentity> findByIssuerAndSubject(String issuer, String subject);
}
