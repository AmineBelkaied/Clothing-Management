package com.clothing.management.controllers;

import com.clothing.management.dto.ProductDTO;
import com.clothing.management.dto.ProductHistoryDTO;
import com.clothing.management.dto.StockDTO;
import com.clothing.management.dto.StockUpdateDto;
import com.clothing.management.entities.Product;
import com.clothing.management.models.ResponsePage;
import com.clothing.management.services.ProductService;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("${api.prefix}/products")
@CrossOrigin
@Secured({"ROLE_ADMIN", "ROLE_USER"})
public class ProductController {
    private final ProductService productService;

    public ProductController(ProductService productService){
        this.productService =productService;
    }

    @GetMapping
    public ResponseEntity<List<ProductDTO>> getAllProducts() {
        List<ProductDTO> products = productService.findAllProducts();
        return ResponseEntity.ok(products);
    }

    @GetMapping("/{productId}")
    public ResponseEntity<Product> getProductById(@PathVariable("productId") Long productId) {
        Optional<Product> product = productService.findProductById(productId);
        return product.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @PostMapping
    public ResponseEntity<Product> createProduct(@RequestBody Product product) {
        Product createdProduct = productService.addProduct(product);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdProduct);
    }

    @PutMapping
    public ResponseEntity<Product> updateProduct(@RequestBody Product product) {
        Product updatedProduct = productService.updateProduct(product);
        return ResponseEntity.ok(updatedProduct);
    }

    @DeleteMapping
    public void deleteProduct(@RequestBody Product product) {
        productService.deleteProduct(product);
    }

    @DeleteMapping("/batch-delete")
    public ResponseEntity<Void> deleteSelectedProducts(@RequestBody List<Long> productIds) {
        productService.deleteSelectedProducts(productIds);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/stock/{modelId}")
    public ResponseEntity<StockDTO> getStock(@PathVariable("modelId") Long modelId,
                                             @RequestParam String beginDate,
                                             @RequestParam String endDate) {
        StockDTO stock = productService.getStock(modelId,beginDate,endDate);
        return ResponseEntity.ok(stock);
    }

    @PostMapping("/stock")
    public ResponseEntity<ResponsePage> addStock(@RequestBody StockUpdateDto stockUpdateDto) {
        try {
            Page<ProductHistoryDTO> pageProductHistory = productService.addStock(stockUpdateDto);
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