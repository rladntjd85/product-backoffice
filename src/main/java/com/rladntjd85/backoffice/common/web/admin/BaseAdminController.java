package com.rladntjd85.backoffice.common.web.admin;

import com.rladntjd85.backoffice.auth.service.CustomUserDetails;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.Authentication;
import org.springframework.ui.Model;

public abstract class BaseAdminController {

    protected String render(Model model, String pageTitle, String contentTemplate) {
        model.addAttribute("pageTitle", pageTitle);
        model.addAttribute("contentTemplate", contentTemplate);   // 예: "admin/dashboard"
        return "layout/admin-layout";
    }

    protected Long currentUserId(Authentication authentication) {
        if (authentication == null) return null;

        Object principal = authentication.getPrincipal();

        if (principal instanceof CustomUserDetails cud) {
            return cud.getId();
        }

        return null;
    }

    protected String clientIp(HttpServletRequest request) {
        String xff = request.getHeader("X-Forwarded-For");
        if (xff != null && !xff.isBlank()) {
            return xff.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }

    protected String userAgent(HttpServletRequest request) {
        return request.getHeader("User-Agent");
    }
}

