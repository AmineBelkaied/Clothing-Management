package com.clothing.management.controllers;

import com.clothing.management.dto.ProductHistoryDTO;
import com.clothing.management.entities.ProductHistory;
import com.clothing.management.models.ResponsePage;
import com.clothing.management.services.ProductHistoryService;
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

    public ProductHistoryController(ProductHistoryService productHistoryService){
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
        try {
            Page<ProductHistoryDTO> pageProductHistory = productHistoryService.findAllProductsHistory(modelId, page, size, colorSize, beginDate, endDate);
            ResponsePage responsePage = new ResponsePage.Builder()
                    .result(pageProductHistory.getContent())
                    .currentPage(pageProductHistory.getNumber())
                    .totalItems(pageProductHistory.getTotalElements())
                    .totalPages(pageProductHistory.getTotalPages())
                    .build();
            return ResponseEntity.ok(responsePage);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{historyId}")
    public ResponseEntity<ProductHistory> getProductHistoryById(@PathVariable("historyId") Long historyId) {
        Optional<ProductHistory> productHistory = productHistoryService.findProductHistoryById(historyId);
        return productHistory.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @PostMapping
    public ResponseEntity<ProductHistory> createProductHistory(@RequestBody ProductHistory productHistory) {
        ProductHistory createdProductHistory = productHistoryService.addProductHistory(productHistory);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdProductHistory);
    }

    @PostMapping("/batch-add")
    public ResponseEntity<List<ProductHistory>> createProductHistories(@RequestBody List<ProductHistory> productHistories) {
        List<ProductHistory> createdProductHistories = productHistoryService.addManyProductHistory(productHistories);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdProductHistories);
    }

    @PutMapping
    public ResponseEntity<ProductHistory> updateProductHistory(@RequestBody ProductHistory productHistory) {
        ProductHistory updatedProductHistory = productHistoryService.updateProductHistory(productHistory);
        return ResponseEntity.ok(updatedProductHistory);
    }

    @DeleteMapping
    public ResponseEntity<Object> deleteProductHistory(@RequestBody ProductHistory productHistory) {
        productHistoryService.deleteProductHistory(productHistory);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/batch-delete/{modelId}")
    public ResponseEntity<ResponsePage> deleteProductHistories(
            @RequestBody List<ProductHistory> productHistories,
            @PathVariable("modelId") Long modelId,
            @RequestParam(defaultValue = "0") int page
    ) {
        try {
            Page<ProductHistoryDTO> pageProductHistory = productHistoryService.deleteProductsHistory(productHistories, modelId, page);
            ResponsePage responsePage = new ResponsePage.Builder()
                    .result(pageProductHistory.getContent())
                    .currentPage(pageProductHistory.getNumber())
                    .totalItems(pageProductHistory.getTotalElements())
                    .totalPages(pageProductHistory.getTotalPages())
                    .build();
            return ResponseEntity.ok(responsePage);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
