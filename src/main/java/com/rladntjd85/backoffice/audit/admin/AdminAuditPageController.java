package com.rladntjd85.backoffice.audit.admin;

import com.rladntjd85.backoffice.audit.service.AdminAuditService;
import com.rladntjd85.backoffice.common.web.admin.BaseAdminController;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.time.LocalDateTime;

@RequiredArgsConstructor
@Controller
@RequestMapping("/admin/audits")
public class AdminAuditPageController extends BaseAdminController {

    private final AdminAuditService adminAuditService;

    @GetMapping
    public String list(Model model,
                       @RequestParam(required = false) String action,
                       @RequestParam(required = false) String targetType,
                       @RequestParam(required = false) String targetId,
                       @RequestParam(required = false) String targetUserEmail,
                       @RequestParam(required = false) String actorEmail,
                       @RequestParam(required = false) String fromDate,
                       @RequestParam(required = false) String toDate,
                       @RequestParam(defaultValue = "0") int page) {

        String targetIdStr = (targetId == null ? "" : targetId.trim());
        Long parsedTargetId = null;
        if (!targetIdStr.isBlank()) parsedTargetId = Long.valueOf(targetIdStr);

        LocalDateTime fromDt = null;
        LocalDateTime toDtExclusive = null;

        if (fromDate != null && !fromDate.isBlank()) {
            fromDt = LocalDate.parse(fromDate).atStartOfDay();
        }
        if (toDate != null && !toDate.isBlank()) {
            toDtExclusive = LocalDate.parse(toDate).plusDays(1).atStartOfDay(); // 다음날 0시 (exclusive)
        }

        var result = adminAuditService.search(
                action, targetType, parsedTargetId,
                targetUserEmail, actorEmail,
                fromDt, toDtExclusive,
                page, 20
        );

        model.addAttribute("result", result);
        model.addAttribute("action", action);
        model.addAttribute("targetType", targetType);
        model.addAttribute("targetId", targetIdStr);
        model.addAttribute("targetUserEmail", targetUserEmail);
        model.addAttribute("actorEmail", actorEmail);
        model.addAttribute("fromDate", fromDate);
        model.addAttribute("toDate", toDate);

        return render(model, "감사목록", "admin/audits/list");
    }
}
