package com.rladntjd85.backoffice.common.security.web;

import com.rladntjd85.backoffice.auth.domain.Role;
import com.rladntjd85.backoffice.user.domain.User;
import com.rladntjd85.backoffice.user.repository.UserRepository;
import com.rladntjd85.backoffice.auth.service.AuthAuditService;
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
    private final AuthAuditService authAuditService;
    private final RequestMetaResolver requestMetaResolver;

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) throws IOException {

        String email = authentication.getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow();

        var meta = requestMetaResolver.resolve(request);

        authAuditService.onLoginSuccess(email, meta.ip(), meta.userAgent());

        if (user.isMustChangePassword()) {
            response.sendRedirect("/auth/password-change");
            return;
        }

        if (user.getRole() == Role.ADMIN || user.getRole() == Role.MD) {
            response.sendRedirect("/admin");
            return;
        }
        response.sendRedirect("/user");
    }
}
