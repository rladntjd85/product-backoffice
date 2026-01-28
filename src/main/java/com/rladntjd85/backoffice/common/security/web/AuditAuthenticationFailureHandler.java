package com.rladntjd85.backoffice.common.security.web;

import com.rladntjd85.backoffice.audit.domain.AuditLog;
import com.rladntjd85.backoffice.audit.repository.AuditLogRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class AuditAuthenticationFailureHandler implements AuthenticationFailureHandler {

    private final AuditLogRepository auditLogRepository;
    private final RequestMetaResolver requestMetaResolver;

    @Override
    public void onAuthenticationFailure(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException exception
    ) throws IOException, ServletException {

        var meta = requestMetaResolver.resolve(request);

        auditLogRepository.save(
                AuditLog.of(
                        null,
                        "LOGIN_FAIL",
                        "AUTH",
                        null,
                        meta.ip(),
                        meta.userAgent(),
                        "{\"reason\":\"" + safe(exception.getClass().getSimpleName()) + "\"}"
                )
        );

        response.sendRedirect("/login?error");
    }

    private String safe(String s) {
        return s == null ? "" : s.replace("\"", "");
    }
}
