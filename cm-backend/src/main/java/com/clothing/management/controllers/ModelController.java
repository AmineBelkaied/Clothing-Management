package com.clothing.management.controllers;

import com.clothing.management.dto.ModelDTO;
import com.clothing.management.entities.Model;
import com.clothing.management.services.ModelService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("${api.prefix}/models")
@CrossOrigin
@Secured({"ROLE_ADMIN", "ROLE_USER"})
public class ModelController {

    private final ModelService modelService;
    private static final Logger LOGGER = LoggerFactory.getLogger(ModelController.class);

    public ModelController(ModelService modelService) {
        this.modelService = modelService;
    }

    @GetMapping("/find-all")
    public ResponseEntity<List<Model>> getAllModels() {
        LOGGER.info("Fetching all models.");
        List<Model> models = modelService.findAllModels();
        LOGGER.info("Successfully fetched {} models.", models.size());
        return new ResponseEntity<>(models, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Model> getModelById(@PathVariable Long id) {
        LOGGER.info("Fetching model with id: {}", id);
        return modelService.findModelById(id)
                .map(model -> {
                    LOGGER.info("Model found: {}", model);
                    return new ResponseEntity<>(model, HttpStatus.OK);
                })
                .orElseGet(() -> {
                    LOGGER.warn("Model with id: {} not found.", id);
                    return new ResponseEntity<>(HttpStatus.NOT_FOUND);
                });
    }

    @PostMapping
    public ResponseEntity<ModelDTO> createModel(@RequestBody Model model) {
        LOGGER.info("Creating new model: {}", model);
        ModelDTO createdModel = modelService.saveModel(model);
        LOGGER.info("Model created successfully: {}", createdModel);
        if(model.getId() != null) {
            return new ResponseEntity<>(createdModel, HttpStatus.OK);
        }
        return new ResponseEntity<>(createdModel, HttpStatus.CREATED);
    }

    @PutMapping
    public ResponseEntity<ModelDTO> updateModel(@RequestBody Model model) {
        LOGGER.info("Updating model: {}", model);
        ModelDTO updatedModel = modelService.saveModel(model);
        LOGGER.info("Model updated successfully: {}", updatedModel);
        return new ResponseEntity<>(updatedModel, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteModelById(@PathVariable Long id) {
        LOGGER.info("Deleting model with id: {}", id);
        modelService.deleteModelById(id);
        LOGGER.info("Model with id: {} deleted successfully.", id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping("/batch-delete")
    public ResponseEntity<Void> deleteSelectedModels(@RequestBody List<Long> modelIds) {
        LOGGER.info("Batch deleting models with ids: {}", modelIds);
        modelService.deleteSelectedModels(modelIds);
        LOGGER.info("Successfully deleted models with ids: {}", modelIds);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping
    public List<ModelDTO> getModels() {
        LOGGER.info("Fetching models as DTOs.");
        List<ModelDTO> modelDTOs = modelService.getModels();
        LOGGER.info("Successfully fetched {} model DTOs.", modelDTOs.size());
        return modelDTOs;
    }
}
