package com.clothing.management.controllers;
import com.clothing.management.dto.StatDTO.ProductsQuantityDTO;
import com.clothing.management.dto.ProductHistoryDTO;
import com.clothing.management.dto.StockDTO;
import com.clothing.management.dto.StockUpdateDTO;
import com.clothing.management.dto.*;
import com.clothing.management.entities.Product;
import com.clothing.management.models.ResponsePage;
import com.clothing.management.services.ProductService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@RestController
@RequestMapping("${api.prefix}/products")
@CrossOrigin
@Secured({"ROLE_ADMIN", "ROLE_USER"})
public class ProductController {
    private final ProductService productService;
    private static final Logger LOGGER = LoggerFactory.getLogger(ProductController.class);

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public ResponseEntity<List<ProductResponse>> getAllProducts() {
        LOGGER.info("Fetching all products.");
        try {
            List<ProductResponse> products = productService.findAllProducts();
            LOGGER.info("Successfully fetched {} products.", products.size());
            return ResponseEntity.ok(products);
        } catch (Exception e) {
            LOGGER.error("Error fetching products: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/byModelIds")
    public ResponseEntity<List<ProductResponse>> getProductsByModelIds(@RequestBody ModelIdsRequest modelIds) {
        LOGGER.info("Fetching products by model IDs: {}", modelIds);
        try {
            List<ProductResponse> products = productService.fingProductsByModelIds(modelIds);
            LOGGER.info("Successfully fetched {} products for model IDs: {}", products.size(), modelIds);
            return ResponseEntity.ok(products);
        } catch (Exception e) {
            LOGGER.error("Error fetching products by model IDs: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{productId}")
    public ResponseEntity<Product> getProductById(@PathVariable("productId") Long productId) {
        LOGGER.info("Fetching product with ID: {}", productId);
        return productService.findProductById(productId)
                .map(product -> {
                    LOGGER.info("Successfully fetched product: {}", product);
                    return ResponseEntity.ok(product);
                })
                .orElseGet(() -> {
                    LOGGER.warn("Product with ID: {} not found.", productId);
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
                });
    }

    @PostMapping
    public ResponseEntity<Product> createProduct(@RequestBody Product product) {
        LOGGER.info("Creating new product: {}", product);
        try {
            Product createdProduct = productService.addProduct(product);
            LOGGER.info("Product created successfully: {}", createdProduct);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdProduct);
        } catch (Exception e) {
            LOGGER.error("Error creating product: ", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @PutMapping
    public ResponseEntity<Product> updateProduct(@RequestBody Product product) {
        LOGGER.info("Updating product: {}", product);
        try {
            Product updatedProduct = productService.updateProduct(product);
            LOGGER.info("Product updated successfully: {}", updatedProduct);
            return ResponseEntity.ok(updatedProduct);
        } catch (Exception e) {
            LOGGER.error("Error updating product: ", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteProduct(@RequestBody Product product) {
        LOGGER.info("Deleting product: {}", product);
        try {
            productService.deleteProduct(product);
            LOGGER.info("Product deleted successfully: {}", product);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            LOGGER.error("Error deleting product: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/batch-delete")
    public ResponseEntity<Void> deleteSelectedProducts(@RequestBody List<Long> productIds) {
        LOGGER.info("Deleting products with IDs: {}", productIds);
        try {
            productService.deleteSelectedProducts(productIds);
            LOGGER.info("Successfully deleted products with IDs: {}", productIds);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            LOGGER.error("Error deleting products with IDs: {}: ", productIds, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/stock/{modelId}")
    public ResponseEntity<StockDTO> getStock(@PathVariable("modelId") Long modelId,
                                             @RequestParam String beginDate,
                                             @RequestParam String endDate) {
        LOGGER.info("Fetching stock for modelId: {}, from {} to {}", modelId, beginDate, endDate);
        try {
            StockDTO stock = productService.getStock(modelId, beginDate, endDate);
            LOGGER.info("Successfully fetched stock for modelId: {}", modelId);
            return ResponseEntity.ok(stock);
        } catch (Exception e) {
            LOGGER.error("Error fetching stock for modelId: {}: ", modelId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/stock-quantity/{modelId}")
    public ResponseEntity<List<ProductsQuantityDTO>> getStockQuantity(@PathVariable("modelId") Long modelId) {
        try {
            List<ProductsQuantityDTO> stock = productService.getStockQuantity(modelId);
            return ResponseEntity.ok(stock);
        } catch (Exception e) {
            LOGGER.error("Error fetching stock for modelId: {}: ", modelId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/stock")
    public ResponseEntity<ResponsePage> addStock(@RequestBody StockUpdateDTO stockUpdateDto) {
        LOGGER.info("Adding stock with details: {}", stockUpdateDto);
        try {
            Page<ProductHistoryDTO> pageProductHistory = productService.addStock(stockUpdateDto);
            ResponsePage responsePage = new ResponsePage.Builder()
                    .result(pageProductHistory.getContent())
                    .currentPage(pageProductHistory.getNumber())
                    .totalItems(pageProductHistory.getTotalElements())
                    .totalPages(pageProductHistory.getTotalPages())
                    .build();
            LOGGER.info("Successfully added stock and created response page.");
            return ResponseEntity.ok(responsePage);
        } catch (Exception e) {
            LOGGER.error("Error adding stock: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
