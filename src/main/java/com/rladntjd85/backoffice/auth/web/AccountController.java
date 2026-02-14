package com.rladntjd85.backoffice.auth.web;

import com.rladntjd85.backoffice.auth.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AccountController {

    private final AccountService accountService;

    // 1️⃣ 비밀번호 변경 페이지
    @GetMapping("/password-change")
    public String passwordChangeForm() {
        return "auth/password-change";
    }

    // 2️⃣ 비밀번호 변경 처리
    @PostMapping("/password-change")
    public String passwordChange(@RequestParam String newPassword,
                                 Authentication authentication,
                                 Model model) {

        try {
            accountService.changePassword(authentication.getName(), newPassword);

            boolean isAdminOrMd = authentication.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN") || a.getAuthority().equals("ROLE_MD"));

            return isAdminOrMd ? "redirect:/admin" : "redirect:/user";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "auth/password-change";
        }
    }
}
