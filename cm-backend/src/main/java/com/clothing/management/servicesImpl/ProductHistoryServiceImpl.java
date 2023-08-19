package com.clothing.management.servicesImpl;

import com.clothing.management.entities.ProductHistory;
import com.clothing.management.repository.IProductHistoryRepository;
import com.clothing.management.services.ProductHistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProductHistoryServiceImpl implements ProductHistoryService {

    @Autowired
    IProductHistoryRepository productHistoryRepository;

    @Override
    public Page<ProductHistory> findAllProductsHistory(Long modelId , Pageable pageable) {
        return productHistoryRepository.findAll(modelId, pageable);
    }
    @Override
    public List<ProductHistory> findAllProductsHistory(int limit, int skip) {
        return productHistoryRepository.findAllProductsHistory(limit, skip);
    }

    @Override
    public Optional<ProductHistory> findProductHistoryById(Long idProductHistory) {
        return productHistoryRepository.findById(idProductHistory);
    }

    @Override
    public ProductHistory addProductHistory(ProductHistory productHistory) {
        return productHistoryRepository.save(productHistory);
    }

    @Override
    public List<ProductHistory> addManyProductHistory(List<ProductHistory> productsHistory) {
        return productHistoryRepository.saveAll(productsHistory);
    }

    @Override
    public ProductHistory updateProductHistory(ProductHistory productHistory) {
        return productHistoryRepository.save(productHistory);
    }

    @Override
    public void deleteProductHistory(ProductHistory productHistory) {
        productHistoryRepository.delete(productHistory);
    }

}
