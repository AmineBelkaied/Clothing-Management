package com.clothing.management.exceptions.custom.alreadyexists;

import com.clothing.management.exceptions.generic.AlreadyExistsException;

public class ColorAlreadyExistsException extends AlreadyExistsException {

    public ColorAlreadyExistsException(Long colorId, String colorName) {
        super("Color", colorId, colorName);
    }
}
