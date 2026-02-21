package com.rladntjd85.backoffice.product.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
public class ProductForm {

    @NotBlank(message = "상품명을 입력하세요.")
    private String name;

    @NotNull(message = "카테고리를 선택하세요.")
    private Long categoryId;

    @NotNull(message = "가격을 입력하세요.")
    @Positive(message = "가격은 1 이상이어야 합니다.")
    private Integer price;

    @NotNull
    @Min(0)
    private Integer stock;

    /**
     * ACTIVE / HIDDEN / SOLD_OUT
     * (DELETED는 폼에서 받지 않음)
     */
    @NotBlank(message = "상태를 선택하세요.")
    private String status;

    // 업로드 파일(선택)
    private MultipartFile thumbnailFile;
    private MultipartFile detailImageFile;

    public static ProductForm empty() {
        ProductForm f = new ProductForm();
        f.setStock(0);
        f.setStatus("ACTIVE");
        return f;
    }
}