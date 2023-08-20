package com.clothing.management.controllers;

import com.clothing.management.entities.ProductHistory;
import com.clothing.management.services.ProductHistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("productHistory")
@CrossOrigin
public class ProductHistoryController {

    @Autowired
    ProductHistoryService productHistoryService;

    @GetMapping(path = "/findAllByModelId/{modelId}")
    public ResponseEntity<Map<String, Object>> findAllProductsHistory(
        @PathVariable Long modelId,
        @RequestParam(required = false) String reference,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "6") int size
      ) {
            try {
                System.out.println(reference);
                Page<ProductHistory> pageProductHistory = productHistoryService.findAllProductsHistory(modelId, page, size, reference);
                Map<String, Object> response = new HashMap<>();
                response.put("productHistories", pageProductHistory.getContent());
                response.put("currentPage", pageProductHistory.getNumber());
                response.put("totalItems", pageProductHistory.getTotalElements());
                response.put("totalPages", pageProductHistory.getTotalPages());

                return new ResponseEntity<>(response, HttpStatus.OK);
            } catch (Exception e) {
                return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
            }
    }
    @GetMapping(path = "/findById/{id}")
    public Optional<ProductHistory> findByIdProductHistory(@PathVariable Long idProductHistory) {
        return productHistoryService.findProductHistoryById(idProductHistory);
    }

    @PostMapping(value = "/add" , produces = "application/json")
    public ProductHistory addProductHistory(@RequestBody ProductHistory productHistory) {
        return productHistoryService.addProductHistory(productHistory);
    }

    @PostMapping(value = "/addProductsHistory" , produces = "application/json")
    public List<ProductHistory> addManyProductsHistory(@RequestBody List<ProductHistory> productsHistory) {
        return productHistoryService.addManyProductHistory(productsHistory);
    }

    @PutMapping(value = "/update" , produces = "application/json")
    public ProductHistory updateProductHistory(@RequestBody ProductHistory productHistory) {
        return productHistoryService.updateProductHistory(productHistory);
    }

    @DeleteMapping(value = "/delete" , produces = "application/json")
    public void deleteProductHistory(@RequestBody ProductHistory productHistory) {
        productHistoryService.deleteProductHistory(productHistory);
    }
}
