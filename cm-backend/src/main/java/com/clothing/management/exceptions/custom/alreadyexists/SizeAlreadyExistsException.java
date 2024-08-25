package com.clothing.management.exceptions.custom.alreadyexists;

import com.clothing.management.exceptions.generic.AlreadyExistsException;

public class SizeAlreadyExistsException extends AlreadyExistsException {

    public SizeAlreadyExistsException(Long sizeId, String sizeName) {
        super("Size", sizeId, sizeName);
    }
}
