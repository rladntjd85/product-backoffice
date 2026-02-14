package com.rladntjd85.backoffice.common.web.admin;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AdminController extends BaseAdminController{

    @GetMapping("/admin")
    public String dashboard(Model model) {
        return render(model, "대시보드", "admin/dashboard");
    }

}
