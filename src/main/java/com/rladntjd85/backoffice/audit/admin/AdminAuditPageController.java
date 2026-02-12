package com.rladntjd85.backoffice.audit.admin;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin/audits")
public class AdminAuditPageController {

    @GetMapping
    public String list(Model model,
                       @RequestParam(required = false) String action,
                       @RequestParam(required = false) String targetType,
                       @RequestParam(required = false) Long targetId,
                       @RequestParam(required = false) Long actorId) {

        model.addAttribute("pageTitle", "감사로그");
        model.addAttribute("content", "admin/audits/list :: content");

        model.addAttribute("action", action);
        model.addAttribute("targetType", targetType);
        model.addAttribute("targetId", targetId);
        model.addAttribute("actorId", actorId);

        return "layout/admin-layout";
    }
}
