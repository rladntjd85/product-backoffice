package com.rladntjd85.backoffice.auth.web;

import com.rladntjd85.backoffice.auth.service.AccountService;
import com.rladntjd85.backoffice.user.domain.User;
import com.rladntjd85.backoffice.user.repository.UserRepository;
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
    private final UserRepository userRepository;


    // 1) 비밀번호 변경 페이지 (최초/일반 공용)
    @GetMapping("/password-change")
    public String passwordChangeForm(Authentication authentication, Model model) {
        String email = authentication.getName();

        boolean mustChange = userRepository.findByEmail(email)
                .map(User::isMustChangePassword)
                .orElse(false);

        model.addAttribute("mustChange", mustChange);
        return "auth/password-change";
    }

    // 2) 비밀번호 변경 처리
    @PostMapping("/password-change")
    public String passwordChange(@RequestParam(required = false) String currentPassword,
                                 @RequestParam String newPassword,
                                 @RequestParam String confirmPassword,
                                 Authentication authentication,
                                 Model model) {

        if (!newPassword.equals(confirmPassword)) {
            model.addAttribute("error", "새 비밀번호 확인이 일치하지 않습니다.");
            model.addAttribute("mustChange", false);
            return "auth/password-change";
        }

        String email = authentication.getName();
        boolean mustChange = userRepository.findByEmail(email)
                .map(User::isMustChangePassword)
                .orElse(false);

        try {
            if (mustChange) {
                accountService.changePassword(email, newPassword); // 최초 강제 변경
            } else {
                accountService.changePasswordWithCurrent(email, currentPassword, newPassword); // 일반 변경(본인)
            }

            boolean isAdminOrMd = authentication.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN") || a.getAuthority().equals("ROLE_MD"));

            return isAdminOrMd ? "redirect:/admin" : "redirect:/user";

        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("mustChange", mustChange);
            return "auth/password-change";
        }
    }
}
