package com.rladntjd85.backoffice.dashboard.service;

import com.rladntjd85.backoffice.audit.repository.AuditLogRepository;
import com.rladntjd85.backoffice.dashboard.dto.*;
import com.rladntjd85.backoffice.product.domain.ProductStatus;
import com.rladntjd85.backoffice.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminDashboardService {

    private final ProductRepository productRepository;
    private final AuditLogRepository auditLogRepository;

    @Cacheable(value = "dashboardSummary", key = "'all'")
    public DashboardSummaryDto summary() {
        LocalDate today = LocalDate.now();
        LocalDateTime from = today.atStartOfDay();
        LocalDateTime to = today.plusDays(1).atStartOfDay();

        return DashboardSummaryDto.builder()
                .totalProducts(productRepository.countAllProducts())
                .activeProducts(productRepository.countByStatus(ProductStatus.ACTIVE))
                .hiddenProducts(productRepository.countByStatus(ProductStatus.HIDDEN))
                .soldOutProducts(productRepository.countByStatus(ProductStatus.SOLD_OUT))
                .deletedProducts(productRepository.countByStatus(ProductStatus.DELETED))
                .todayEvents(auditLogRepository.countBetween(from, to))
                .todayLoginFail(auditLogRepository.countLoginFailBetween(from, to))
                .build();
    }

    @Cacheable(value = "auditDaily", key = "#days")
    public List<AuditDailyDto> auditDaily(int days) {
        LocalDateTime from = LocalDate.now().minusDays(days - 1L).atStartOfDay();
        var raw = auditLogRepository.dailyCountsSince(from);

        Map<String, Long> map = new LinkedHashMap<>();
        for (int i = days - 1; i >= 0; i--) {
            map.put(LocalDate.now().minusDays(i).toString(), 0L);
        }
        for (var r : raw) {
            String d = r.getD().toLocalDate().toString();
            if (map.containsKey(d)) {
                map.put(d, r.getCnt());
            }
        }

        return map.entrySet().stream()
                .map(e -> new AuditDailyDto(e.getKey(), e.getValue()))
                .toList();
    }

    @Cacheable(value = "auditTopActions", key = "#days + '-' + #limit")
    public List<ActionTopDto> topActions(int days, int limit) {
        LocalDateTime from = LocalDate.now().minusDays(days - 1L).atStartOfDay();
        var list = auditLogRepository.topActionsSince(from, PageRequest.of(0, limit));
        return list.stream()
                .map(v -> new ActionTopDto(v.getActionType(), v.getCnt()))
                .toList();
    }

    @Cacheable(value = "recentAudits", key = "#limit")
    public List<RecentAuditDto> recentAudits(int limit) {
        var logs = auditLogRepository.findTop20ByOrderByCreatedAtDesc();
        return logs.stream().limit(limit)
                .map(a -> new RecentAuditDto(
                        a.getId(), a.getCreatedAt().toString(), a.getActionType(),
                        a.getTargetType(), a.getTargetId(), a.getActorUserId(), a.getIp()
                ))
                .toList();
    }

    @Cacheable(value = "stockAlerts", key = "#threshold + '-' + #limit")
    public StockAlertsDto stockAlerts(int threshold, int limit) {
        var low = productRepository.findLowStock(threshold, PageRequest.of(0, limit));
        var soldOut = productRepository.findSoldOut(PageRequest.of(0, limit));

        return new StockAlertsDto(
                low.stream().map(p -> new ProductAlertDto(p.getId(), p.getName(), p.getStock(), p.getStatus().name())).toList(),
                soldOut.stream().map(p -> new ProductAlertDto(p.getId(), p.getName(), p.getStock(), p.getStatus().name())).toList()
        );
    }
}