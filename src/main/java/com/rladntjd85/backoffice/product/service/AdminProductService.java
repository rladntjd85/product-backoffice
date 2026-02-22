package com.rladntjd85.backoffice.product.service;

import com.rladntjd85.backoffice.audit.annotation.AuditLoggable;
import com.rladntjd85.backoffice.audit.annotation.AuditTargetId;
import com.rladntjd85.backoffice.category.domain.Category;
import com.rladntjd85.backoffice.category.repository.CategoryRepository;
import com.rladntjd85.backoffice.common.util.FileStorage;
import com.rladntjd85.backoffice.product.domain.Product;
import com.rladntjd85.backoffice.product.domain.ProductStatus;
import com.rladntjd85.backoffice.product.dto.AdminProductRow;
import com.rladntjd85.backoffice.product.dto.ProductForm;
import com.rladntjd85.backoffice.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class AdminProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final FileStorage fileStorage;

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

    /**
     * 상품 생성: AOP가 반환된 Long ID를 사용하여 생성 직후의 스냅샷을 기록합니다.
     */
    @Transactional
    @AuditLoggable(action = "PRODUCT_CREATED", targetType = "PRODUCT", entityClass = Product.class)
    public Long create(ProductForm form) {
        Category category = categoryRepository.findById(form.getCategoryId())
                .orElseThrow(() -> new IllegalArgumentException("카테고리를 찾을 수 없습니다."));

        if (!category.isEnabled()) {
            throw new IllegalArgumentException("비활성 카테고리에는 상품을 등록할 수 없습니다.");
        }

        ProductStatus status = normalizeStatus(parseStatus(form.getStatus()), form.getStock());

        // 썸네일 처리
        String thumbUrl = form.getThumbnailDirectUrl();
        String thumbOrg = null;
        if (form.getThumbnailFile() != null && !form.getThumbnailFile().isEmpty()) {
            var thumb = fileStorage.storeProductThumb(form.getThumbnailFile());
            if (thumb != null) {
                thumbUrl = thumb.url();
                thumbOrg = thumb.originalName();
            }
        }

        // 상세 이미지 처리
        String detailUrl = form.getDetailImageDirectUrl();
        String detailOrg = null;
        if (form.getDetailImageFile() != null && !form.getDetailImageFile().isEmpty()) {
            var detail = fileStorage.storeProductDetail(form.getDetailImageFile());
            if (detail != null) {
                detailUrl = detail.url();
                detailOrg = detail.originalName();
            }
        }

        Product p = Product.builder()
                .name(form.getName())
                .category(category)
                .price(form.getPrice())
                .stock(form.getStock())
                .status(status)
                .content(form.getContent())
                .thumbnailUrl(thumbUrl)
                .thumbnailOriginalName(thumbOrg)
                .detailImageUrl(detailUrl)
                .detailOriginalName(detailOrg)
                .build();

        productRepository.save(p);
        return p.getId(); // AOP가 이 값을 가로채서 CREATE 로그를 남깁니다.
    }

    /**
     * 상품 수정: AOP가 @AuditTargetId를 기반으로 전/후 데이터를 자동 비교합니다.
     */
    @Transactional
    @AuditLoggable(action = "PRODUCT_UPDATED", targetType = "PRODUCT", entityClass = Product.class)
    public void update(@AuditTargetId Long id, ProductForm form) {
        Product p = getWithCategory(id);

        // 1. 기존 본문(HTML)에서 이미지 URL 추출
        List<String> oldImages = extractImageUrls(p.getContent());

        // 2. 새 본문(HTML)에서 이미지 URL 추출
        List<String> newImages = extractImageUrls(form.getContent());

        // 3. 삭제 대상 찾기 (기존에는 있었는데 새 본문에는 없는 URL)
        List<String> imagesToDelete = oldImages.stream()
                .filter(url -> !newImages.contains(url))
                .filter(url -> url.startsWith("/uploads")) // 외부 URL 제외
                .toList();

        // 4. 실제 파일 삭제
        imagesToDelete.forEach(fileStorage::deleteByUrl);

        if (p.getStatus() == ProductStatus.DELETED) {
            throw new IllegalArgumentException("삭제된 상품은 수정할 수 없습니다.");
        }

        Category category = categoryRepository.findById(form.getCategoryId())
                .orElseThrow(() -> new IllegalArgumentException("카테고리를 찾을 수 없습니다."));

        if (!category.isEnabled()) {
            throw new IllegalArgumentException("비활성 카테고리로 변경할 수 없습니다.");
        }

        // 기본 정보 업데이트
        ProductStatus status = normalizeStatus(parseStatus(form.getStatus()), form.getStock());
        p.updateBasic(form.getName(), form.getPrice(), form.getStock(), status, category, form.getContent());

        // 썸네일 교체
        updateThumbnail(p, form);

        // 상세 이미지 교체
        updateDetailImage(p, form);
    }

    @Transactional
    @AuditLoggable(action = "PRODUCT_STATUS_CHANGED", targetType = "PRODUCT", entityClass = Product.class)
    public void hide(@AuditTargetId Long id) {
        Product p = getWithCategory(id);
        if (p.getStatus() != ProductStatus.ACTIVE) {
            throw new IllegalArgumentException("판매중인 상품만 판매중지할 수 있습니다.");
        }
        p.updateBasic(p.getName(), p.getPrice(), p.getStock(), ProductStatus.HIDDEN, p.getCategory(), p.getContent());
    }

    @Transactional
    @AuditLoggable(action = "PRODUCT_STATUS_CHANGED", targetType = "PRODUCT", entityClass = Product.class)
    public void unhide(@AuditTargetId Long id) {
        Product p = getWithCategory(id);
        if (p.getStatus() != ProductStatus.HIDDEN) {
            throw new IllegalArgumentException("판매중지 상태에서만 판매재개할 수 있습니다.");
        }
        ProductStatus next = normalizeStatus(ProductStatus.ACTIVE, p.getStock());
        p.updateBasic(p.getName(), p.getPrice(), p.getStock(), next, p.getCategory(), p.getContent());
    }

    @Transactional
    @AuditLoggable(action = "PRODUCT_DELETED", targetType = "PRODUCT", entityClass = Product.class)
    public void softDelete(@AuditTargetId Long id) {
        Product p = productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("상품을 찾을 수 없습니다."));
        if (p.getStatus() == ProductStatus.DELETED) return;
        p.softDelete();
    }

    @Transactional
    @AuditLoggable(action = "PRODUCT_THUMB_REMOVED", targetType = "PRODUCT", entityClass = Product.class)
    public void removeThumbnail(@AuditTargetId Long id) {
        Product p = getWithCategory(id);
        fileStorage.deleteByUrl(p.getThumbnailUrl());
        p.clearThumbnail();
    }

    @Transactional
    @AuditLoggable(action = "PRODUCT_DETAIL_REMOVED", targetType = "PRODUCT", entityClass = Product.class)
    public void removeDetailImage(@AuditTargetId Long id) {
        Product p = getWithCategory(id);
        fileStorage.deleteByUrl(p.getDetailImageUrl());
        p.clearDetailImage();
    }

    // --- 헬퍼 메서드들 ---

    private void updateThumbnail(Product p, ProductForm form) {
        String newUrl = p.getThumbnailUrl();
        String newOrg = p.getThumbnailOriginalName();

        if (form.getThumbnailFile() != null && !form.getThumbnailFile().isEmpty()) {
            var thumb = fileStorage.storeProductThumb(form.getThumbnailFile());
            if (thumb != null) {
                fileStorage.deleteByUrl(p.getThumbnailUrl());
                newUrl = thumb.url();
                newOrg = thumb.originalName();
            }
        } else if (form.getThumbnailDirectUrl() != null && !form.getThumbnailDirectUrl().isBlank()) {
            if (!Objects.equals(p.getThumbnailUrl(), form.getThumbnailDirectUrl())) {
                fileStorage.deleteByUrl(p.getThumbnailUrl());
                newUrl = form.getThumbnailDirectUrl();
                newOrg = null;
            }
        }
        p.updateImages(newUrl, newOrg, p.getDetailImageUrl(), p.getDetailOriginalName());
    }

    private void updateDetailImage(Product p, ProductForm form) {
        String newUrl = p.getDetailImageUrl();
        String newOrg = p.getDetailOriginalName();

        if (form.getDetailImageFile() != null && !form.getDetailImageFile().isEmpty()) {
            var detail = fileStorage.storeProductDetail(form.getDetailImageFile());
            if (detail != null) {
                fileStorage.deleteByUrl(p.getDetailImageUrl());
                newUrl = detail.url();
                newOrg = detail.originalName();
            }
        } else if (form.getDetailImageDirectUrl() != null && !form.getDetailImageDirectUrl().isBlank()) {
            if (!Objects.equals(p.getDetailImageUrl(), form.getDetailImageDirectUrl())) {
                fileStorage.deleteByUrl(p.getDetailImageUrl());
                newUrl = form.getDetailImageDirectUrl();
                newOrg = null;
            }
        }
        p.updateImages(p.getThumbnailUrl(), p.getThumbnailOriginalName(), newUrl, newOrg);
    }

    private ProductStatus parseStatus(String raw) {
        try {
            return ProductStatus.valueOf(raw);
        } catch (Exception e) {
            throw new IllegalArgumentException("상태 값이 올바르지 않습니다.");
        }
    }

    private ProductStatus normalizeStatus(ProductStatus requested, Integer stock) {
        int s = (stock == null) ? 0 : stock;
        if (s == 0) return ProductStatus.SOLD_OUT;
        if (requested == ProductStatus.SOLD_OUT) return ProductStatus.ACTIVE;
        if (requested == ProductStatus.DELETED) {
            throw new IllegalArgumentException("DELETED 상태로 변경할 수 없습니다.");
        }
        return requested;
    }
    /**
     * 정규표현식을 사용하여 HTML 태그 내 src 경로를 모두 추출
     */
    private List<String> extractImageUrls(String html) {
        if (html == null || html.isBlank()) return List.of();
        List<String> urls = new ArrayList<>();
        Pattern pattern = Pattern.compile("<img[^>]*src=[\"']([^\"']+)[\"']", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(html);
        while (matcher.find()) {
            urls.add(matcher.group(1));
        }
        return urls;
    }
}