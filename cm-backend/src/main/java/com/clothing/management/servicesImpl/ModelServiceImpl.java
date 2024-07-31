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
import jakarta.persistence.EntityNotFoundException;
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
            model  = modelRepository.save(model);
            model  = addUnknownColorsAndSizes(model);
            // Generate products
            generateModelProducts(model);

        //deleteUnusedProducts(model);
        return model;
    }

    @Override
    public Model generateModelProducts(Model model) {
        try {
            // Generate products
            if(model.getColors().size() > 0) {
                System.out.println("model colors:"+model.getColors());
                for(Color color : model.getColors()) {
                    if(model.getSizes().size() > 0) {
                        for(Size size : model.getSizes()) {
                            Product product1 = productRepository.findByModelAndColorAndSize(model.getId(), color.getId(), size.getId());
                            if( product1 == null) {
                                //System.out.println("color:"+color.getId());
                                //String productRef = model.getReference().concat(color.getReference()).concat(size.getReference());
                                Product product = new Product( size, color, 0, new Date(), model);
                                productRepository.save(product);
                            }
                        }
                    }
                }
            }
        } catch (EntityNotFoundException e) {

            System.out.println(e);
        }

        //deleteUnusedProducts(model);
        return model;
    }

    public void deleteUnusedProducts(Model model) {
        Model oldModel = modelRepository.findById(model.getId()).orElse(null);

        if (oldModel != null) {
            List<Size> oldSizes = oldModel.getSizes();
            List<Color> oldColors = oldModel.getColors();
            for (Size size : oldSizes) {
                if (size != null && !model.getSizes().stream().map(Size::getId).anyMatch(id -> id.equals(size.getId()))) {
                    System.out.println("delete size"+size.getId());
                    productRepository.deleteProductsByModelAndSize(model.getId(), size.getId());
                }
            }
            for (Color color : oldColors) {
                if (color == null && !model.getColors().contains(color) && !color.getReference().equals("?")) {
                    System.out.println("delete color"+color.getId());
                    productRepository.deleteProductsByModelAndColor(model.getId(), color.getId());
                }
            }
        }

    }

    private Model addUnknownColorsAndSizes(Model model) {

        Optional<Model> optionalModel= modelRepository.findById(model.getId());
        if(optionalModel != null)
        {
            model = optionalModel.get();
            if(model.getColors().stream().noneMatch(color -> color.getReference().equals("?"))
                    && model.getSizes().stream().noneMatch(size -> size.getReference().equals("?"))) {
                model.getColors().add(colorRepository.findByReference("?"));
                model.getSizes().add(sizeRepository.findByReference("?"));
            }
        }
        return modelRepository.save(model);
    }

    @Override
    public Model updateModel(Model model) {
        //deleteUnusedProducts(model);
        return addModel(model);
    }

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
