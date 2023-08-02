package com.clothing.management.repository;

import com.clothing.management.entities.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IProductRepository extends JpaRepository<Product, Long> {
    Product findByReference(String offerProductRef);
    List<Product> findAllByModel(Long idModel);
    List<Product> findAllByReference(String offerProductRef);
}
