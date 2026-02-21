package com.rladntjd85.backoffice.product.service;

import com.rladntjd85.backoffice.audit.service.AuditWriter;
import com.rladntjd85.backoffice.category.domain.Category;
import com.rladntjd85.backoffice.category.repository.CategoryRepository;
import com.rladntjd85.backoffice.common.util.FileStorage;
import com.rladntjd85.backoffice.product.domain.Product;
import com.rladntjd85.backoffice.product.domain.ProductStatus;
import com.rladntjd85.backoffice.product.dto.AdminProductRow;
import com.rladntjd85.backoffice.product.dto.ProductForm;
import com.rladntjd85.backoffice.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class AdminProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final FileStorage fileStorage;

    // 감사로그
    private final AuditWriter auditWriter;

    @Transactional(readOnly = true)
    public Page<AdminProductRow> search(String q, Long categoryId, String status, String sort, int page, int size) {
        Sort s = switch (sort == null ? "CREATED_DESC" : sort) {
            case "PRICE_ASC" -> Sort.by(Sort.Direction.ASC, "price").and(Sort.by(Sort.Direction.DESC, "id"));
            case "PRICE_DESC" -> Sort.by(Sort.Direction.DESC, "price").and(Sort.by(Sort.Direction.DESC, "id"));
            default -> Sort.by(Sort.Direction.DESC, "createdAt").and(Sort.by(Sort.Direction.DESC, "id"));
        };
        Pageable pageable = PageRequest.of(page, size, s);
        return productRepository.searchRows(q, categoryId, status, pageable);
    }

    @Transactional(readOnly = true)
    public long countAll(String q, Long categoryId) {
        return productRepository.countByFilter(q, categoryId, "");
    }

    @Transactional(readOnly = true)
    public long countByStatus(String q, Long categoryId, String status) {
        return productRepository.countByFilter(q, categoryId, status);
    }

    @Transactional(readOnly = true)
    public Product getWithCategory(Long id) {
        return productRepository.findWithCategory(id)
                .orElseThrow(() -> new IllegalArgumentException("상품을 찾을 수 없습니다."));
    }

    private record ProductSnap(
            String name,
            Long categoryId,
            Integer price,
            Integer stock,
            String status,
            String thumbnailUrl,
            String detailImageUrl
    ) {
        static ProductSnap from(Product p) {
            return new ProductSnap(
                    p.getName(),
                    p.getCategory() != null ? p.getCategory().getId() : null,
                    p.getPrice(),
                    p.getStock(),
                    p.getStatus() != null ? p.getStatus().name() : null,
                    p.getThumbnailUrl(),
                    p.getDetailImageUrl()
            );
        }
    }

    private static boolean eq(Object a, Object b) {
        return Objects.equals(a, b);
    }

    @Transactional
    public Long create(ProductForm form, Long actorUserId, String ip, String userAgent) {
        Category category = categoryRepository.findById(form.getCategoryId())
                .orElseThrow(() -> new IllegalArgumentException("카테고리를 찾을 수 없습니다."));

        if (!category.isEnabled()) {
            throw new IllegalArgumentException("비활성 카테고리에는 상품을 등록할 수 없습니다.");
        }

        ProductStatus status = normalizeStatus(parseStatus(form.getStatus()), form.getStock());

        var thumb = fileStorage.storeProductThumb(form.getThumbnailFile());
        var detail = fileStorage.storeProductDetail(form.getDetailImageFile());

        Product p = Product.builder()
                .name(form.getName())
                .category(category)
                .price(form.getPrice())
                .stock(form.getStock())
                .status(status)
                .thumbnailUrl(thumb != null ? thumb.url() : null)
                .thumbnailOriginalName(thumb != null ? thumb.originalName() : null)
                .detailImageUrl(detail != null ? detail.url() : null)
                .detailOriginalName(detail != null ? detail.originalName() : null)
                .build();

        productRepository.save(p);

        // 생성은 스냅샷 1건(원하면 changes로 바꿀 수 있음)
        Map<String, Object> snapshot = new LinkedHashMap<>();
        snapshot.put("name", p.getName());
        snapshot.put("categoryId", p.getCategory() != null ? p.getCategory().getId() : null);
        snapshot.put("price", p.getPrice());
        snapshot.put("stock", p.getStock());
        snapshot.put("status", p.getStatus() != null ? p.getStatus().name() : null);
        snapshot.put("thumbnailUrl", p.getThumbnailUrl());
        snapshot.put("detailImageUrl", p.getDetailImageUrl());

        Map<String, Object> diff = new LinkedHashMap<>();
        diff.put("snapshot", snapshot);

        auditWriter.write(actorUserId, "PRODUCT_CREATED", "PRODUCT", p.getId(), ip, userAgent, diff);

        return p.getId();
    }

    @Transactional
    public void update(Long id, ProductForm form, Long actorUserId, String ip, String userAgent) {
        Product p = getWithCategory(id);

        if (p.getStatus() == ProductStatus.DELETED) {
            throw new IllegalArgumentException("삭제된 상품은 수정할 수 없습니다.");
        }

        Category category = categoryRepository.findById(form.getCategoryId())
                .orElseThrow(() -> new IllegalArgumentException("카테고리를 찾을 수 없습니다."));

        if (!category.isEnabled()) {
            throw new IllegalArgumentException("비활성 카테고리로 변경할 수 없습니다.");
        }

        ProductSnap before = ProductSnap.from(p);

        ProductStatus status = normalizeStatus(parseStatus(form.getStatus()), form.getStock());
        p.updateBasic(form.getName(), form.getPrice(), form.getStock(), status, category);

        // 이미지 교체 시: 기존 파일 삭제 + DB 갱신
        var newThumb = fileStorage.storeProductThumb(form.getThumbnailFile());
        if (newThumb != null) {
            fileStorage.deleteByUrl(p.getThumbnailUrl());
            p.updateImages(
                    newThumb.url(), newThumb.originalName(),
                    p.getDetailImageUrl(), p.getDetailOriginalName()
            );
        }

        var newDetail = fileStorage.storeProductDetail(form.getDetailImageFile());
        if (newDetail != null) {
            fileStorage.deleteByUrl(p.getDetailImageUrl());
            p.updateImages(
                    p.getThumbnailUrl(), p.getThumbnailOriginalName(),
                    newDetail.url(), newDetail.originalName()
            );
        }

        ProductSnap after = ProductSnap.from(p);

        boolean anyChanged = false;

        // 세부 이벤트(여러 건)
        if (!eq(before.price, after.price)) {
            anyChanged = true;
            auditWriter.write(actorUserId, "PRODUCT_PRICE_CHANGED", "PRODUCT", id, ip, userAgent,
                    AuditWriter.change("price", before.price, after.price));
        }
        if (!eq(before.stock, after.stock)) {
            anyChanged = true;
            auditWriter.write(actorUserId, "PRODUCT_STOCK_CHANGED", "PRODUCT", id, ip, userAgent,
                    AuditWriter.change("stock", before.stock, after.stock));
        }
        if (!eq(before.status, after.status)) {
            anyChanged = true;
            auditWriter.write(actorUserId, "PRODUCT_STATUS_CHANGED", "PRODUCT", id, ip, userAgent,
                    AuditWriter.change("status", before.status, after.status));
        }
        if (!eq(before.thumbnailUrl, after.thumbnailUrl)) {
            anyChanged = true;
            auditWriter.write(actorUserId, "PRODUCT_THUMB_CHANGED", "PRODUCT", id, ip, userAgent,
                    AuditWriter.change("thumbnailUrl", before.thumbnailUrl, after.thumbnailUrl));
        }
        if (!eq(before.detailImageUrl, after.detailImageUrl)) {
            anyChanged = true;
            auditWriter.write(actorUserId, "PRODUCT_DETAIL_CHANGED", "PRODUCT", id, ip, userAgent,
                    AuditWriter.change("detailImageUrl", before.detailImageUrl, after.detailImageUrl));
        }

        // 일반 수정(PRODUCT_UPDATED)도 남김(요구사항: 다 남김)
        // changedFields 요약 형태(로그 용량/가독성 균형)
        if (anyChanged || !eq(before.name, after.name) || !eq(before.categoryId, after.categoryId)) {
            var changed = new java.util.ArrayList<String>();
            if (!eq(before.name, after.name)) changed.add("name");
            if (!eq(before.categoryId, after.categoryId)) changed.add("categoryId");
            if (!eq(before.price, after.price)) changed.add("price");
            if (!eq(before.stock, after.stock)) changed.add("stock");
            if (!eq(before.status, after.status)) changed.add("status");
            if (!eq(before.thumbnailUrl, after.thumbnailUrl)) changed.add("thumbnailUrl");
            if (!eq(before.detailImageUrl, after.detailImageUrl)) changed.add("detailImageUrl");

            Map<String, Object> diff = new LinkedHashMap<>();
            diff.put("changedFields", changed);

            auditWriter.write(actorUserId, "PRODUCT_UPDATED", "PRODUCT", id, ip, userAgent, diff);
        }
    }

    @Transactional
    public void hide(Long id, Long actorUserId, String ip, String userAgent) {
        Product p = productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("상품을 찾을 수 없습니다."));

        if (p.getStatus() == ProductStatus.DELETED) {
            throw new IllegalArgumentException("삭제된 상품은 변경할 수 없습니다.");
        }
        if (p.getStatus() != ProductStatus.ACTIVE) {
            throw new IllegalArgumentException("판매중인 상품만 판매중지할 수 있습니다.");
        }

        String before = p.getStatus().name();
        p.updateBasic(p.getName(), p.getPrice(), p.getStock(), ProductStatus.HIDDEN, p.getCategory());
        String after = p.getStatus().name();

        auditWriter.write(actorUserId, "PRODUCT_STATUS_CHANGED", "PRODUCT", id, ip, userAgent,
                AuditWriter.change("status", before, after));

        auditWriter.write(actorUserId, "PRODUCT_UPDATED", "PRODUCT", id, ip, userAgent,
                Map.of("changedFields", java.util.List.of("status")));
    }

    @Transactional
    public void unhide(Long id, Long actorUserId, String ip, String userAgent) {
        Product p = productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("상품을 찾을 수 없습니다."));

        if (p.getStatus() == ProductStatus.DELETED) {
            throw new IllegalArgumentException("삭제된 상품은 변경할 수 없습니다.");
        }
        if (p.getStatus() != ProductStatus.HIDDEN) {
            throw new IllegalArgumentException("판매중지 상태에서만 판매재개할 수 있습니다.");
        }

        String before = p.getStatus().name();
        ProductStatus next = normalizeStatus(ProductStatus.ACTIVE, p.getStock());
        p.updateBasic(p.getName(), p.getPrice(), p.getStock(), next, p.getCategory());
        String after = p.getStatus().name();

        auditWriter.write(actorUserId, "PRODUCT_STATUS_CHANGED", "PRODUCT", id, ip, userAgent,
                AuditWriter.change("status", before, after));

        auditWriter.write(actorUserId, "PRODUCT_UPDATED", "PRODUCT", id, ip, userAgent,
                Map.of("changedFields", java.util.List.of("status")));
    }

    @Transactional
    public void softDelete(Long id, Long actorUserId, String ip, String userAgent) {
        Product p = productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("상품을 찾을 수 없습니다."));
        if (p.getStatus() == ProductStatus.DELETED) return;

        String before = p.getStatus().name();
        p.softDelete();
        String after = p.getStatus().name();

        auditWriter.write(actorUserId, "PRODUCT_DELETED", "PRODUCT", id, ip, userAgent,
                AuditWriter.change("status", before, after));

        auditWriter.write(actorUserId, "PRODUCT_UPDATED", "PRODUCT", id, ip, userAgent,
                Map.of("changedFields", java.util.List.of("status")));
    }

    @Transactional
    public void removeThumbnail(Long id, Long actorUserId, String ip, String userAgent) {
        Product p = productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("상품을 찾을 수 없습니다."));
        if (p.getStatus() == ProductStatus.DELETED) {
            throw new IllegalArgumentException("삭제된 상품은 변경할 수 없습니다.");
        }

        String beforeUrl = p.getThumbnailUrl();
        fileStorage.deleteByUrl(beforeUrl);
        p.clearThumbnail();
        String afterUrl = p.getThumbnailUrl(); // 보통 null

        auditWriter.write(actorUserId, "PRODUCT_THUMB_REMOVED", "PRODUCT", id, ip, userAgent,
                AuditWriter.change("thumbnailUrl", beforeUrl, afterUrl));

        auditWriter.write(actorUserId, "PRODUCT_UPDATED", "PRODUCT", id, ip, userAgent,
                Map.of("changedFields", java.util.List.of("thumbnailUrl")));
    }

    @Transactional
    public void removeDetailImage(Long id, Long actorUserId, String ip, String userAgent) {
        Product p = productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("상품을 찾을 수 없습니다."));
        if (p.getStatus() == ProductStatus.DELETED) {
            throw new IllegalArgumentException("삭제된 상품은 변경할 수 없습니다.");
        }

        String beforeUrl = p.getDetailImageUrl();
        fileStorage.deleteByUrl(beforeUrl);
        p.clearDetailImage();
        String afterUrl = p.getDetailImageUrl(); // 보통 null

        auditWriter.write(actorUserId, "PRODUCT_DETAIL_REMOVED", "PRODUCT", id, ip, userAgent,
                AuditWriter.change("detailImageUrl", beforeUrl, afterUrl));

        auditWriter.write(actorUserId, "PRODUCT_UPDATED", "PRODUCT", id, ip, userAgent,
                Map.of("changedFields", java.util.List.of("detailImageUrl")));
    }

    private ProductStatus parseStatus(String raw) {
        try {
            return ProductStatus.valueOf(raw);
        } catch (Exception e) {
            throw new IllegalArgumentException("상태 값이 올바르지 않습니다.");
        }
    }

    // 정책:
    // - stock==0 -> SOLD_OUT
    // - stock>0 + requested==SOLD_OUT -> ACTIVE로 자동 복귀
    private ProductStatus normalizeStatus(ProductStatus requested, Integer stock) {
        int s = (stock == null) ? 0 : stock;
        if (s == 0) return ProductStatus.SOLD_OUT;
        if (requested == ProductStatus.SOLD_OUT) return ProductStatus.ACTIVE;
        if (requested == ProductStatus.DELETED) {
            throw new IllegalArgumentException("DELETED 상태로 변경할 수 없습니다.");
        }
        return requested;
    }
}