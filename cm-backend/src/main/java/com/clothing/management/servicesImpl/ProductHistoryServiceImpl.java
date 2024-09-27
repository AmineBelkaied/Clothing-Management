package com.clothing.management.servicesImpl;

import com.clothing.management.dto.ProductHistoryDTO;
import com.clothing.management.entities.Product;
import com.clothing.management.entities.ProductHistory;
import com.clothing.management.repository.IProductHistoryRepository;
import com.clothing.management.repository.IProductRepository;
import com.clothing.management.services.ProductHistoryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Optional;

@Service
public class ProductHistoryServiceImpl implements ProductHistoryService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProductHistoryServiceImpl.class);

    private final IProductHistoryRepository productHistoryRepository;
    private final IProductRepository productRepository;

    public ProductHistoryServiceImpl(IProductHistoryRepository productHistoryRepository, IProductRepository productRepository) {
        this.productHistoryRepository = productHistoryRepository;
        this.productRepository = productRepository;
    }

    @Override
    public Page<ProductHistoryDTO> findAllProductsHistory(Long modelId, int page, int size, String colorSize, String beginDate, String endDate) throws ParseException {

        Pageable paging = PageRequest.of(page, size, Sort.by("lastModificationDate").descending());

        if (beginDate.isEmpty() && endDate.isEmpty()) {
            Page<ProductHistoryDTO> result;
            if (colorSize.isEmpty()) {
                result = productHistoryRepository.findAll(modelId, paging);
            } else {
                result = productHistoryRepository.findAllByReference(modelId, colorSize, paging);
            }
            LOGGER.info("Fetched product history with filter. Result size: {}", result.getSize());
            return result;
        } else {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            Page<ProductHistoryDTO> result = productHistoryRepository.findAllByDateRange(
                    modelId, dateFormat.parse(beginDate), dateFormat.parse(endDate), paging);
            LOGGER.info("Fetched product history within date range. Result size: {}", result.getSize());
            return result;
        }
    }

    @Override
    public Optional<ProductHistory> findProductHistoryById(Long idProductHistory) {
        Optional<ProductHistory> productHistory = productHistoryRepository.findById(idProductHistory);
        if (productHistory.isPresent()) {
            LOGGER.info("Product history found with id: {}", idProductHistory);
        } else {
            LOGGER.warn("Product history not found with id: {}", idProductHistory);
        }
        return productHistory;
    }

    @Override
    public ProductHistory addProductHistory(ProductHistory productHistory) {
        ProductHistory savedProductHistory = productHistoryRepository.save(productHistory);
        LOGGER.info("Product history added with id: {}", savedProductHistory.getId());
        return savedProductHistory;
    }

    @Override
    public List<ProductHistory> addManyProductHistory(List<ProductHistory> productsHistory) {
        List<ProductHistory> savedProductHistories = productHistoryRepository.saveAll(productsHistory);
        LOGGER.info("Multiple product histories added. Number of entries: {}", savedProductHistories.size());
        return savedProductHistories;
    }

    @Override
    public ProductHistory updateProductHistory(ProductHistory productHistory) {
        ProductHistory updatedProductHistory = productHistoryRepository.save(productHistory);
        LOGGER.info("Product history updated with id: {}", updatedProductHistory.getId());
        return updatedProductHistory;
    }

    @Override
    public void deleteProductHistory(ProductHistory productHistory) {
        productHistoryRepository.delete(productHistory);
        LOGGER.info("Product history deleted with id: {}", productHistory.getId());
    }

    @Override
    public Page<ProductHistoryDTO> deleteProductsHistory(List<ProductHistory> productsHistory, Long modelId, int page) {
        productsHistory.forEach(productHistory -> {
            Optional<Product> optionalProduct = productRepository.findById(productHistory.getProduct().getId());
            optionalProduct.ifPresent(product -> {
                product.setQuantity(product.getQuantity() - productHistory.getQuantity());
                productRepository.save(product);
                LOGGER.info("Updated product quantity for productId: {}", product.getId());
            });
            productHistoryRepository.deleteById(productHistory.getId());
            LOGGER.info("Deleted product history with id: {}", productHistory.getId());
        });
        Pageable paging = PageRequest.of(page, 10, Sort.by("lastModificationDate").descending());
        Page<ProductHistoryDTO> result = productHistoryRepository.findAll(modelId, paging);
        LOGGER.info("Fetched updated product history after deletion. Result size: {}", result.getSize());
        return result;
    }

}
