package com.clothing.management.services;

import com.clothing.management.entities.Model;

import java.util.List;
import java.util.Optional;

public interface ModelService {

    public List<Model> findAllModels();
    public Optional<Model> findModelById(Long idModel);
    public Model addModel(Model model);
    public Model updateModel(Model model);
    public Model generateModelProducts(Model model);
    public void deleteModelById(Long idModel);
    void deleteSelectedModels(List<Long> modelsId);
}
