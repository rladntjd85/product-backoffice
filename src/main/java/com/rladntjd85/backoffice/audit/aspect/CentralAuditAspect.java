package com.rladntjd85.backoffice.audit.aspect;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rladntjd85.backoffice.audit.annotation.AuditLoggable;
import com.rladntjd85.backoffice.audit.annotation.AuditTargetId;
import com.rladntjd85.backoffice.audit.service.AuditWriter;
import com.rladntjd85.backoffice.auth.service.CustomUserDetails;
import jakarta.persistence.EntityManager;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.lang.annotation.Annotation;
import java.util.*;

@Aspect
@Component
public class CentralAuditAspect {

    private final AuditWriter auditWriter;
    private final EntityManager entityManager;
    private final ObjectMapper objectMapper;

    public CentralAuditAspect(AuditWriter auditWriter,
                              EntityManager entityManager,
                              @Qualifier("auditObjectMapper") ObjectMapper objectMapper) {
        this.auditWriter = auditWriter;
        this.entityManager = entityManager;
        this.objectMapper = objectMapper;
    }

    @Around("@annotation(auditLoggable)")
    public Object processAudit(ProceedingJoinPoint joinPoint, AuditLoggable auditLoggable) throws Throwable {
        String ip = extractIp();
        String userAgent = extractUserAgent();
        Long actorUserId = extractActorId();
        Long targetId = extractTargetId(joinPoint);

        Object beforeEntity = null;
        boolean isEntityOperation = auditLoggable.entityClass() != void.class;

        // 1. UPDATE 및 (논리) DELETED 시점의 이전 데이터 확보
        if (isEntityOperation && targetId != null && !auditLoggable.action().contains("CREATED")) {
            beforeEntity = entityManager.find(auditLoggable.entityClass(), targetId);
            if (beforeEntity != null) {
                entityManager.detach(beforeEntity); // 영속성 컨텍스트 분리
            }
        }

        // 2. 실제 비즈니스 로직(Service) 실행
        Object result = joinPoint.proceed();
        Object payload = null;

        // 3. 로그 페이로드(Diff 또는 Snapshot) 생성
        if (isEntityOperation) {
            if (auditLoggable.action().contains("CREATED")) {
                // CREATE: 반환된 Long ID를 이용해 등록된 엔티티 스냅샷 기록
                if (result instanceof Long) {
                    targetId = (Long) result;
                    Object afterEntity = entityManager.find(auditLoggable.entityClass(), targetId);
                    Map<String, Object> snapshot = objectMapper.convertValue(afterEntity, new TypeReference<>() {});
                    payload = Map.of("snapshot", snapshot);
                }
            } else {
                // UPDATE / DELETE: 수정 후 데이터 다시 조회 및 Diff 추출
                Object afterEntity = entityManager.find(auditLoggable.entityClass(), targetId);
                payload = generateDiff(beforeEntity, afterEntity);
            }
        } else if (result instanceof Map) {
            // LOGIN_FAIL 등 Map을 반환하는 이벤트 기반 처리
            payload = result;
        }

        // 4. 변경사항이 존재할 때만 AuditWriter 호출
        if (payload != null && !(payload instanceof Map && ((Map<?, ?>) payload).isEmpty())) {
            // LOGIN_FAIL 등 특수 케이스는 빈 Map이라도 기록을 허용할 수 있도록 예외 처리 가능
            auditWriter.write(actorUserId, auditLoggable.action(), auditLoggable.targetType(), targetId, ip, userAgent, payload);
        }

        return result;
    }

    // --- 유틸리티 메서드들 ---

    private Long extractTargetId(ProceedingJoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Annotation[][] parameterAnnotations = signature.getMethod().getParameterAnnotations();
        Object[] args = joinPoint.getArgs();

        for (int i = 0; i < parameterAnnotations.length; i++) {
            for (Annotation annotation : parameterAnnotations[i]) {
                if (annotation instanceof AuditTargetId && args[i] instanceof Long) {
                    return (Long) args[i];
                }
            }
        }
        return null;
    }

    private Map<String, Object> generateDiff(Object before, Object after) {
        Map<String, Object> beforeMap = before == null ? Collections.emptyMap() : objectMapper.convertValue(before, new TypeReference<>() {});
        Map<String, Object> afterMap = after == null ? Collections.emptyMap() : objectMapper.convertValue(after, new TypeReference<>() {});

        List<Map<String, Object>> changes = new ArrayList<>();
        Set<String> allKeys = new HashSet<>(beforeMap.keySet());
        allKeys.addAll(afterMap.keySet());

        for (String key : allKeys) {
            Object bVal = beforeMap.get(key);
            Object aVal = afterMap.get(key);
            if (!Objects.equals(bVal, aVal)) {
                Map<String, Object> fieldChange = new LinkedHashMap<>();
                fieldChange.put("field", key);
                fieldChange.put("before", bVal);
                fieldChange.put("after", aVal);
                changes.add(fieldChange);
            }
        }

        if (changes.isEmpty()) return Collections.emptyMap();

        Map<String, Object> diffResult = new LinkedHashMap<>();
        diffResult.put("changes", changes);
        return diffResult;
    }

    private Long extractActorId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof CustomUserDetails cud) {
            return cud.getId();
        }
        return null;
    }

    private String extractIp() {
        ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attrs == null) return "UNKNOWN";
        HttpServletRequest request = attrs.getRequest();

        String xff = request.getHeader("X-Forwarded-For");
        if (xff != null && !xff.isBlank()) {
            return xff.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }

    private String extractUserAgent() {
        ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        return (attrs != null) ? attrs.getRequest().getHeader("User-Agent") : "UNKNOWN";
    }
}