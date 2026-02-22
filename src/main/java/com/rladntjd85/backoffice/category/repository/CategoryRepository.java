package com.rladntjd85.backoffice.category.repository;

import com.rladntjd85.backoffice.category.domain.Category;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    boolean existsByNameIgnoreCase(String name);

    // 특정 부모 아래에 같은 이름이 있는지 확인
    boolean existsByParentAndNameIgnoreCase(Category parent, String name);

    // 1차 카테고리만 조회 (부모 선택용)
    List<Category> findByParentIsNullAndEnabledTrueOrderByNameAsc();

    @Query("""
                select c from Category c
                left join fetch c.parent
                where (:q is null or :q = '' or lower(c.name) like lower(concat('%', :q, '%')))
                  and (:status is null or :status = '' or 
                      (:status = 'ACTIVE' and c.enabled = true) or 
                      (:status = 'INACTIVE' and c.enabled = false))
                order by 
                    coalesce(c.parent.id, c.id) asc, 
                    case when c.parent.id is null then 0 else 1 end asc, 
                    c.id asc
            """)
    Page<Category> search(@Param("q") String q, @Param("status") String status, Pageable pageable);

    @Query("""
            select count(c.id)
            from Category c
            where (:q is null or :q = '' or lower(c.name) like lower(concat('%', :q, '%')))
            """)
    long countAllByQ(@Param("q") String q);

    @Query("""
            select count(c.id)
            from Category c
            where c.enabled = true
              and (:q is null or :q = '' or lower(c.name) like lower(concat('%', :q, '%')))
            """)
    long countActiveByQ(@Param("q") String q);

    @Query("""
            select count(c.id)
            from Category c
            where c.enabled = false
              and (:q is null or :q = '' or lower(c.name) like lower(concat('%', :q, '%')))
            """)
    long countInactiveByQ(@Param("q") String q);

    // 부모가 없고, 활성화 상태이며, 특정 ID가 아닌 카테고리 목록 조회
    List<Category> findByParentIsNullAndEnabledTrueAndIdNotOrderByNameAsc(Long id);

    // 1차 카테고리 조회 (부모가 없는 카테고리)
    List<Category> findByParentIdIsNull();

    // 특정 부모 ID를 가진 하위 카테고리 조회
    List<Category> findByParentId(Long parentId);

    // 활성화된 1차 카테고리만 가져오고 싶을 경우 (선택 사항)
    List<Category> findByParentIdIsNullAndEnabledTrue();


}
