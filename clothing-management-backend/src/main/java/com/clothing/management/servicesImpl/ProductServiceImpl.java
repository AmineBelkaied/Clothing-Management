package com.clothing.management.servicesImpl;

import com.clothing.management.dto.OfferModelDTO;
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

  /*  idPacket offerName modelName Qte Couleurs sizes
    1          2sd       sd       2    b,r      m,l
    2          ts        sc       2    bl,j     s,m
    2          ts        pu       1    bg        xl
    "2sd-sd-rm, 2sd[2sd(rm,bl)]  , ts[2sc(bl,js),1pu]  ,Pa PA n m ";
    public List<Product> findProductsFromPacket(String productsList) {
        String[] products = productsList.split(",");
        for(String product :products) {
            String[] offerProductRefs = product.split("-");
            Offer offer = offerRepository.findByName(offerProductRefs[0]);
            Product p = findProductByReference(offerProductRefs[1]);
            OfferModelDTO offerModelDTO = new OfferModelDTO(offer.getId() , offer.getName(), offer.getPrice() , true , null);


        }
    }*/

    @Override
    public Product updateProduct(Product product) {
        return productRepository.save(product);
    }

    @Override
    public void deleteProduct(Product product) {
        productRepository.delete(product);
    }
}
