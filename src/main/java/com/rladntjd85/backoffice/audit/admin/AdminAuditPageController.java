package com.rladntjd85.backoffice.audit.admin;

import com.rladntjd85.backoffice.audit.service.AdminAuditService;
import com.rladntjd85.backoffice.common.web.admin.BaseAdminController;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RequiredArgsConstructor
@Controller
@RequestMapping("/admin/audits")
public class AdminAuditPageController extends BaseAdminController {

    private final AdminAuditService adminAuditService;

    @GetMapping
    public String list(Model model,
                       @RequestParam(required = false) String action,
                       @RequestParam(required = false) String targetType,
                       @RequestParam(required = false) Long targetId,
                       @RequestParam(required = false) Long actorId,
                       @RequestParam(defaultValue = "0") int page) {

        var result = adminAuditService.search(
                action, targetType, targetId, actorId,
                page, 20
        );

        model.addAttribute("result", result);
        model.addAttribute("action", action);
        model.addAttribute("targetType", targetType);
        model.addAttribute("targetId", targetId);
        model.addAttribute("actorId", actorId);

        return render(model, "감사목록", "admin/audits/list");
    }
}
