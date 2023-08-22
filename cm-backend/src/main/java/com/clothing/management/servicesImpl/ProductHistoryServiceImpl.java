package com.clothing.management.servicesImpl;

import com.clothing.management.entities.Product;
import com.clothing.management.entities.ProductHistory;
import com.clothing.management.repository.IProductHistoryRepository;
import com.clothing.management.repository.IProductRepository;
import com.clothing.management.services.ProductHistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ProductHistoryServiceImpl implements ProductHistoryService {

    @Autowired
    IProductHistoryRepository productHistoryRepository;

    @Autowired
    IProductRepository productRepository;

    @Override
    public Page<ProductHistory> findAllProductsHistory(Long modelId , int page, int size, String reference, String beginDate, String endDate) {
        Pageable paging = PageRequest.of(page, size, Sort.by("last_modification_date").descending());
        if(beginDate.isEmpty() && endDate.isEmpty()) {
            if(reference.isEmpty())
                return productHistoryRepository.findAll(modelId, paging);
            else
                return productHistoryRepository.findAllByReference(modelId, reference, paging);
        } else {
            if(!reference.isEmpty())
                return productHistoryRepository.findAllByDateRangeAndReference(modelId, reference, beginDate, endDate, paging);
            return productHistoryRepository.findAllByDateRange(modelId, beginDate, endDate, paging);
        }
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

    @Override
    public Page<ProductHistory> deleteProductsHistory(List<ProductHistory> productsHistory, Long modelId, int page) {
        productsHistory.forEach(productHistory -> {
           Optional<Product> optionalProduct = productRepository.findById(productHistory.getProductId());
           optionalProduct.ifPresent(product -> {
               product.setQuantity(product.getQuantity() - productHistory.getQuantity());
               productRepository.save(product);
           });
           productHistoryRepository.deleteById(productHistory.getId());
        });
        Pageable paging = PageRequest.of(page, 10, Sort.by("last_modification_date").descending());
        return productHistoryRepository.findAll(modelId, paging);
    }

}
