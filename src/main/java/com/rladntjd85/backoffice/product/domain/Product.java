package com.rladntjd85.backoffice.product.domain;

import com.rladntjd85.backoffice.category.domain.Category;
import com.rladntjd85.backoffice.common.jpa.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Entity
@Table(name = "products")
public class Product extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @Column(nullable = false, length = 200)
    private String name;

    @Column(nullable = false)
    private int price; // 원

    @Column(nullable = false)
    private int stock;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private ProductStatus status;

    @Column(name = "thumbnail_url", length = 500)
    private String thumbnailUrl;

    @Column(name = "detail_image_url", length = 500)
    private String detailImageUrl;

    @Column(name = "thumbnail_original_name", length = 255)
    private String thumbnailOriginalName;

    @Column(name = "detail_original_name", length = 255)
    private String detailOriginalName;

    public void updateBasic(String name, int price, int stock, ProductStatus status, Category category) {
        this.name = name;
        this.price = price;
        this.stock = stock;
        this.status = status;
        this.category = category;
    }

    public void updateImages(String thumbUrl, String thumbOrg, String detailUrl, String detailOrg) {
        this.thumbnailUrl = thumbUrl;
        this.thumbnailOriginalName = thumbOrg;
        this.detailImageUrl = detailUrl;
        this.detailOriginalName = detailOrg;
    }

    public void softDelete() {
        this.status = ProductStatus.DELETED;
    }

    public void clearThumbnail() {
        this.thumbnailUrl = null;
        this.thumbnailOriginalName = null;
    }

    public void clearDetailImage() {
        this.detailImageUrl = null;
        this.detailOriginalName = null;
    }
}
