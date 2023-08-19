package com.clothing.management.repository;

import com.clothing.management.entities.ProductHistory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IProductHistoryRepository extends JpaRepository<ProductHistory, Long> {
    @Query(value = "select * from product_history p order by p.last_modification_date LIMIT :limit OFFSET :skip", nativeQuery = true)
    public List<ProductHistory> findAllProductsHistory(@Param("limit") int limit, @Param("skip") int skip);

    @Query(value = "select * from product_history pr where pr.model_id = :modelId",
            countQuery = "select count(*) from product_history pr where pr.model_id = :modelId",
            nativeQuery = true)
    public Page<ProductHistory> findAll(@Param("modelId") Long modelId, Pageable pageable);

}
