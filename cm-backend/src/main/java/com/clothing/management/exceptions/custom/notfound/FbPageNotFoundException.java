package com.clothing.management.exceptions.custom.notfound;

import com.clothing.management.exceptions.generic.EntityNotFoundException;

public class FbPageNotFoundException extends EntityNotFoundException {

    public FbPageNotFoundException(Long fbPageId, String fbPageName) {
        super("FbPage", fbPageId, fbPageName);
    }
}
