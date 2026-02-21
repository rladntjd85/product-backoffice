package com.rladntjd85.backoffice.user.service;

import com.rladntjd85.backoffice.audit.service.AuditWriter;
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

import java.util.LinkedHashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AdminUserService {

    public enum Tab {ALL, ACTIVE, DISABLED, LOCKED}

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final TempPasswordGenerator tempPasswordGenerator;

    //공통 감사 로거
    private final AuditWriter auditWriter;

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

        // enabled=true인 ADMIN이 최소 1명은 유지되어야 함
        if (user.getRole() == Role.ADMIN && user.isEnabled()) {
            long enabledAdminCount = userRepository.countByRoleAndEnabledTrue(Role.ADMIN);
            if (enabledAdminCount <= 1) {
                throw new IllegalStateException("마지막 ADMIN 계정은 비활성화할 수 없습니다.");
            }
        }

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

        if (user.getRole() == Role.ADMIN && newRole != Role.ADMIN && user.isEnabled()) {
            long enabledAdminCount = userRepository.countByRoleAndEnabledTrue(Role.ADMIN);
            if (enabledAdminCount <= 1) {
                throw new IllegalStateException("최소 1명의 ADMIN(활성)은 유지되어야 합니다.");
            }
        }

        Role oldRole = user.getRole();
        user.changeRole(newRole);

        //AuditWriter 사용 (수동 JSON/try-catch 제거)
        auditWriter.write(
                actorUserId,
                "USER_ROLE_CHANGE",
                "USER",
                userId,
                ip,
                userAgent,
                AuditWriter.change("role", oldRole.name(), newRole.name())
        );
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
        user.unlock(); // locked=false + failed=0

        //AuditWriter 사용 (하드코딩 JSON 제거)
        Map<String, Object> diff = new LinkedHashMap<>();
        diff.put("action", "RESET_PASSWORD");
        diff.put("mustChangePassword", true);
        diff.put("unlock", true);

        auditWriter.write(
                actorUserId,
                "USER_RESET_PASSWORD",
                "USER",
                userId,
                ip,
                userAgent,
                diff
        );

        return new ResetPasswordResult(user.getId(), user.getEmail(), tempPassword);
    }

    public record ResetPasswordResult(Long userId, String email, String tempPassword) {
    }

    public record CreateUserResult(
            Long userId,
            String email,
            String name,
            Role role,
            String tempPasswordOrNull
    ) {
    }
}