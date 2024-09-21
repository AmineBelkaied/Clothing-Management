package com.clothing.management.exceptions.custom.notfound;

import com.clothing.management.exceptions.generic.EntityNotFoundException;

public class ModelNotFoundException extends EntityNotFoundException {

    public ModelNotFoundException(Long modelId) {
        super("Model", modelId);
    }

    public ModelNotFoundException(Long modelId, String modelName) {
        super("Model", modelId, modelName);
    }
}
