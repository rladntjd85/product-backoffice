package com.rladntjd85.backoffice.auth.web;

import com.rladntjd85.backoffice.user.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
@RequiredArgsConstructor
public class MustChangePasswordInterceptor implements HandlerInterceptor {

    private final UserRepository userRepository;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        String uri = request.getRequestURI();

        // 허용 경로들 (비번변경/로그아웃/정적리소스)
        if (uri.startsWith("/auth/password-change")
                || uri.startsWith("/login")
                || uri.startsWith("/logout")
                || uri.startsWith("/error")
                || uri.startsWith("/css/")
                || uri.startsWith("/js/")
                || uri.startsWith("/img/")
                || uri.startsWith("/uploads/")
                || uri.equals("/favicon.ico")) {
            return true;
        }

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal())) {
            return true; // 인증 안 된 건 시큐리티가 처리
        }

        String email = auth.getName();
        boolean mustChange = userRepository.findByEmail(email)
                .map(u -> u.isMustChangePassword())
                .orElse(false);

        if (mustChange) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return false;
        }

        return true;
    }
}
