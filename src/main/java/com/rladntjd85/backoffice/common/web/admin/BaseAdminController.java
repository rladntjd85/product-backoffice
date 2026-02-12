package com.rladntjd85.backoffice.common.web.admin;

import org.springframework.ui.Model;

public abstract class BaseAdminController {

    protected String render(Model model, String pageTitle, String contentFragment) {
        model.addAttribute("pageTitle", pageTitle);
        model.addAttribute("content", contentFragment);
        return "layout/admin-layout";
    }
}

