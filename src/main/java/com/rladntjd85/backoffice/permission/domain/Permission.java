package com.rladntjd85.backoffice.permission.domain;

import jakarta.persistence.*;
import lombok.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Entity
@Table(name = "permissions")
public class Permission {

    @Id
    @Column(length = 50)
    private String code;

    @Column(nullable = false, length = 255)
    private String description;
}
