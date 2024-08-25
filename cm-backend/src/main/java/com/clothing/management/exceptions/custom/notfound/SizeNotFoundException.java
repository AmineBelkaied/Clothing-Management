package com.clothing.management.exceptions.custom.notfound;

import com.clothing.management.exceptions.generic.EntityNotFoundException;

public class SizeNotFoundException extends EntityNotFoundException {

    public SizeNotFoundException(Long sizeId, String sizeName) {
        super("Size", sizeId, sizeName);
    }
}
