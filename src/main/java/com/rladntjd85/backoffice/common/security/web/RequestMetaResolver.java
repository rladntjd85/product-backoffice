package com.rladntjd85.backoffice.common.security.web;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;

@Component
public class RequestMetaResolver {

    public RequestMeta resolve(HttpServletRequest request) {
        String ip = extractClientIp(request);
        String ua = request.getHeader("User-Agent");
        return new RequestMeta(ip, ua);
    }

    private String extractClientIp(HttpServletRequest request) {
        String xff = request.getHeader("X-Forwarded-For");
        if (xff != null && !xff.isBlank()) {
            return xff.split(",")[0].trim();
        }
        String xrip = request.getHeader("X-Real-IP");
        if (xrip != null && !xrip.isBlank()) {
            return xrip.trim();
        }
        return request.getRemoteAddr();
    }

    public record RequestMeta(String ip, String userAgent) {}
}
