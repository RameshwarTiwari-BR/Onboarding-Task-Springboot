
package com.learning.springbootonboarding.weekThreeFourFive.repository;

import com.learning.springbootonboarding.weekThreeFourFive.model.User;
import com.learning.springbootonboarding.weekThreeFourFive.model.UserStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {

    Optional<User> findByEmailIgnoreCase(String email);

    @Query("SELECT u.status FROM User u WHERE LOWER(u.email) = LOWER(:email)")
    Optional<UserStatus> findStatusByEmail(@Param("email") String email);

}

