package com.clothing.management.exceptions.custom.notfound;

import com.clothing.management.exceptions.generic.EntityNotFoundException;

public class ProductNotFoundException extends EntityNotFoundException {

    public ProductNotFoundException(Long productId) {
        super("Product", productId);
    }
}
