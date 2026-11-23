package com.learning.springbootonboarding.weekThreeFourFive.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "user_identities")
@Getter
@Setter
@NoArgsConstructor
public class UserIdentity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "provider", nullable = false, length = 30)
    private String provider;

    @Column(name = "issuer", nullable = false, length = 500)
    private String issuer;

    @Column(name = "subject", nullable = false, length = 255)
    private String subject;

    @Column(name = "password_hash", length = 255)
    private String passwordHash;

    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt = OffsetDateTime.now();

    public UserIdentity(User user, String provider, String issuer, String subject) {
        this.user = user;
        this.provider = provider;
        this.issuer = issuer;
        this.subject = subject;
        this.passwordHash = null;
        this.createdAt = OffsetDateTime.now();
    }
}
