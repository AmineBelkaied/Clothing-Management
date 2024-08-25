package com.clothing.management.exceptions.custom.alreadyexists;

import com.clothing.management.exceptions.generic.AlreadyExistsException;

public class ProductAlreadyExistsException extends AlreadyExistsException {

    public ProductAlreadyExistsException(Long productId) {
        super("Product", productId);
    }
}
