package com.rladntjd85.backoffice.audit.annotation;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface AuditLoggable {
    String action();               // 예: PRODUCT_UPDATED, LOGIN_FAIL
    String targetType();           // 예: PRODUCT, USER, AUTH
    Class<?> entityClass() default void.class; // 엔티티 조회가 필요할 경우 클래스 지정
}