package com.rladntjd85.backoffice.common.web.admin;

import org.springframework.ui.Model;

public abstract class BaseAdminController {

    protected String render(Model model, String pageTitle, String contentTemplate) {
        model.addAttribute("pageTitle", pageTitle);
        model.addAttribute("contentTemplate", contentTemplate);   // 예: "admin/dashboard"
        return "layout/admin-layout";
    }
}

