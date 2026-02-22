package com.rladntjd85.backoffice.auth.service;

import com.rladntjd85.backoffice.audit.annotation.AuditLoggable;
import com.rladntjd85.backoffice.user.domain.User;
import com.rladntjd85.backoffice.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthAuditService {

    private static final int LOCK_THRESHOLD = 10;

    private final UserRepository userRepository;

    @Transactional
    @AuditLoggable(action = "LOGIN_SUCCESS", targetType = "AUTH")
    public Map<String, Object> onLoginSuccess(String email) {
        User user = userRepository.findByEmail(email).orElse(null);

        if (user != null) {
            // 성공 시 실패횟수 초기화 + 마지막 로그인 시각(있으면)
            user.resetFailedLoginCount();
            user.unlock();
            user.touchLastLoginAt(); // 없으면 제거
        }

        return Map.of("email", email);
    }

    @Transactional
    @AuditLoggable(action = "LOGIN_FAIL", targetType = "USER")
    public Map<String, Object> onLoginFailure(String email, String reason) {
        // 1. 유저 조회 및 상태 변경 (핵심 로직)
        User user = userRepository.findByEmail(email).orElse(null);
        boolean lockedNow = false;

        if (user != null) {
            user.increaseFailedLoginCount();
            if (user.getFailedLoginCount() >= LOCK_THRESHOLD) {
                user.lock();
                lockedNow = true;
            }
        }

        // 2. 결과만 Map으로 반환 (AOP가 이걸 가로채서 로그로 저장함)
        var logData = new LinkedHashMap<String, Object>();
        logData.put("email", email);
        logData.put("reason", reason);

        if (user != null) {
            logData.put("targetId", user.getId()); // AOP의 targetId 추출용
            logData.put("failedCount", user.getFailedLoginCount());
            logData.put("locked", lockedNow);
        }

        return logData;
    }
}
