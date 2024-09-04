package com.clothing.management.exceptions.custom.alreadyexists;

import com.clothing.management.exceptions.generic.AlreadyExistsException;

public class FbPageAlreadyExistsException extends AlreadyExistsException {

    public FbPageAlreadyExistsException(Long fbPageId, String fbPageName) {
        super("FbPage", fbPageId, fbPageName);
    }
    public FbPageAlreadyExistsException(String fbPageName) {
        super("FbPage", fbPageName);
    }
}
