package com.clothing.management.exceptions.custom.notfound;

import com.clothing.management.exceptions.generic.EntityNotFoundException;

public class ColorNotFoundException extends EntityNotFoundException {

    public ColorNotFoundException(Long colorId, String colorName) {
        super("Color", colorId, colorName);
    }
}
