package com.rladntjd85.backoffice.dashboard.service;

import com.rladntjd85.backoffice.audit.repository.AuditLogRepository;
import com.rladntjd85.backoffice.product.domain.ProductStatus;
import com.rladntjd85.backoffice.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
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

    public DashboardSummaryDto summary() {
        LocalDate today = LocalDate.now(); // KST 서버 기준
        LocalDateTime from = today.atStartOfDay();
        LocalDateTime to = today.plusDays(1).atStartOfDay();

        long totalProducts = productRepository.countAllProducts();
        long active = productRepository.countByStatus(ProductStatus.ACTIVE);
        long hidden = productRepository.countByStatus(ProductStatus.HIDDEN);
        long soldOut = productRepository.countByStatus(ProductStatus.SOLD_OUT);
        long deleted = productRepository.countByStatus(ProductStatus.DELETED);

        long todayEvents = auditLogRepository.countBetween(from, to);
        long todayLoginFail = auditLogRepository.countLoginFailBetween(from, to);

        return new DashboardSummaryDto(
                totalProducts, active, hidden, soldOut, deleted,
                todayEvents, todayLoginFail
        );
    }

    public List<AuditDailyDto> auditDaily(int days) {
        LocalDateTime from = LocalDate.now().minusDays(days - 1L).atStartOfDay();
        var raw = auditLogRepository.dailyCountsSince(from);

        // 빠진 날짜는 0으로 채우기(차트가 예쁘게 나옴)
        Map<LocalDate, Long> map = new LinkedHashMap<>();
        for (int i = days - 1; i >= 0; i--) {
            map.put(LocalDate.now().minusDays(i), 0L);
        }
        for (var r : raw) {
            LocalDate d = r.getD().toLocalDate();
            map.put(d, r.getCnt());
        }

        return map.entrySet().stream()
                .map(e -> new AuditDailyDto(e.getKey().toString(), e.getValue()))
                .toList();
    }

    public List<ActionTopDto> topActions(int days, int limit) {
        LocalDateTime from = LocalDate.now().minusDays(days - 1L).atStartOfDay();
        var list = auditLogRepository.topActionsSince(from, PageRequest.of(0, limit));
        return list.stream()
                .map(v -> new ActionTopDto(v.getActionType(), v.getCnt()))
                .toList();
    }

    public List<RecentAuditDto> recentAudits(int limit) {
        // 간단히 top20 고정. limit 조절하려면 쿼리/메서드 추가.
        var logs = auditLogRepository.findTop20ByOrderByCreatedAtDesc();
        return logs.stream().limit(limit)
                .map(a -> new RecentAuditDto(
                        a.getId(),
                        a.getCreatedAt().toString(),
                        a.getActionType(),
                        a.getTargetType(),
                        a.getTargetId(),
                        a.getActorUserId(),
                        a.getIp()
                ))
                .toList();
    }

    public StockAlertsDto stockAlerts(int threshold, int limit) {
        var low = productRepository.findLowStock(threshold, PageRequest.of(0, limit));
        var soldOut = productRepository.findSoldOut(PageRequest.of(0, limit));

        return new StockAlertsDto(
                low.stream().map(p -> new ProductAlertDto(p.getId(), p.getName(), p.getStock(), p.getStatus().name())).toList(),
                soldOut.stream().map(p -> new ProductAlertDto(p.getId(), p.getName(), p.getStock(), p.getStatus().name())).toList()
        );
    }

    public record DashboardSummaryDto(
            long totalProducts,
            long activeProducts,
            long hiddenProducts,
            long soldOutProducts,
            long deletedProducts,
            long todayEvents,
            long todayLoginFail
    ) {
    }

    public record AuditDailyDto(String day, long count) {
    }

    public record ActionTopDto(String actionType, long count) {
    }

    public record RecentAuditDto(
            Long id,
            String createdAt,
            String actionType,
            String targetType,
            Long targetId,
            Long actorUserId,
            String ip
    ) {
    }

    public record ProductAlertDto(Long id, String name, Integer stock, String status) {
    }

    public record StockAlertsDto(List<ProductAlertDto> lowStock, List<ProductAlertDto> soldOut) {
    }
}