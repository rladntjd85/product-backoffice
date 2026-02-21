// src/main/java/com/rladntjd85/backoffice/product/repository/ProductRepository.java
package com.rladntjd85.backoffice.product.repository;

import com.rladntjd85.backoffice.product.domain.Product;
import com.rladntjd85.backoffice.product.domain.ProductStatus;
import com.rladntjd85.backoffice.product.dto.AdminProductRow;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {

    boolean existsByCategoryId(Long categoryId);

    // ===== edit 화면에서 category 같이 필요 =====
    @EntityGraph(attributePaths = {"category"})
    @Query("select p from Product p where p.id = :id")
    Optional<Product> findWithCategory(@Param("id") Long id);

    // ===== 목록 검색 =====
    @Query(
            value = """
                        select new com.rladntjd85.backoffice.product.dto.AdminProductRow(
                            p.id,
                            p.name,
                            c.id,
                            c.name,
                            p.price,
                            p.stock,
                            cast(p.status as string),
                            p.thumbnailUrl,
                            p.createdAt
                        )
                        from Product p
                        join p.category c
                        where (
                            :q is null or :q = '' or
                            lower(p.name) like lower(concat('%', :q, '%')) or
                            cast(p.id as string) like concat('%', :q, '%')
                        )
                          and (:categoryId is null or c.id = :categoryId)
                          and (:status is null or :status = '' or cast(p.status as string) = :status)
                    """,
            countQuery = """
                        select count(p.id)
                        from Product p
                        join p.category c
                        where (
                            :q is null or :q = '' or
                            lower(p.name) like lower(concat('%', :q, '%')) or
                            cast(p.id as string) like concat('%', :q, '%')
                        )
                          and (:categoryId is null or c.id = :categoryId)
                          and (:status is null or :status = '' or cast(p.status as string) = :status)
                    """
    )
    Page<AdminProductRow> searchRows(@Param("q") String q,
                                     @Param("categoryId") Long categoryId,
                                     @Param("status") String status,
                                     Pageable pageable);

    // ===== 탭 배지용 count (필터 반영) =====
    @Query("""
                select count(p.id)
                from Product p
                join p.category c
                where (
                    :q is null or :q = '' or
                    lower(p.name) like lower(concat('%', :q, '%')) or
                    cast(p.id as string) like concat('%', :q, '%')
                )
                  and (:categoryId is null or c.id = :categoryId)
                  and (:status is null or :status = '' or cast(p.status as string) = :status)
            """)
    long countByFilter(@Param("q") String q,
                       @Param("categoryId") Long categoryId,
                       @Param("status") String status);

    long countByStatus(ProductStatus status);

    @Query("select count(p) from Product p")
    long countAllProducts();

    @Query("""
                select p from Product p
                where p.status <> com.rladntjd85.backoffice.product.domain.ProductStatus.DELETED
                  and p.stock <= :threshold
                order by p.stock asc, p.id desc
            """)
    List<Product> findLowStock(@Param("threshold") int threshold, Pageable pageable);

    @Query("""
                select p from Product p
                where p.status = com.rladntjd85.backoffice.product.domain.ProductStatus.SOLD_OUT
                order by p.id desc
            """)
    List<Product> findSoldOut(Pageable pageable);
}
