package com.rladntjd85.backoffice.audit.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rladntjd85.backoffice.audit.domain.AuditLog;
import com.rladntjd85.backoffice.audit.repository.AuditLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class AuditWriter {

    private final AuditLogRepository auditLogRepository;
    private final ObjectMapper objectMapper;

    public void write(Long actorUserId,
                      String actionType,
                      String targetType,
                      Long targetId,
                      String ip,
                      String userAgent,
                      Object diffObjectOrNull) {
        try {
            String diffJson = (diffObjectOrNull == null) ? null : objectMapper.writeValueAsString(diffObjectOrNull);

            auditLogRepository.save(
                    AuditLog.of(actorUserId, actionType, targetType, targetId, ip, userAgent, diffJson)
            );
        } catch (Exception e) {
            // best-effort: 감사 실패로 비즈니스 실패 방지
            log.error("Audit write failed actionType={}, targetType={}, targetId={}",
                    actionType, targetType, targetId, e);
        }
    }

    // diff 포맷 통일 헬퍼(선택)
    public static Map<String, Object> change(String field, Object before, Object after) {
        Map<String, Object> one = new LinkedHashMap<>();
        one.put("field", field);
        one.put("before", before);
        one.put("after", after);

        Map<String, Object> m = new LinkedHashMap<>();
        m.put("changes", java.util.List.of(one));
        return m;
    }

    public static Map<String, Object> payload(Map<String, Object> map) {
        return map; // 의미만 부여(가독성)
    }
}