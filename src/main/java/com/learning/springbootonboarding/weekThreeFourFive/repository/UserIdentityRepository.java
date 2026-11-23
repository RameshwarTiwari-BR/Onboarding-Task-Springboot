package com.learning.springbootonboarding.weekThreeFourFive.repository;

import com.learning.springbootonboarding.weekThreeFourFive.model.UserIdentity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserIdentityRepository extends JpaRepository<UserIdentity, UUID> {

    <T> Optional<UserIdentity> findByIssuerAndSubject(String issuer, String subject);
}
