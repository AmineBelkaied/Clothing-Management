package com.clothing.management.services;

import com.clothing.management.dto.*;
import com.clothing.management.dto.DayCount.ProductsQuantityDTO;
import com.clothing.management.dto.ProductDTO;
import com.clothing.management.dto.ProductHistoryDTO;
import com.clothing.management.dto.StockDTO;
import com.clothing.management.dto.StockUpdateDTO;
import com.clothing.management.entities.ModelStockHistory;
import com.clothing.management.entities.Product;
import com.clothing.management.entities.User;
import org.springframework.data.domain.Page;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public interface ProductService {

    List<ProductResponse> findAllProducts();
    List<ProductResponse> fingProductsByModelIds(ModelIdsRequest modelIds);

    int fingNullProductsByModelId(Long modelId);

    Optional<Product> findProductById(Long idProduct);
    Product addProduct(Product product);
    Product updateProduct(Product product);
    void deleteProduct(Product product);
    void deleteSelectedProducts(List<Long> productsId);
    StockDTO getStock(Long modelId, String beginDate, String endDate);
    List<ProductsQuantityDTO> getStockQuantity(Long modelId);
    List<ModelStockHistory> countStock();
    Page<ProductHistoryDTO> addStock(StockUpdateDTO updateStock);

}
