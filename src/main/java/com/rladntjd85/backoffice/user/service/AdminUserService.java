package com.rladntjd85.backoffice.user.service;

import com.rladntjd85.backoffice.audit.annotation.AuditLoggable;
import com.rladntjd85.backoffice.audit.annotation.AuditTargetId;
import com.rladntjd85.backoffice.auth.domain.Role;
import com.rladntjd85.backoffice.common.security.util.PasswordValidator;
import com.rladntjd85.backoffice.common.security.util.TempPasswordGenerator;
import com.rladntjd85.backoffice.user.domain.User;
import com.rladntjd85.backoffice.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AdminUserService {

    public enum Tab {ALL, ACTIVE, DISABLED, LOCKED}

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final TempPasswordGenerator tempPasswordGenerator;

    /**
     * 회원 생성: AOP가 반환된 CreateUserResult의 userId를 통해 스냅샷을 기록합니다.
     * (AOP 엔진에서 result가 DTO인 경우 ID를 추출하는 로직이 필요합니다.)
     */
    @Transactional
    @AuditLoggable(action = "USER_CREATED", targetType = "USER", entityClass = User.class)
    public CreateUserResult createUser(String email, String name, String passwordOrBlank) {
        if (userRepository.existsByEmail(email)) {
            throw new IllegalStateException("이미 존재하는 이메일입니다.");
        }

        Role role = Role.VIEWER;
        boolean generated = (passwordOrBlank == null || passwordOrBlank.isBlank());
        String rawPassword = generated ? tempPasswordGenerator.generate() : passwordOrBlank;

        PasswordValidator.validatePassword(rawPassword);

        User user = User.builder()
                .email(email)
                .name(name)
                .role(role)
                .passwordHash(passwordEncoder.encode(rawPassword))
                .mustChangePassword(true)
                .enabled(true)
                .locked(false)
                .failedLoginCount(0)
                .build();

        userRepository.save(user);

        return new CreateUserResult(user.getId(), user.getEmail(), user.getName(), user.getRole(),
                generated ? rawPassword : null);
    }

    @Transactional(readOnly = true)
    public Page<User> listUsers(Tab tab, Pageable pageable) {
        return switch (tab) {
            case ACTIVE -> userRepository.findByEnabled(true, pageable);
            case DISABLED -> userRepository.findByEnabled(false, pageable);
            case LOCKED -> userRepository.findByLockedTrue(pageable);
            case ALL -> userRepository.findAll(pageable);
        };
    }

    @Transactional
    @AuditLoggable(action = "USER_UNLOCKED", targetType = "USER", entityClass = User.class)
    public void unlock(@AuditTargetId Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("user not found"));
        user.unlock();
    }

    @Transactional
    @AuditLoggable(action = "USER_ENABLED", targetType = "USER", entityClass = User.class)
    public void enable(@AuditTargetId Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("user not found"));
        user.enable();
    }

    @Transactional
    @AuditLoggable(action = "USER_DISABLED", targetType = "USER", entityClass = User.class)
    public void disable(@AuditTargetId Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("user not found"));

        if (user.getRole() == Role.ADMIN && user.isEnabled()) {
            long enabledAdminCount = userRepository.countByRoleAndEnabledTrue(Role.ADMIN);
            if (enabledAdminCount <= 1) {
                throw new IllegalStateException("마지막 ADMIN 계정은 비활성화할 수 없습니다.");
            }
        }
        user.disable();
    }

    /**
     * 역할 변경: AOP가 Role 필드의 변경 전/후를 자동 감지하여 Diff를 생성합니다.
     */
    @Transactional
    @AuditLoggable(action = "USER_ROLE_CHANGE", targetType = "USER", entityClass = User.class)
    public void changeRole(@AuditTargetId Long userId, String roleValue) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("user not found"));

        Role newRole = Role.valueOf(roleValue);

        if (user.getRole() == Role.ADMIN && newRole != Role.ADMIN && user.isEnabled()) {
            long enabledAdminCount = userRepository.countByRoleAndEnabledTrue(Role.ADMIN);
            if (enabledAdminCount <= 1) {
                throw new IllegalStateException("최소 1명의 ADMIN(활성)은 유지되어야 합니다.");
            }
        }
        user.changeRole(newRole);
    }

    /**
     * 비밀번호 초기화: AOP가 마킹된 필드 변경 및 결과 Map을 기록합니다.
     */
    @Transactional
    @AuditLoggable(action = "USER_RESET_PASSWORD", targetType = "USER", entityClass = User.class)
    public ResetPasswordResult resetPassword(@AuditTargetId Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("user not found"));

        String tempPassword = tempPasswordGenerator.generate();
        String hash = passwordEncoder.encode(tempPassword);

        user.updatePasswordHash(hash);
        user.markMustChangePassword(true);
        user.unlock();

        return new ResetPasswordResult(user.getId(), user.getEmail(), tempPassword);
    }

    public record ResetPasswordResult(Long userId, String email, String tempPassword) {}

    public record CreateUserResult(
            Long userId,
            String email,
            String name,
            Role role,
            String tempPasswordOrNull
    ) {}
}