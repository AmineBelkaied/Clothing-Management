package com.clothing.management.servicesImpl;

import com.clothing.management.entities.Color;
import com.clothing.management.entities.Model;
import com.clothing.management.entities.Product;
import com.clothing.management.entities.Size;
import com.clothing.management.repository.IColorRepository;
import com.clothing.management.repository.IModelRepository;
import com.clothing.management.repository.IProductRepository;
import com.clothing.management.repository.ISizeRepository;
import com.clothing.management.services.ModelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ModelServiceImpl implements ModelService {

    @Autowired
    IModelRepository modelRepository;

    @Autowired
    IColorRepository colorRepository;

    @Autowired
    ISizeRepository sizeRepository;
    @Autowired
    IProductRepository productRepository;

    @Override
    public List<Model> findAllModels() {
        return modelRepository.findAll().stream().peek(model ->
        {
            try {
                if (model.getImage() != null)
                    model.setBytes(Files.readAllBytes(new File(model.getImage().getImagePath()).toPath()));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }).collect(Collectors.toList());
    }

    @Override
    public Optional<Model> findModelById(Long idModel) {
        return modelRepository.findById(idModel);
    }

    @Override
    public Model addModel(Model model) {
      addUnknownColorsAndSizes(model);
      Model modelResponse = modelRepository.save(model);
      // Generate products
      if(model.getColors().size() > 0) {
          for(Color color : model.getColors()) {
              if(model.getSizes().size() > 0) {
                  for(Size size : model.getSizes()) {
                      String productRef = model.getReference().concat(color.getReference()).concat(size.getReference());
                      if(productRepository.findByReference(productRef) == null) {
                          Product product = new Product(productRef, size, color, 0, new Date(), null, modelResponse);
                          productRepository.save(product);
                      }
                  }
                }
            }
         }
        return modelResponse;
    }
    public void deleteUnusedProducts(Model model) {
        Model oldModel = modelRepository.findById(model.getId()).get();
        List<Size> oldSizes = oldModel.getSizes();
        List<Color> oldColors = oldModel.getColors();
        for (Size size : oldSizes) {
            if (size != null && !model.getSizes().stream().map(Size::getId).anyMatch(id -> id.equals(size.getId()))) {
                productRepository.deleteProductsByModelAndSize(model.getId(), size.getId());
            }
        }
        for (Color color : oldColors) {
            if (color == null && !model.getColors().contains(color) && !color.getReference().equals("?")) {
                productRepository.deleteProductsByModelAndColor(model.getId(), color.getId());
            }
        }
    }

    private void addUnknownColorsAndSizes(Model model) {
        if(model.getColors().stream().noneMatch(color -> color.getReference().equals("?"))
                && model.getSizes().stream().noneMatch(size -> size.getReference().equals("?"))) {
            model.getColors().add(colorRepository.findByReference("?"));
            model.getSizes().add(sizeRepository.findByReference("?"));
        }
    }

    @Override
    public Model updateModel(Model model) {
        //deleteUnusedProducts(model);
        return addModel(model); }

    @Override
    public void deleteModelById(Long idModel) {
        modelRepository.deleteById(idModel);
    }

    /**
     * Delete selected models by id
     * @param modelsId
     */
    @Override
    public void deleteSelectedModels(List<Long> modelsId) {
        modelRepository.deleteAllById(modelsId);
    }
}
