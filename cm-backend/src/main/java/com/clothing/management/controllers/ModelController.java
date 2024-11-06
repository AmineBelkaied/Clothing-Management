package com.clothing.management.controllers;

import com.clothing.management.dto.ModelDTO;
import com.clothing.management.dto.ModelDeleteDTO;
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
    public ResponseEntity<ModelDTO> createModel(@RequestBody ModelDTO modelDTO) {
        LOGGER.info("Creating new model: {}", modelDTO);
        ModelDTO createdModel = modelService.saveModel(modelDTO);
        LOGGER.info("Model created successfully: {}", createdModel);
        if(modelDTO.getId() != null) {
            return new ResponseEntity<>(createdModel, HttpStatus.OK);
        }
        return new ResponseEntity<>(createdModel, HttpStatus.CREATED);
    }

    @PutMapping
    public ResponseEntity<ModelDTO> updateModel(@RequestBody ModelDTO modelDTO) {
        LOGGER.info("Updating model: {}", modelDTO);
        ModelDTO updatedModel = modelService.saveModel(modelDTO);
        LOGGER.info("Model updated successfully: {}", updatedModel);
        return new ResponseEntity<>(updatedModel, HttpStatus.OK);
    }

    @GetMapping("/check-model-usage/{id}")
    public ResponseEntity<ModelDeleteDTO> checkModelUsage(@PathVariable Long id) {
        LOGGER.info("Checking model with id: {}", id);
        ModelDeleteDTO modelDeleteDTO = modelService.checkModelUsage(id);
        LOGGER.info("Model with id used : {} .", modelDeleteDTO.getUsedOffersCount());
        return new ResponseEntity<>(modelDeleteDTO, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteModelById(@PathVariable Long id, @RequestParam("soft-delete") boolean isSoftDelete) {
        LOGGER.info("Deleting model with id: {}", id);
        modelService.deleteModelById(id, isSoftDelete);
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

    @DeleteMapping("/rollback/{id}")
    public ResponseEntity<Void> rollBackModel(@PathVariable Long id) {
        LOGGER.info("Rollback model with id: {}", id);
        modelService.rollBackModel(id);
        LOGGER.info("Model with id: {} rolled back successfully.", id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
