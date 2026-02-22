package com.rladntjd85.backoffice.category.domain;

import com.rladntjd85.backoffice.common.jpa.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Entity
@Table(name = "categories")
public class Category extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100) // 중복 체크는 부모별로 다를 수 있어 유니크 해제 고려
    private String name;

    @Column(nullable = false)
    private boolean enabled;

    // 추가: 자기 참조 연관관계
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Category parent;

    @Builder.Default
    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL)
    private List<Category> children = new ArrayList<>();

    public void rename(String name) {
        this.name = name;
    }

    // 이 메서드를 추가해 주세요
    public void changeParent(Category parent) {
        this.parent = parent;
    }

    public void enable() {
        this.enabled = true;
    }

    public void disable() {
        this.enabled = false;
        // 부모가 비활성화되면 자식들도 비활성화 (선택 사항)
        this.children.forEach(Category::disable);
    }
}