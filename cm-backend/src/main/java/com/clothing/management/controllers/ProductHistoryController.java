package com.clothing.management.controllers;

import com.clothing.management.dto.ProductHistoryDTO;
import com.clothing.management.entities.ProductHistory;
import com.clothing.management.models.ResponsePage;
import com.clothing.management.services.ProductHistoryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("${api.prefix}/product-histories")
@CrossOrigin
@Secured({"ROLE_ADMIN", "ROLE_USER"})
public class ProductHistoryController {

    private final ProductHistoryService productHistoryService;
    private static final Logger LOGGER = LoggerFactory.getLogger(ProductHistoryController.class);

    public ProductHistoryController(ProductHistoryService productHistoryService) {
        this.productHistoryService = productHistoryService;
    }

    @GetMapping("/model/{modelId}")
    public ResponseEntity<ResponsePage> getProductHistoryByModelId(
            @PathVariable("modelId") Long modelId,
            @RequestParam(required = false) String beginDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(required = false) String colorSize,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        LOGGER.info("Fetching product history for modelId: {}, from {} to {}, colorSize: {}, page: {}, size: {}",
                modelId, beginDate, endDate, colorSize, page, size);
        try {
            Page<ProductHistoryDTO> pageProductHistory = productHistoryService.findAllProductsHistory(
                    modelId, page, size, colorSize);
            ResponsePage responsePage = new ResponsePage.Builder()
                    .result(pageProductHistory.getContent())
                    .currentPage(pageProductHistory.getNumber())
                    .totalItems(pageProductHistory.getTotalElements())
                    .totalPages(pageProductHistory.getTotalPages())
                    .build();
            LOGGER.info("Successfully fetched product history for modelId: {}", modelId);
            return ResponseEntity.ok(responsePage);
        } catch (Exception e) {
            LOGGER.error("Error fetching product history for modelId: {}: ", modelId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{historyId}")
    public ResponseEntity<ProductHistory> getProductHistoryById(@PathVariable("historyId") Long historyId) {
        LOGGER.info("Fetching product history with ID: {}", historyId);
        return productHistoryService.findProductHistoryById(historyId)
                .map(productHistory -> {
                    LOGGER.info("Successfully fetched product history: {}", productHistory);
                    return ResponseEntity.ok(productHistory);
                })
                .orElseGet(() -> {
                    LOGGER.warn("Product history with ID: {} not found.", historyId);
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
                });
    }

    @PostMapping
    public ResponseEntity<ProductHistory> createProductHistory(@RequestBody ProductHistory productHistory) {
        LOGGER.info("Creating new product history: {}", productHistory);
        try {
            ProductHistory createdProductHistory = productHistoryService.addProductHistory(productHistory);
            LOGGER.info("Successfully created product history: {}", createdProductHistory);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdProductHistory);
        } catch (Exception e) {
            LOGGER.error("Error creating product history: ", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @PostMapping("/batch-add")
    public ResponseEntity<List<ProductHistory>> createProductHistories(@RequestBody List<ProductHistory> productHistories) {
        LOGGER.info("Creating batch of product histories: {}", productHistories);
        try {
            List<ProductHistory> createdProductHistories = productHistoryService.addManyProductHistory(productHistories);
            LOGGER.info("Successfully created batch of product histories.");
            return ResponseEntity.status(HttpStatus.CREATED).body(createdProductHistories);
        } catch (Exception e) {
            LOGGER.error("Error creating batch of product histories: ", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @PutMapping
    public ResponseEntity<ProductHistory> updateProductHistory(@RequestBody ProductHistory productHistory) {
        LOGGER.info("Updating product history: {}", productHistory);
        try {
            ProductHistory updatedProductHistory = productHistoryService.updateProductHistory(productHistory);
            LOGGER.info("Successfully updated product history: {}", updatedProductHistory);
            return ResponseEntity.ok(updatedProductHistory);
        } catch (Exception e) {
            LOGGER.error("Error updating product history: ", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteProductHistory(
            @RequestBody ProductHistory productHistory,
            @RequestParam(required = false) String colorSize) {
        LOGGER.info("Deleting product history: {}", productHistory);
        try {
            productHistoryService.deleteProductHistory(productHistory);
            LOGGER.info("Successfully deleted product history: {}", productHistory);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            LOGGER.error("Error deleting product history: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/batch-delete/{modelId}")
    public ResponseEntity<ResponsePage> deleteProductHistories(
            @RequestBody List<ProductHistoryDTO> productHistories,
            @PathVariable("modelId") Long modelId,
            @RequestParam(required = false) String colorSize,
            @RequestParam(defaultValue = "0") int page
    ) {
        LOGGER.info("Deleting batch of product histories for modelId: {}: {}", modelId, productHistories);
        try {
            Page<ProductHistoryDTO> pageProductHistory = productHistoryService.deleteProductsHistory(
                    productHistories, modelId,colorSize, page);
            ResponsePage responsePage = new ResponsePage.Builder()
                    .result(pageProductHistory.getContent())
                    .currentPage(pageProductHistory.getNumber())
                    .totalItems(pageProductHistory.getTotalElements())
                    .totalPages(pageProductHistory.getTotalPages())
                    .build();
            LOGGER.info("Successfully deleted batch of product histories for modelId: {}", modelId);
            return ResponseEntity.ok(responsePage);
        } catch (Exception e) {
            LOGGER.error("Error deleting batch of product histories for modelId: {}: ", modelId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
