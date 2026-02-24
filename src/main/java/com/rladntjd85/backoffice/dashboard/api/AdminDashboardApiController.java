package com.rladntjd85.backoffice.dashboard.api;

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

    /**
     * ADMIN/MD 공통: 상품 KPI + (관리자용이면) todayEvents/todayLoginFail도 포함되어 내려옴
     * - MD UI에서는 todayEvents/todayLoginFail을 그냥 안 쓰면 됨
     */
    @GetMapping("/summary")
    @PreAuthorize("hasAnyRole('ADMIN','MD')")
    public AdminDashboardService.DashboardSummaryDto summary() {
        return dashboardService.summary();
    }

    /**
     * ADMIN/MD 공통: 재고 알림
     */
    @GetMapping("/stock-alerts")
    @PreAuthorize("hasAnyRole('ADMIN','MD')")
    public AdminDashboardService.StockAlertsDto stockAlerts(
            @RequestParam(defaultValue = "5") int threshold,
            @RequestParam(defaultValue = "10") int limit
    ) {
        return dashboardService.stockAlerts(threshold, limit);
    }

    /**
     * ADMIN 전용: 최근 N일 일자별 이벤트 수
     */
    @GetMapping("/audit-daily")
    @PreAuthorize("hasRole('ADMIN')")
    public List<AdminDashboardService.AuditDailyDto> auditDaily(
            @RequestParam(defaultValue = "7") int days
    ) {
        return dashboardService.auditDaily(days);
    }

    /**
     * ADMIN 전용: 최근 N일 action_type TOP N
     */
    @GetMapping("/audit-top-actions")
    @PreAuthorize("hasRole('ADMIN')")
    public List<AdminDashboardService.ActionTopDto> auditTopActions(
            @RequestParam(defaultValue = "7") int days,
            @RequestParam(defaultValue = "8") int limit
    ) {
        return dashboardService.topActions(days, limit);
    }

    /**
     * ADMIN 전용: 최근 감사로그 N건
     */
    @GetMapping("/recent-audits")
    @PreAuthorize("hasRole('ADMIN')")
    public List<AdminDashboardService.RecentAuditDto> recentAudits(
            @RequestParam(defaultValue = "20") int limit
    ) {
        return dashboardService.recentAudits(limit);
    }
}