package com.clothing.management.repository;

import com.clothing.management.entities.Packet;
import com.clothing.management.entities.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IProductRepository extends JpaRepository<Product, Long> {
    Product findByReference(String offerProductRef);
    List<Product> findAllByModel(Long idModel);
    List<Product> findAllByReference(String offerProductRef);

    @Query(value="DELETE FROM `product` WHERE model_id = :modelId AND color_id = :colorId", nativeQuery = true)
    int deleteProductsByModelAndColor(@Param("modelId") Long modelId, @Param("colorId") Long colorsId);

    @Query(value="DELETE FROM `product` WHERE model_id = :modelId AND size_id = :sizeId", nativeQuery = true)
    int deleteProductsByModelAndSize(@Param("modelId") Long modelId,@Param("sizeId") Long sizeId);


}
