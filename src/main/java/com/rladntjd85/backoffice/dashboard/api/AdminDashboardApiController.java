package com.rladntjd85.backoffice.dashboard.api;

import com.rladntjd85.backoffice.dashboard.dto.*;
import com.rladntjd85.backoffice.dashboard.service.AdminDashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/api/dashboard")
public class AdminDashboardApiController {

    private final AdminDashboardService dashboardService;

    @GetMapping("/summary")
    @PreAuthorize("hasAnyRole('ADMIN','MD')")
    public DashboardSummaryDto summary() {
        return dashboardService.summary();
    }

    @GetMapping("/stock-alerts")
    @PreAuthorize("hasAnyRole('ADMIN','MD')")
    public StockAlertsDto stockAlerts(
            @RequestParam(defaultValue = "5") int threshold,
            @RequestParam(defaultValue = "10") int limit
    ) {
        return dashboardService.stockAlerts(threshold, limit);
    }

    @GetMapping("/audit-daily")
    @PreAuthorize("hasRole('ADMIN')")
    public List<AuditDailyDto> auditDaily(@RequestParam(defaultValue = "7") int days) {
        return dashboardService.auditDaily(days);
    }

    @GetMapping("/audit-top-actions")
    @PreAuthorize("hasRole('ADMIN')")
    public List<ActionTopDto> auditTopActions(
            @RequestParam(defaultValue = "7") int days,
            @RequestParam(defaultValue = "8") int limit
    ) {
        return dashboardService.topActions(days, limit);
    }

    @GetMapping("/recent-audits")
    @PreAuthorize("hasRole('ADMIN')")
    public List<RecentAuditDto> recentAudits(@RequestParam(defaultValue = "20") int limit) {
        return dashboardService.recentAudits(limit);
    }
}