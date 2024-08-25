package com.clothing.management.exceptions.custom.notfound;

import com.clothing.management.exceptions.generic.EntityNotFoundException;

public class GovernorateNotFoundException extends EntityNotFoundException {

    public GovernorateNotFoundException(Long governorateId, String governorateName) {
        super("Governorate", governorateId, governorateName);
    }
}
