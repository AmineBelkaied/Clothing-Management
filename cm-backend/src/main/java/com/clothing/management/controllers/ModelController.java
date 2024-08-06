package com.clothing.management.controllers;

import com.clothing.management.dto.ModelDTO;
import com.clothing.management.entities.Model;
import com.clothing.management.services.ModelService;
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
    public ModelController(ModelService modelService){
        this.modelService = modelService;
    }

    @GetMapping("/find-all")
    public ResponseEntity<List<Model>> getAllModels() {
        List<Model> models = modelService.findAllModels();
        return new ResponseEntity<>(models, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Model> getModelById(@PathVariable Long id) {
        return modelService.findModelById(id)
                .map(model -> new ResponseEntity<>(model, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PostMapping
    public ResponseEntity<Model> createModel(@RequestBody Model model) {
        Model createdModel = modelService.saveModel(model);
        return new ResponseEntity<>(createdModel, HttpStatus.CREATED);
    }

    @PutMapping
    public ResponseEntity<Model> updateModel(@RequestBody Model model) {
        Model updatedModel = modelService.saveModel(model);
        return new ResponseEntity<>(updatedModel, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteModelById(@PathVariable Long id) {
        modelService.deleteModelById(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping("/batch-delete")
    public ResponseEntity<Void> deleteSelectedModels(@RequestBody List<Long> modelIds) {
        modelService.deleteSelectedModels(modelIds);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping
    public List<ModelDTO> getModels() {
        return modelService.getModels();
    }
}
