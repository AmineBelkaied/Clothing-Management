package com.clothing.management.services;

import com.clothing.management.dto.ProductDTO;
import com.clothing.management.dto.ProductHistoryDTO;
import com.clothing.management.dto.StockDTO;
import com.clothing.management.dto.StockUpdateDto;
import com.clothing.management.dto.ModelStockHistory;
import com.clothing.management.entities.Product;
import com.clothing.management.entities.User;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Optional;

public interface ProductService {
    
    List<ProductDTO> findAllProducts();
    Optional<Product> findProductById(Long idProduct);
    Product addProduct(Product product);
    Product updateProduct(Product product);
    void deleteProduct(Product product);
    void deleteSelectedProducts(List<Long> productsId);
    StockDTO getStock(Long modelId, String beginDate, String endDate);
    List<ModelStockHistory> countStock();
    Page<ProductHistoryDTO> addStock(StockUpdateDto updateStock);

}
