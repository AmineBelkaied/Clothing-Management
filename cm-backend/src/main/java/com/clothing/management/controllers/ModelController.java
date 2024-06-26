package com.clothing.management.controllers;

import com.clothing.management.entities.Model;
import com.clothing.management.services.ModelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("model")
@CrossOrigin
public class ModelController {

    @Autowired
    ModelService modelService;

    @GetMapping(path = "/findAll")
    public List<Model> findAllModels() {
        return modelService.findAllModels();
    }

    @GetMapping(path = "/findById/{id}")
    public Optional<Model> findByIdModel(@PathVariable Long idModel) {
        return modelService.findModelById(idModel);
    }

    @PostMapping(value = "/add" , produces = "application/json")
    public Model addModel(@RequestBody  Model model) {
        return modelService.addModel(model);
    }

    @PutMapping(value = "/update" , produces = "application/json")
    public Model updateModel(@RequestBody Model model) {
        return modelService.updateModel(model);
    }

    @DeleteMapping(value = "/deleteById/{idModel}")
    public void deleteModelById(@PathVariable Long idModel) {
        modelService.deleteModelById(idModel);
    }

    @DeleteMapping(value = "/deleteSelectedModels/{modelsId}" , produces = "application/json")
    public void deleteSelectedModels(@PathVariable List<Long> modelsId) { modelService.deleteSelectedModels(modelsId);
    }
}
