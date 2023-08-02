package com.clothing.management.servicesImpl;

import com.clothing.management.entities.Offer;
import com.clothing.management.entities.Product;
import com.clothing.management.repository.IOfferRepository;
import com.clothing.management.repository.IProductRepository;
import com.clothing.management.services.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    IProductRepository productRepository;

    @Autowired
    IOfferRepository offerRepository;

    @Override
    public List<Product> findAllProducts() {
        return productRepository.findAll();
    }

    @Override
    public Optional<Product> findProductById(Long idProduct) {
        return productRepository.findById(idProduct);
    }

    @Override
    public Product findProductByReference(String reference) {
        return productRepository.findByReference(reference);
    }

    @Override
    public Product addProduct(Product product) {
        return productRepository.save(product);
    }

    @Override
    public Product updateProduct(Product product) {
        return productRepository.save(product);
    }

    @Override
    public void deleteProduct(Product product) {
        productRepository.delete(product);
    }

    @Override
    public void deleteSelectedProducts(List<Long> productsId) {
        productRepository.deleteAllById(productsId);
    }
}
