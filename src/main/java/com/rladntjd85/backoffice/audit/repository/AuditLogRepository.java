package com.rladntjd85.backoffice.audit.repository;

import com.rladntjd85.backoffice.audit.domain.AuditLog;
import com.rladntjd85.backoffice.audit.dto.AdminAuditRow;
import org.apache.ibatis.annotations.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;

public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {

    @Query(
        value = """
            select new com.rladntjd85.backoffice.audit.dto.AdminAuditRow(
                a.id, a.createdAt, a.actionType,
                a.actorUserId, u.email,
                a.targetType, a.targetId,
                a.ip, a.userAgent, a.diffJson
            )
            from AuditLog a
            left join User u on u.id = a.actorUserId
            left join User u2 on u2.id = a.targetId and a.targetType = 'USER'
            where (:action is null or :action = '' or lower(a.actionType) like lower(concat('%', :action, '%')))
              and (:targetType is null or :targetType = '' or a.targetType = :targetType)
              and (:targetId is null or a.targetId = :targetId)
              and (:targetUserEmail is null or :targetUserEmail = '' or lower(u2.email) like lower(concat('%', :targetUserEmail, '%')))
              and (:actorEmail is null or :actorEmail = '' or lower(u.email) like lower(concat('%', :actorEmail, '%')))
              and (:fromDt is null or a.createdAt >= :fromDt)
              and (:toDtExclusive is null or a.createdAt < :toDtExclusive)
            order by a.createdAt desc
            """,
        countQuery = """
            select count(a.id)
            from AuditLog a
            left join User u on u.id = a.actorUserId
            left join User u2 on u2.id = a.targetId and a.targetType = 'USER'
            where (:action is null or :action = '' or lower(a.actionType) like lower(concat('%', :action, '%')))
              and (:targetType is null or :targetType = '' or a.targetType = :targetType)
              and (:targetId is null or a.targetId = :targetId)
              and (:targetUserEmail is null or :targetUserEmail = '' or lower(u2.email) like lower(concat('%', :targetUserEmail, '%')))
              and (:actorEmail is null or :actorEmail = '' or lower(u.email) like lower(concat('%', :actorEmail, '%')))
              and (:fromDt is null or a.createdAt >= :fromDt)
              and (:toDtExclusive is null or a.createdAt < :toDtExclusive)
            """
    )
    Page<AdminAuditRow> searchRows(@Param("action") String action,
                                   @Param("targetType") String targetType,
                                   @Param("targetId") Long targetId,
                                   @Param("targetUserEmail") String targetUserEmail,
                                   @Param("actorEmail") String actorEmail,
                                   @Param("fromDt") LocalDateTime fromDt,
                                   @Param("toDtExclusive") LocalDateTime toDtExclusive,
                                   Pageable pageable);


}
