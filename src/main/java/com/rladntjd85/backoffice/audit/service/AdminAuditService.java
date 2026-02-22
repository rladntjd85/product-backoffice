package com.rladntjd85.backoffice.audit.service;

import com.rladntjd85.backoffice.audit.dto.AdminAuditRow;
import com.rladntjd85.backoffice.audit.repository.AuditLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AdminAuditService {

    private final AuditLogRepository auditLogRepository;

    public Page<AdminAuditRow> search(String action, String targetType, Long targetId,
                                      String actorEmail, String targetUserEmail,
                                      LocalDateTime fromDt, LocalDateTime toDtExclusive,
                                      int page, int size) {

        Pageable pageable = PageRequest.of(page, size,
                Sort.by(Sort.Direction.DESC, "createdAt")
                        .and(Sort.by(Sort.Direction.DESC, "id")));

        // 파라미터 순서를 레포지토리 메서드 정의와 일치시킵니다.
        return auditLogRepository.searchRows(
                action,
                targetType,
                targetId,
                targetUserEmail, // 4번째 위치 확인
                actorEmail,      // 5번째 위치 확인
                fromDt,
                toDtExclusive,
                pageable
        );
    }
}
