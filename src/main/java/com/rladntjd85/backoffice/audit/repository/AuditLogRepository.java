package com.rladntjd85.backoffice.audit.repository;

import com.rladntjd85.backoffice.audit.domain.AuditLog;
import com.rladntjd85.backoffice.audit.dto.AdminAuditRow;
import org.apache.ibatis.annotations.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

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

    @Query("""
                select count(a) from AuditLog a
                where a.createdAt >= :from and a.createdAt < :to
            """)
    long countBetween(@Param("from") LocalDateTime from, @Param("to") LocalDateTime to);

    @Query("""
                select count(a) from AuditLog a
                where a.actionType = 'LOGIN_FAIL'
                  and a.createdAt >= :from and a.createdAt < :to
            """)
    long countLoginFailBetween(@Param("from") LocalDateTime from, @Param("to") LocalDateTime to);

    // 최근 N건
    List<AuditLog> findTop20ByOrderByCreatedAtDesc();

    // Top actions (최근 days)
    @Query("""
                select a.actionType as actionType, count(a) as cnt
                from AuditLog a
                where a.createdAt >= :from
                group by a.actionType
                order by count(a) desc
            """)
    List<ActionCountView> topActionsSince(@Param("from") LocalDateTime from, Pageable pageable);

    // 일자별 count (최근 days)
    // DB가 MySQL이면 DATE(a.createdAt) 사용 가능. JPA에서 function 사용.
    @Query("""
                select function('date', a.createdAt) as d, count(a) as cnt
                from AuditLog a
                where a.createdAt >= :from
                group by function('date', a.createdAt)
                order by function('date', a.createdAt) asc
            """)
    List<DailyCountView> dailyCountsSince(@Param("from") LocalDateTime from);

    interface ActionCountView {
        String getActionType();

        long getCnt();
    }

    interface DailyCountView {
        java.sql.Date getD();

        long getCnt();
    }

}
