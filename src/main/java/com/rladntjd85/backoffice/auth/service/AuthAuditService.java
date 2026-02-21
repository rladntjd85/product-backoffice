package com.rladntjd85.backoffice.auth.service;

import com.rladntjd85.backoffice.audit.domain.AuditLog;
import com.rladntjd85.backoffice.audit.repository.AuditLogRepository;
import com.rladntjd85.backoffice.audit.service.AuditWriter;
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
    private final AuditWriter auditWriter;

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

        auditWriter.write(
                userId,
                "LOGIN_SUCCESS",
                "AUTH",
                null,
                ip,
                userAgent,
                AuditWriter.payload(java.util.Map.of("email", email))
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

        var diff = new java.util.LinkedHashMap<String, Object>();
        diff.put("email", email);
        diff.put("reason", reason);
        if (user != null) {
            diff.put("failedCount", failedCount);
            diff.put("locked", lockedNow);
        }

        auditWriter.write(
                null, // 실패는 인증 주체가 없으므로 actor null 유지
                "LOGIN_FAIL",
                "USER",
                targetId,
                ip,
                userAgent,
                diff
        );
    }
}
