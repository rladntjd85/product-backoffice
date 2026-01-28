package com.rladntjd85.backoffice.auth.domain;

import com.rladntjd85.backoffice.common.jpa.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Entity
@Table(name = "users")
public class User extends BaseTimeEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 255)
    private String email;

    @Column(name = "password_hash", nullable = false, length = 255)
    private String passwordHash;

    @Column(nullable = false, length = 100)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private Role role;

    @Column(nullable = false)
    private boolean enabled;

    @Column(nullable = false)
    private boolean locked;

    @Column(name = "failed_login_count", nullable = false)
    private int failedLoginCount;

    @Column(name = "last_login_at")
    private LocalDateTime lastLoginAt;

    public void increaseFailCountAndLockIfNeeded(int maxFail) {
        this.failedLoginCount++;
        if (this.failedLoginCount >= maxFail) {
            this.locked = true;
        }
    }

    public void resetFailCount() {
        this.failedLoginCount = 0;
    }

    public void unlock() {
        this.locked = false;
        this.failedLoginCount = 0;
    }

    public void markLoginSuccess(LocalDateTime at) {
        this.lastLoginAt = at;
    }
}
