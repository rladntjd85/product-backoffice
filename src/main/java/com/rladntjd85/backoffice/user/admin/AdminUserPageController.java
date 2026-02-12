package com.rladntjd85.backoffice.user.admin;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin/users")
public class AdminUserPageController {

    @GetMapping
    public String list(Model model) {
        model.addAttribute("pageTitle", "사용자 목록");
        model.addAttribute("content", "admin/users/list :: content");
        return "layout/admin-layout";
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Model model) {
        model.addAttribute("pageTitle", "사용자 상세");
        model.addAttribute("content", "admin/users/detail :: content");
        model.addAttribute("userId", id);
        return "layout/admin-layout";
    }

    // 아래 3개는 “UI 버튼 연결용” 라우팅 자리(서비스 연결은 다음 단계)
    @PostMapping("/{id}/role")
    public String changeRole(@PathVariable Long id,
                             @RequestParam("role") String role) {
        return "redirect:/admin/users/" + id;
    }

    @PostMapping("/{id}/unlock")
    public String unlock(@PathVariable Long id) {
        return "redirect:/admin/users/" + id;
    }

    @PostMapping("/{id}/reset-password")
    public String resetPassword(@PathVariable Long id) {
        return "redirect:/admin/users/" + id + "?resetDone";
    }
}
