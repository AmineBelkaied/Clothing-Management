package com.clothing.management.repository;

import com.clothing.management.dto.ProductHistoryDTO;
import com.clothing.management.entities.ProductHistory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface IProductHistoryRepository extends JpaRepository<ProductHistory, Long> {
    @Query(value = "select * from product_history p order by p.last_modification_date LIMIT :limit OFFSET :skip", nativeQuery = true)
    public List<ProductHistory> findAllProductsHistory(@Param("limit") int limit, @Param("skip") int skip);

    @Query(value = "SELECT NEW com.clothing.management.dto.ProductHistoryDTO("
            + "pr.id, "
            + "pr.product.id, "
            + "CONCAT(pr.product.color.name, ' ', pr.product.size.reference), "
            + "pr.model.id, "
            + "pr.quantity, "
            + "pr.lastModificationDate, "
            + "pr.user,"
            + "pr.comment) "
            + "FROM ProductHistory pr "
            + "WHERE pr.model.id = :modelId")
    Page<ProductHistoryDTO> findAll(@Param("modelId") Long modelId, Pageable pageable);

    @Query(value = "SELECT NEW com.clothing.management.dto.ProductHistoryDTO("
            + "pr.id, "
            + "pr.product.id, "
            + "CONCAT(pr.product.color.name, ' ', pr.product.size.reference), "
            + "pr.model.id, "
            + "pr.quantity, "
            + "pr.lastModificationDate, "
            + "pr.user,"
            + "pr.comment) "
            + "FROM ProductHistory pr "
            + "WHERE pr.model.id = :modelId "
            + "AND DATE(pr.lastModificationDate) >= DATE(:beginDate) "
            + "AND DATE(pr.lastModificationDate) <= DATE(:endDate)")
    Page<ProductHistoryDTO> findAllByDateRange(
            @Param("modelId") Long modelId,
            @Param("beginDate") Date beginDate,
            @Param("endDate") Date endDate,
            Pageable pageable);

    @Query(value = "SELECT NEW com.clothing.management.dto.ProductHistoryDTO("
            + "pr.id, "
            + "pr.product.id, "
            + "CONCAT(pr.product.color.name, ' ', pr.product.size.reference), "
            + "pr.model.id, "
            + "pr.quantity, "
            + "pr.lastModificationDate, "
            + "pr.user,"
            + "pr.comment) "
            + "FROM ProductHistory pr "
            + "WHERE pr.model.id = :modelId "
            + "AND (pr.user.userName LIKE %:reference% "
            + "OR CONCAT(pr.product.color.name,' ', pr.product.size.reference) LIKE %:reference%)")
    Page<ProductHistoryDTO> findAllByReference(@Param("modelId") Long modelId, @Param("reference") String reference, Pageable pageable);

}