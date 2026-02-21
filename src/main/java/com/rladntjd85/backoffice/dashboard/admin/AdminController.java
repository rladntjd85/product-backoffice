package com.rladntjd85.backoffice.dashboard.admin;

import com.rladntjd85.backoffice.common.web.admin.BaseAdminController;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AdminController extends BaseAdminController {

    @GetMapping("/admin")
    public String dashboard(Model model, Authentication auth) {
        
        boolean isAdmin = auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        model.addAttribute("isAdmin", isAdmin);

        return render(model, "대시보드", "admin/dashboard");
    }
}
