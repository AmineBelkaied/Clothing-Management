package com.clothing.management.services;

import com.clothing.management.dto.ProductQuantity;
import com.clothing.management.dto.StockDTO;
import com.clothing.management.entities.Product;
import com.clothing.management.entities.ProductHistory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface ProductHistoryService {

    public Page<ProductHistory> findAllProductsHistory(Long modelId, int page, int size, String reference);
    public List<ProductHistory> findAllProductsHistory(int limit, int skip);
    public Optional<ProductHistory> findProductHistoryById(Long idProductHistory);
    public ProductHistory addProductHistory(ProductHistory productHistory);
    public List<ProductHistory> addManyProductHistory(List<ProductHistory> productsHistory);
    public ProductHistory updateProductHistory(ProductHistory productHistory);
    public void deleteProductHistory(ProductHistory productHistory);
}
