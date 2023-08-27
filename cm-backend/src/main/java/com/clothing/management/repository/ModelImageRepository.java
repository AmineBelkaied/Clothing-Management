package com.clothing.management.repository;

import com.clothing.management.entities.ModelImage;
import com.clothing.management.entities.ProductsPacket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ModelImageRepository extends JpaRepository<ModelImage, Long> {

    Optional<ModelImage> findByName(String fileName);
    @Query(value = "select * from model_image  where model_id = :modelId", nativeQuery = true)
    Optional<ModelImage> findByModelId(@Param("modelId") Long modelId);
}