package com.clothing.management.services;

import com.clothing.management.dto.ProductDTO;
import com.clothing.management.dto.ProductHistoryDTO;
import com.clothing.management.dto.StockDTO;
import com.clothing.management.dto.StockUpdateDto;
import com.clothing.management.entities.ModelStockHistory;
import com.clothing.management.entities.Product;
import com.clothing.management.entities.ProductHistory;
import com.clothing.management.models.ModelsStockCount;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Optional;

public interface ProductService {
    
    public List<ProductDTO> findAllProducts();
    public Optional<Product> findProductById(Long idProduct);
    //public Product findProductByReference(String reference);
    public Product addProduct(Product product);
    public Product updateProduct(Product product);
    public void deleteProduct(Product product);
    void deleteSelectedProducts(List<Long> productsId);
    StockDTO getStock(Long modelId);
    List<ModelStockHistory> countStock();
    Page<ProductHistoryDTO> addStock(StockUpdateDto updateStock);
    Product findByModelAndColorAndSize(Long modelId, Long colorId, Long sizeId);

}
