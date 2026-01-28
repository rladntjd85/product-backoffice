package com.rladntjd85.backoffice.permission.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Entity
@Table(name = "user_permission")
public class UserPermission {

    @EmbeddedId
    private UserPermissionId id;

    @Column(name = "created_at", nullable = false, updatable = false, insertable = false)
    private LocalDateTime createdAt;

    public static UserPermission of(Long userId, String permissionCode) {
        return UserPermission.builder()
                .id(new UserPermissionId(userId, permissionCode))
                .build();
    }
}
