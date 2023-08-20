package com.clothing.management.services;

import com.clothing.management.dto.ProductQuantity;
import com.clothing.management.dto.StockDTO;
import com.clothing.management.dto.StockUpdateDto;
import com.clothing.management.entities.Product;
import com.clothing.management.entities.ProductHistory;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface ProductService {
    
    public List<Product> findAllProducts();
    public Optional<Product> findProductById(Long idProduct);
    public Product findProductByReference(String reference);
    public Product addProduct(Product product);
    public Product updateProduct(Product product);
    public void deleteProduct(Product product);
    void deleteSelectedProducts(List<Long> productsId);
    StockDTO getStock(Long modelId);
    Page<ProductHistory> addStock(StockUpdateDto updateStock);
    //void addStock(List<ProductQuantity> products);
}
