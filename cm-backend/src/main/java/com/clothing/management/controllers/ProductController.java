package com.clothing.management.controllers;

import com.clothing.management.dto.ProductQuantity;
import com.clothing.management.dto.StockDTO;
import com.clothing.management.entities.Product;
import com.clothing.management.services.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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
    public void getStock(@RequestBody List<ProductQuantity> products) {
        productService.addStock(products);
    }
}
