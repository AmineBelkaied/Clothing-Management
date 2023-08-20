package com.clothing.management.controllers;

import com.clothing.management.dto.ProductQuantity;
import com.clothing.management.dto.StockDTO;
import com.clothing.management.dto.StockUpdateDto;
import com.clothing.management.entities.Product;
import com.clothing.management.entities.ProductHistory;
import com.clothing.management.services.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("product")
@CrossOrigin
public class ProductController {

    @Autowired
    ProductService productService;

    @GetMapping(path = "/findAll")
    public List<Product> findAllProducts() {
        return productService.findAllProducts();
    }

    @GetMapping(path = "/findById/{id}")
    public Optional<Product> findByIdProduct(@PathVariable Long idProduct) {
        return productService.findProductById(idProduct);
    }

    @PostMapping(value = "/add" , produces = "application/json")
    public Product addProduct(@RequestBody  Product product) {
        return productService.addProduct(product);
    }

    @PutMapping(value = "/update" , produces = "application/json")
    public Product updateProduct(@RequestBody Product product) {
        return productService.updateProduct(product);
    }

    @DeleteMapping(value = "/delete" , produces = "application/json")
    public void deleteProduct(@RequestBody Product product) {
        productService.deleteProduct(product);
    }

    @DeleteMapping(value = "/deleteSelectedProducts/{productsId}" , produces = "application/json")
    public void deleteSelectedPackets(@PathVariable List<Long> productsId) {
        productService.deleteSelectedProducts(productsId);
    }

    @GetMapping(path = "/getStock/{modelId}")
    public StockDTO getStock(@PathVariable Long modelId) {
        return productService.getStock(modelId);
    }

    @PostMapping(path = "/addStock" , produces = "application/json")
    public ResponseEntity<Map<String, Object>>  getStock(@RequestBody StockUpdateDto updateIdSockList) {
        try {
            Page<ProductHistory> pageProductHistory = productService.addStock(updateIdSockList);
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
}
