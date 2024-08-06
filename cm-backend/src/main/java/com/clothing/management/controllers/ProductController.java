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
@RequestMapping("product")
@CrossOrigin
@Secured({"ROLE_ADMIN", "ROLE_USER"})
public class ProductController {
    private final ProductService productService;

    public ProductController(ProductService productService){
        this.productService =productService;
    }

    @GetMapping(path = "/findAll")
    public List<ProductDTO> findAllProducts() {
        return productService.findAllProducts();
    }

    @GetMapping(path = "/findById/{id}")
    public Optional<Product> findByIdProduct(@PathVariable Long id) {
        return productService.findProductById(id);
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
    public ResponseEntity<ResponsePage>  getStock(@RequestBody StockUpdateDto updateIdSockList) {
        try {
            Page<ProductHistoryDTO> pageProductHistory = productService.addStock(updateIdSockList);
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
