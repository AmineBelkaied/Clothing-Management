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

import java.util.*;

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
        return modelRepository.findAll();
    }

    @Override
    public Optional<Model> findModelById(Long idModel) {
        return modelRepository.findById(idModel);
    }

    @Override
    public Model addModel(Model model) {
        Color color1 = colorRepository.findByReference("?");
        model.getColors().add(color1);
        model.getSizes().add(sizeRepository.findByReference("?"));
      Model modelResponse = modelRepository.save(model);
      // Generate products
      if(model.getColors().size() > 0) {
          for(Color color : model.getColors()) {
              if(model.getSizes().size() > 0) {
                  for(Size size : model.getSizes()) {
                      String productRef = model.getReference().concat(color.getReference()).concat(size.getReference());
                      if(productRepository.findByReference(productRef) == null) {
                          Product product = new Product(productRef, size, color, 0, new Date(), null, modelResponse);
                          System.out.println("product -- " + productRef);
                          productRepository.save(product);
                      }
                  }
                }
            }
         }
        return modelResponse;
    }

    @Override
    public Model updateModel(Model model) { return addModel(model); }

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
