package com.rladntjd85.backoffice.common.web;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class UserHomeController {

    @GetMapping("/user")
    public String userHome(Model model) {
        model.addAttribute("pageTitle", "사용자 홈");
        model.addAttribute("content", "user/home :: content");
        return "layout/admin-layout"; // 사용자 전용 레이아웃 만들기 전이면 임시로 공유
    }
}
