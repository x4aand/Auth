package com.auth.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Table(name = "users_auth", schema = "auth_data")
@Entity
@Getter @Setter
public class UserEntity {

    @Id
    @Column(name = "uuid", nullable = false)
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID uuid;

    @Column(name = "username", nullable = false,  unique = true)
    private String username;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "email", nullable = false, unique = true)
    private String  email;


    // данные по аккаунту

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    @Getter
    private Instant createdAt;

    @Column(name = "account_non_expired", nullable = false)
    @Getter @Setter
    private boolean accountNonExpired = true;

    @Column(name = "account_non_locked", nullable = false)
    @Getter @Setter
    private boolean accountNonLocked = true;

    @Column(name = "credentials_non_expired", nullable = false)
    @Getter @Setter
    private boolean credentialsNonExpired = true;

    @Column(nullable = false)
    @Getter @Setter
    private boolean enabled = true;

    @Column(name = "account_expires_at")
    @Getter @Setter
    private Instant accountExpiresAt;

    @LastModifiedDate
    @Column(name = "password_changed_at")
    @Getter @Setter
    private Instant passwordChangedAt;


    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "users_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )

    @Getter @Setter
    private Set<UserRoleEntity> roles = new HashSet<>();

    public UserEntity() {
        this.accountExpiresAt = Instant.now().plus(365, ChronoUnit.DAYS);
        this.enabled = true;
        this.passwordChangedAt = Instant.now();
        this.createdAt = Instant.now();
    }
}
