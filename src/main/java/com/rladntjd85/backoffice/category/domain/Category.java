package com.rladntjd85.backoffice.category.domain;

import com.rladntjd85.backoffice.common.jpa.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Entity
@Table(name = "categories")
public class Category extends BaseTimeEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String name;

    @Column(nullable = false)
    private boolean enabled;

    public void rename(String name) {
        this.name = name;
    }

    public void enable() { this.enabled = true; }
    public void disable() { this.enabled = false; }
}
