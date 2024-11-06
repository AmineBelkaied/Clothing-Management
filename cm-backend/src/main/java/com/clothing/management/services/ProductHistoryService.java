package com.clothing.management.services;

import com.clothing.management.dto.ProductHistoryDTO;
import com.clothing.management.entities.ProductHistory;
import org.springframework.data.domain.Page;

import java.text.ParseException;
import java.util.List;
import java.util.Optional;

public interface ProductHistoryService {

    Page<ProductHistoryDTO> findAllProductsHistory(Long modelId, int page, int size, String colorSize) throws ParseException;
    Optional<ProductHistory> findProductHistoryById(Long idProductHistory);
    ProductHistory addProductHistory(ProductHistory productHistory);
    List<ProductHistory> addManyProductHistory(List<ProductHistory> productsHistory);
    ProductHistory updateProductHistory(ProductHistory productHistory);
    void deleteProductHistory(ProductHistory productHistory);
    Page<ProductHistoryDTO> deleteProductsHistory(List<ProductHistoryDTO> productsHistory, Long modelId,String colorSize, int page);
}
