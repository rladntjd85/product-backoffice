package com.rladntjd85.backoffice.user.domain;

import com.rladntjd85.backoffice.auth.domain.Role;
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

    @Column(name = "must_change_password", nullable = false)
    private boolean mustChangePassword;

    @Column(nullable = false)
    private boolean enabled;

    @Column(nullable = false)
    private boolean locked;

    @Column(name = "failed_login_count", nullable = false)
    private Integer failedLoginCount;

    @Column(name = "last_login_at")
    private LocalDateTime lastLoginAt;

    public void unlock() {
        this.locked = false;
        this.failedLoginCount = 0;
    }

    public void markLoginSuccess(LocalDateTime at) {
        this.lastLoginAt = at;
    }

    public void increaseFailedLoginCount() {
        this.failedLoginCount = (this.failedLoginCount == null ? 1 : this.failedLoginCount + 1);
    }

    public void resetFailedLoginCount() {
        this.failedLoginCount = 0;
    }

    public int getFailedLoginCount() {
        return this.failedLoginCount == null ? 0 : this.failedLoginCount;
    }

    public void lock() {
        this.locked = true;
    }

    // 선택: 마지막 로그인 기록 컬럼이 있으면
    public void touchLastLoginAt() {
        this.lastLoginAt = java.time.LocalDateTime.now();
    }

    public void changeName(String name) { this.name = name; }
    public void changeRole(Role role) { this.role = role; }
    public void updatePasswordHash(String hash) { this.passwordHash = hash; }
    public void markMustChangePassword(boolean v) { this.mustChangePassword = v; }
    public void enable() { this.enabled = true; }
    public void disable() { this.enabled = false; }
}
