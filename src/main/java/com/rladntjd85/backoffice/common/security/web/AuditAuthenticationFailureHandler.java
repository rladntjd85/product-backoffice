package com.rladntjd85.backoffice.common.security.web;

import com.rladntjd85.backoffice.auth.service.AuthAuditService;
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

    private final RequestMetaResolver requestMetaResolver;
    private final AuthAuditService authAuditService;

    @Override
    public void onAuthenticationFailure(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException exception
    ) throws IOException, ServletException {

        String email = request.getParameter("email");
        var meta = requestMetaResolver.resolve(request);

        authAuditService.onLoginFailure(
                email,
                meta.ip(),
                meta.userAgent(),
                exception.getClass().getSimpleName()
        );

        response.sendRedirect("/login?error");
    }

    private String safe(String s) {
        return s == null ? "" : s.replace("\"", "");
    }
}
