package com.clothing.management.services;

import com.clothing.management.entities.Product;

import java.util.List;
import java.util.Optional;

public interface ProductService {
    
    public List<Product> findAllProducts();
    public Optional<Product> findProductById(Long idProduct);
    public Product findProductByReference(String reference);
    public Product addProduct(Product product);
    public Product updateProduct(Product product);
    public void deleteProduct(Product product);
    void deleteSelectedProducts(List<Long> productsId);
}
