package com.rladntjd85.backoffice.category.repository;

import com.rladntjd85.backoffice.category.domain.Category;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    boolean existsByNameIgnoreCase(String name);

    @Query("""
                select c
                from Category c
                where (:q is null or :q = '' or lower(c.name) like lower(concat('%', :q, '%')))
                  and (
                    :status is null or :status = '' or
                    (:status = 'ACTIVE' and c.enabled = true) or
                    (:status = 'INACTIVE' and c.enabled = false)
                  )
                order by c.id desc
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

    List<Category> findByEnabledTrueOrderByNameAsc();

}
