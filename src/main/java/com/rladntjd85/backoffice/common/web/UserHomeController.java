package com.rladntjd85.backoffice.common.web;

import com.rladntjd85.backoffice.common.web.admin.BaseAdminController;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class UserHomeController extends BaseAdminController {

    @GetMapping("/user")
    public String userHome(Model model) {
//        model.addAttribute("pageTitle", "사용자 홈");
//        model.addAttribute("content", "user/home :: content");
//        return "layout/admin-layout"; // 사용자 전용 레이아웃 만들기 전이면 임시로 공유
        return render(model, "사용자 홈", "user/home");
    }
}
