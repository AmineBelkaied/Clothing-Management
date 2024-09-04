package com.clothing.management.services;

import com.clothing.management.dto.ModelDTO;
import com.clothing.management.entities.Model;

import java.util.List;
import java.util.Optional;

public interface ModelService {

    List<Model> findAllModels();
    Optional<Model> findModelById(Long idModel);
    Model saveModel(Model model);
    Model generateModelProducts(Model model);
    void deleteModelById(Long idModel);
    void deleteSelectedModels(List<Long> modelsId);
    List<ModelDTO> getModels();
}
