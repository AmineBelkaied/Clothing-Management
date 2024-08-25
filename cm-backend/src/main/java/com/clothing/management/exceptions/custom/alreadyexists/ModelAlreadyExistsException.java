package com.clothing.management.exceptions.custom.alreadyexists;

import com.clothing.management.exceptions.generic.AlreadyExistsException;

public class ModelAlreadyExistsException extends AlreadyExistsException {

    public ModelAlreadyExistsException(Long modelId, String modelName) {
        super("Model", modelId, modelName);
    }
}
