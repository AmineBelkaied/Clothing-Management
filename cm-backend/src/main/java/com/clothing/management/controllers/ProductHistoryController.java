package com.clothing.management.controllers;

import com.clothing.management.dto.ProductHistoryDTO;
import com.clothing.management.entities.ProductHistory;
import com.clothing.management.models.ResponsePage;
import com.clothing.management.services.ProductHistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("productHistory")
@CrossOrigin
@Secured({"ROLE_ADMIN", "ROLE_USER"})
public class ProductHistoryController {

    @Autowired
    ProductHistoryService productHistoryService;

    @GetMapping(path = "/findAllByModelId/{modelId}")
    public ResponseEntity<ResponsePage> findAllProductsHistory(
        @PathVariable Long modelId,
        @RequestParam(required = false) String beginDate,
        @RequestParam(required = false) String endDate,
        @RequestParam(required = false) String colorSize,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size
      ) {
            try {
                Page<ProductHistoryDTO> pageProductHistory = productHistoryService.findAllProductsHistory(modelId, page, size, colorSize, beginDate, endDate);
                return new ResponseEntity<>(new ResponsePage.Builder()
                        .result(pageProductHistory.getContent())
                        .currentPage(pageProductHistory.getNumber())
                        .totalItems(pageProductHistory.getTotalElements())
                        .totalPages(pageProductHistory.getTotalPages())
                        .build(), HttpStatus.OK);
            } catch (Exception e) {
                return new ResponseEntity<>(new ResponsePage.Builder().build(), HttpStatus.INTERNAL_SERVER_ERROR);
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

    @PostMapping(value = "/deleteProductsHistory/{modelId}" , produces = "application/json")
    public ResponseEntity<ResponsePage> deleteProductHistory(@RequestBody List<ProductHistory> productsHistory,@PathVariable("modelId")Long modelId, @RequestParam int page) {
        try {
            Page<ProductHistoryDTO> pageProductHistory = productHistoryService.deleteProductsHistory(productsHistory, modelId, page);
            return new ResponseEntity<>(new ResponsePage.Builder()
                    .result(pageProductHistory.getContent())
                    .currentPage(pageProductHistory.getNumber())
                    .totalItems(pageProductHistory.getTotalElements())
                    .totalPages(pageProductHistory.getTotalPages())
                    .build(), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(new ResponsePage.Builder().build(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
