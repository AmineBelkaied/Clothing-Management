package com.clothing.management.servicesImpl;

import com.clothing.management.dto.ModelDTO;
import com.clothing.management.entities.Color;
import com.clothing.management.entities.Model;
import com.clothing.management.entities.Product;
import com.clothing.management.entities.Size;
import com.clothing.management.exceptions.custom.alreadyexists.ModelAlreadyExistsException;
import com.clothing.management.repository.IColorRepository;
import com.clothing.management.repository.IModelRepository;
import com.clothing.management.repository.IProductRepository;
import com.clothing.management.repository.ISizeRepository;
import com.clothing.management.services.ModelService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class ModelServiceImpl implements ModelService {

    private final IModelRepository modelRepository;
    private final IColorRepository colorRepository;
    private final ISizeRepository sizeRepository;
    private final IProductRepository productRepository;
    
    public ModelServiceImpl(IModelRepository modelRepository, IColorRepository colorRepository,
                            ISizeRepository sizeRepository, IProductRepository productRepository) {
        this.modelRepository = modelRepository;
        this.colorRepository = colorRepository;
        this.sizeRepository = sizeRepository;
        this.productRepository = productRepository;
    }

    @Override
    public List<Model> findAllModels() {
        return modelRepository.findAll();
    }

    @Override
    public Optional<Model> findModelById(Long idModel) {
        return modelRepository.findById(idModel);
    }

    @Override
    public Model saveModel(Model model) {
        modelRepository.findByNameIsIgnoreCase(model.getName())
                .ifPresent(existingModel -> {
                    throw new ModelAlreadyExistsException(existingModel.getId(), existingModel.getName());
                });
        model = modelRepository.save(model);
        model = addUnknownColorsAndSizes(model);
        // Generate products
        generateModelProducts(model);
        
        return model;
    }

    @Override
    public Model generateModelProducts(Model model) {
        try {
            // Generate products
            if(!model.getColors().isEmpty()) {
                for(Color color : model.getColors()) {
                    if(!model.getSizes().isEmpty()) {
                        for(Size size : model.getSizes()) {
                            Product product1 = productRepository.findByModelAndColorAndSize(model.getId(), color.getId(), size.getId());
                            if( product1 == null) {
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

    private Model addUnknownColorsAndSizes(Model model) {
        return modelRepository.findById(model.getId()).map(existingModel -> {
            if (existingModel.getColors().stream().noneMatch(color -> color.getReference().equals("?"))
                    && existingModel.getSizes().stream().noneMatch(size -> size.getReference().equals("?"))) {
                existingModel.getColors().add(colorRepository.findByReferenceIsIgnoreCase("?").get());
                existingModel.getSizes().add(sizeRepository.findByReferenceIsIgnoreCase("?").get());
            }
            return modelRepository.save(existingModel);
        }).orElse(model);
    }

    @Override
    public void deleteModelById(Long idModel) {
        modelRepository.deleteById(idModel);
    }

    /**
     * Delete selected models by their IDs.
     *
     * @param modelsId a list of IDs representing the models to be deleted
     */
    @Override
    public void deleteSelectedModels(List<Long> modelsId) {
        modelRepository.deleteAllById(modelsId);
    }

    @Override
    @Transactional("tenantTransactionManager")
    public List<ModelDTO> getModels(){
        return modelRepository.findAll()
                .stream()
                .map(ModelDTO::new)
                .collect(Collectors.toList());
    }
}
/*
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
 */