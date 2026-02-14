package com.rladntjd85.backoffice.user.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rladntjd85.backoffice.audit.domain.AuditLog;
import com.rladntjd85.backoffice.audit.repository.AuditLogRepository;
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

    public enum Tab { ALL, ACTIVE, DISABLED, LOCKED }

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final TempPasswordGenerator tempPasswordGenerator;
    private final AuditLogRepository auditLogRepository;
    private final ObjectMapper objectMapper;

    // 생성: 정책상 무조건 VIEWER
    @Transactional
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
    public void unlock(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("user not found"));
        user.unlock(); // locked=false + failed=0
    }

    @Transactional
    public void enable(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("user not found"));
        user.enable();
    }

    @Transactional
    public void disable(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("user not found"));
        user.disable();
    }

    @Transactional
    public void changeRole(Long userId,
                           String roleValue,
                           Long actorUserId,
                           String ip,
                           String userAgent) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("user not found"));

        Role newRole = Role.valueOf(roleValue);

        if (user.getRole() == Role.ADMIN && newRole != Role.ADMIN) {
            long adminCount = userRepository.countByRole(Role.ADMIN);
            if (adminCount <= 1) {
                throw new IllegalStateException("최소 1명의 ADMIN은 유지되어야 합니다.");
            }
        }

        Role oldRole = user.getRole();
        user.changeRole(newRole);

        try {
            String diffJson = objectMapper.writeValueAsString(
                    java.util.Map.of(
                            "oldRole", oldRole.name(),
                            "newRole", newRole.name()
                    )
            );

            auditLogRepository.save(
                    AuditLog.builder()
                            .actorUserId(actorUserId)
                            .actionType("USER_ROLE_CHANGE")
                            .targetType("USER")
                            .targetId(userId)
                            .ip(ip)
                            .userAgent(userAgent)
                            .diffJson(diffJson)
                            .build()
            );
        } catch (Exception e) {
            throw new RuntimeException("Audit JSON 직렬화 실패", e);
        }
    }

    @Transactional
    public ResetPasswordResult resetPassword(Long userId, Long actorUserId, String ip, String userAgent) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("user not found"));

        String tempPassword = tempPasswordGenerator.generate();
        String hash = passwordEncoder.encode(tempPassword);

        user.updatePasswordHash(hash);
        user.markMustChangePassword(true);

        // 운영 UX: 비번 재발급 시 잠금도 해제
        user.unlock(); // locked=false + failed=0 (네 엔티티에 이미 존재)

        String diffJson = "{\"action\":\"RESET_PASSWORD\",\"mustChangePassword\":true}";

        auditLogRepository.save(
                com.rladntjd85.backoffice.audit.domain.AuditLog.builder()
                        .actorUserId(actorUserId)
                        .actionType("USER_RESET_PASSWORD")
                        .targetType("USER")
                        .targetId(userId)
                        .ip(ip)
                        .userAgent(userAgent)
                        .diffJson(diffJson)
                        .build()
        );

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
