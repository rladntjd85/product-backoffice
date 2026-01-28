package com.rladntjd85.backoffice.common.security.web;

import com.rladntjd85.backoffice.audit.repository.AuditLogRepository;
import com.rladntjd85.backoffice.audit.domain.AuditLog;
import com.rladntjd85.backoffice.auth.repository.UserRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class AuditAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final UserRepository userRepository;
    private final AuditLogRepository auditLogRepository;
    private final RequestMetaResolver requestMetaResolver;

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) throws IOException, ServletException {

        String email = authentication.getName();
        Long userId = userRepository.findByEmail(email).map(u -> u.getId()).orElse(null);

        var meta = requestMetaResolver.resolve(request);

        auditLogRepository.save(
                AuditLog.of(
                        userId,
                        "LOGIN_SUCCESS",
                        "AUTH",
                        null,
                        meta.ip(),
                        meta.userAgent(),
                        null
                )
        );

        response.sendRedirect("/admin"); // 필요 시 변경
    }
}
