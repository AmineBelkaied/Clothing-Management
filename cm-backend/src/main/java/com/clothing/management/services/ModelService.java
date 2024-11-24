package com.clothing.management.services;

import com.clothing.management.dto.ModelDTO;
import com.clothing.management.dto.ModelDeleteDTO;
import com.clothing.management.entities.Model;
import com.clothing.management.models.ModelSaveResponse;

import java.util.List;
import java.util.Optional;

public interface ModelService {

    List<Model> findAllModels();
    Optional<Model> findModelById(Long idModel);
    ModelSaveResponse saveModel(ModelDTO modelDTO);
    void deleteModelById(Long idModel, boolean isSoftDelete);
    void deleteSelectedModels(List<Long> modelsId);
    List<ModelDTO> getModels();
    ModelDeleteDTO checkModelUsage(Long id);
    void rollBackModel(Long id);
}
