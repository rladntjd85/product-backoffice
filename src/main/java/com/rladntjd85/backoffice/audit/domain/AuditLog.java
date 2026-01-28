package com.rladntjd85.backoffice.audit.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Entity
@Table(name = "audit_log")
public class AuditLog {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "actor_user_id")
    private Long actorUserId;

    @Column(name = "action_type", nullable = false, length = 50)
    private String actionType;

    @Column(name = "target_type", nullable = false, length = 30)
    private String targetType;

    @Column(name = "target_id")
    private Long targetId;

    @Column(length = 45)
    private String ip;

    @Column(name = "user_agent", length = 500)
    private String userAgent;

    @Column(name = "diff_json", columnDefinition = "json")
    private String diffJson;

    @Column(name = "created_at", nullable = false, updatable = false, insertable = false)
    private LocalDateTime createdAt;

    public static AuditLog of(Long actorUserId, String actionType, String targetType, Long targetId,
                              String ip, String userAgent, String diffJson) {
        return AuditLog.builder()
                .actorUserId(actorUserId)
                .actionType(actionType)
                .targetType(targetType)
                .targetId(targetId)
                .ip(ip)
                .userAgent(userAgent)
                .diffJson(diffJson)
                .build();
    }
}
