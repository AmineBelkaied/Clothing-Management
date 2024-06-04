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



    /*@Query(value = "select * from product_history pr where pr.model_id = :modelId",
            countQuery = "select count(*) from product_history pr where pr.model_id = :modelId",
            nativeQuery = true)
    public Page<ProductHistory> findAll(@Param("modelId") Long modelId, Pageable pageable);*/

    @Query(value = "SELECT NEW com.clothing.management.dto.ProductHistoryDTO("
            + "pr.id, "
            + "CONCAT(pr.product.color.name, ' ', pr.product.size.reference), "
            + "pr.model.id, "
            + "pr.quantity, "
            + "pr.lastModificationDate, "
            + "pr.user,"
            + "pr.comment) "
            + "FROM ProductHistory pr "
            + "WHERE pr.model.id = :modelId")
    public Page<ProductHistoryDTO> findAll(@Param("modelId") Long modelId, Pageable pageable);


    /*@Query(value = "select * from product_history pr where pr.model_id = :modelId and pr.reference LIKE %:reference%",
            countQuery = "select count(*) from product_history pr where pr.model_id = :modelId and pr.reference LIKE %:reference%",
            nativeQuery = true)
    public Page<ProductHistory> findAllByReference(@Param("modelId") Long modelId, @Param("reference") String reference, Pageable pageable);*/

    @Query(value = "SELECT NEW com.clothing.management.dto.ProductHistoryDTO("
            + "pr.id, "
            + "CONCAT(pr.product.color.name, ' ', pr.product.size.reference), "
            + "pr.model.id, "
            + "pr.quantity, "
            + "pr.lastModificationDate, "
            + "pr.user,"
            + "pr.comment) "
            + "FROM ProductHistory pr "
            + "WHERE pr.model.id = :modelId AND CONCAT(pr.product.color.name, ' ', pr.product.size.reference) LIKE %:reference%")
    public Page<ProductHistoryDTO> findAllByReference(@Param("modelId") Long modelId, @Param("reference") String reference, Pageable pageable);


    @Query(value = "SELECT NEW com.clothing.management.dto.ProductHistoryDTO("
            + "pr.id, "
            + "CONCAT(pr.product.color.name, ' ', pr.product.size.reference), "
            + "pr.model.id, "
            + "pr.quantity, "
            + "pr.lastModificationDate, "
            + "pr.user,"
            + "pr.comment) "
            + "FROM ProductHistory pr "
            + "WHERE pr.model.id = :modelId "
            + "AND DATE(pr.lastModificationDate) >= :beginDate "
            + "AND DATE(pr.lastModificationDate) <= :endDate")
    public Page<ProductHistoryDTO> findAllByDateRange(
            @Param("modelId") Long modelId,
            @Param("beginDate") Date beginDate,
            @Param("endDate") Date endDate,
            Pageable pageable);

    @Query(value = "select * from product_history pr where pr.model_id = :modelId and pr.reference LIKE %:reference% and DATE(pr.last_modification_date) >= :beginDate and DATE(pr.last_modification_date) <= :endDate",
            countQuery = "select count(*) from product_history pr where pr.model_id = :modelId and pr.reference LIKE %:reference% and DATE(pr.last_modification_date) >= :beginDate and DATE(pr.last_modification_date) <= :endDate",
            nativeQuery = true)
    public Page<ProductHistory> findAllByDateRangeAndReference(@Param("modelId") Long modelId, @Param("reference") String reference, @Param("beginDate") String beginDate, @Param("endDate") String endDate, Pageable pageable);
}