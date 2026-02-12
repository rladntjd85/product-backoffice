package com.rladntjd85.backoffice.auth.service;

import com.rladntjd85.backoffice.audit.domain.AuditLog;
import com.rladntjd85.backoffice.audit.repository.AuditLogRepository;
import com.rladntjd85.backoffice.user.domain.User;
import com.rladntjd85.backoffice.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthAuditService {

    private static final int LOCK_THRESHOLD = 10;

    private final UserRepository userRepository;
    private final AuditLogRepository auditLogRepository;

    @Transactional
    public void onLoginSuccess(String email, String ip, String userAgent) {
        User user = userRepository.findByEmail(email).orElse(null);

        Long userId = (user != null) ? user.getId() : null;

        if (user != null) {
            // 성공 시 실패횟수 초기화 + 마지막 로그인 시각(있으면)
            user.resetFailedLoginCount();
            user.unlock();
            user.touchLastLoginAt(); // 없으면 제거
        }

        auditLogRepository.save(
                AuditLog.of(
                        userId,                 // actor_user_id
                        "LOGIN_SUCCESS",
                        "AUTH",                 // target_type
                        null,                   // target_id
                        ip,
                        userAgent,
                        "{\"email\":\"" + email + "\"}"
                )
        );
    }

    @Transactional
    public void onLoginFailure(String email, String ip, String userAgent, String reason) {
        User user = userRepository.findByEmail(email).orElse(null);

        Long targetId = (user != null) ? user.getId() : null;
        boolean lockedNow = false;
        int failedCount = -1;

        if (user != null) {
            user.increaseFailedLoginCount();
            failedCount = user.getFailedLoginCount();

            if (failedCount >= LOCK_THRESHOLD) {
                user.lock();
                lockedNow = true;
            }
        }

        // actor_user_id는 실패에서는 null 유지(인증된 주체가 아니므로)
        String diffJson = "{\"email\":\"" + email + "\",\"reason\":\"" + safe(reason) + "\""
                + (user != null ? ",\"failedCount\":" + failedCount : "")
                + (user != null ? ",\"locked\":" + lockedNow : "")
                + "}";

        auditLogRepository.save(
                AuditLog.of(
                        null,                  // actor_user_id
                        "LOGIN_FAIL",
                        "USER",                // target_type
                        targetId,              // target_id (존재 시 userId)
                        ip,
                        userAgent,
                        diffJson
                )
        );
    }

    private String safe(String s) {
        return s == null ? "" : s.replace("\"", "");
    }
}
