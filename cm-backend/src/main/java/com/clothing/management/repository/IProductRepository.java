package com.clothing.management.repository;

import com.clothing.management.dto.ModelStockHistory;
import com.clothing.management.dto.ProductDTO;
import com.clothing.management.entities.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IProductRepository extends JpaRepository<Product, Long> {
    @Query(value="DELETE FROM `product` WHERE model_id = :modelId AND color_id = :colorId", nativeQuery = true)
    int deleteProductsByModelAndColor(@Param("modelId") Long modelId, @Param("colorId") Long colorsId);

    @Query(value="DELETE FROM `product` WHERE model_id = :modelId AND size_id = :sizeId", nativeQuery = true)
    int deleteProductsByModelAndSize(@Param("modelId") Long modelId,@Param("sizeId") Long sizeId);

    @Query(value = "SELECT " +
            "NEW com.clothing.management.dto.ModelStockHistory(p.model.id, p.model.name , SUM(p.quantity)) " +
            "FROM Product p GROUP BY p.model.id")
    List<ModelStockHistory> countStock();

    @Query(value = "SELECT p FROM Product p WHERE p.model.id IN :modelIds")
    List<Product> getProductsByModelIds(@Param("modelIds") List<Long> modelIds);

    @Query(value = "SELECT p.id FROM product p WHERE p.model = :modelId AND p.color = NULL AND p.size = NULL",nativeQuery = true)
    int findNullProductsByModelId(@Param("modelId") Long modelId);

    @Query(value = "SELECT * FROM product WHERE model_id = :modelId AND color_id = :colorId AND size_id = :sizeId LIMIT 1",nativeQuery = true)
    Product findByModelAndColorAndSize(@Param("modelId") Long modelId, @Param("colorId") Long colorId, @Param("sizeId") Long sizeId);

    @Query(value = "SELECT * FROM product WHERE model_id = :modelId",nativeQuery = true)
    List<Product> findByModel(@Param("modelId") Long modelId);

}
