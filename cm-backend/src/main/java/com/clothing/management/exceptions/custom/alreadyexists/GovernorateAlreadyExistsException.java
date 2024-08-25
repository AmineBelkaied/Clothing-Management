package com.clothing.management.exceptions.custom.alreadyexists;

import com.clothing.management.exceptions.generic.AlreadyExistsException;

public class GovernorateAlreadyExistsException extends AlreadyExistsException {

    public GovernorateAlreadyExistsException(Long governorateId, String governorateName) {
        super("Governorate", governorateId, governorateName);
    }
}
