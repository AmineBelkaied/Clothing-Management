package com.clothing.management.servicesImpl;

import com.clothing.management.dto.ModelDTO;
import com.clothing.management.entities.Color;
import com.clothing.management.entities.Model;
import com.clothing.management.entities.Product;
import com.clothing.management.entities.Size;
import com.clothing.management.exceptions.custom.alreadyexists.ModelAlreadyExistsException;
import com.clothing.management.mappers.ModelMapper;
import com.clothing.management.repository.IColorRepository;
import com.clothing.management.repository.IModelRepository;
import com.clothing.management.repository.IProductRepository;
import com.clothing.management.repository.ISizeRepository;
import com.clothing.management.services.ModelService;
import com.clothing.management.utils.EntityBuilderHelper;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class ModelServiceImpl implements ModelService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ModelServiceImpl.class);

    private final IModelRepository modelRepository;
    private final IProductRepository productRepository;
    private final EntityBuilderHelper entityBuilderHelper;
    private final ModelMapper modelMapper;
    private final IColorRepository colorRepository;
    private final ISizeRepository sizeRepository;

    public ModelServiceImpl(IModelRepository modelRepository, IProductRepository productRepository, EntityBuilderHelper entityBuilderHelper, ModelMapper modelMapper,
                            IColorRepository colorRepository, ISizeRepository sizeRepository) {
        this.modelRepository = modelRepository;
        this.productRepository = productRepository;
        this.entityBuilderHelper = entityBuilderHelper;
        this.modelMapper = modelMapper;
        this.colorRepository = colorRepository;
        this.sizeRepository = sizeRepository;

    }

    @Override
    public List<Model> findAllModels() {
        List<Model> models = modelRepository.findAll();
        LOGGER.info("Found {} models.", models.size());
        return models;
    }

    @Override
    public Optional<Model> findModelById(Long idModel) {
        Optional<Model> model = modelRepository.findById(idModel);
        if (model.isPresent()) {
            LOGGER.info("Model found: {}", model.get());
        } else {
            LOGGER.warn("Model with ID: {} not found.", idModel);
        }
        return model;
    }

    @Override
    public ModelDTO saveModel(Model model) {
        if (model.getId() == null) {
            modelRepository.findByNameIsIgnoreCase(model.getName())
                    .ifPresent(existingModel -> {
                        LOGGER.error("Model already exists with ID: {} and name: {}", existingModel.getId(), existingModel.getName());
                        throw new ModelAlreadyExistsException(existingModel.getId(), existingModel.getName());
                    });
        }
        List<Color> colors = new ArrayList<>();
        model.getColors().forEach(color -> {
            Color modelColor = colorRepository.findById(color.getId()).orElse(null);
            colors.add(modelColor);
        });
        model.setColors(colors);
        List<Size> sizes = new ArrayList<>();
        model.getSizes().forEach(size -> {
            Size modelSize = sizeRepository.findById(size.getId()).orElse(null);
            sizes.add(modelSize);
        });
        model.setSizes(sizes);
        model = modelRepository.save(model);
        LOGGER.info("Model saved with ID: {}", model.getId());

        // Generate products
        LOGGER.info("Generating products for model ID: {}", model.getId());
        model = generateModelProducts(model);

        return modelMapper.toDto(model);
    }

    public Model generateModelProducts(Model model) {

        List<Color> colors = model.getColors();
        List<Size> sizes = model.getSizes();
        try {
            // Generate products if there are colors or sizes
            if (!colors.isEmpty() || !sizes.isEmpty()) {
                // Loop through all combinations of color and size
                for (Color color : colors) {
                    for (Size size : sizes) {
                        createProductIfNotExists(model, color, size);
                    }
                }

                // Handle case where color is null but size is present
                for (Size size : sizes) {
                    createProductIfNotExists(model, null, size);
                }

                // Handle case where size is null but color is present
                for (Color color : colors) {
                    createProductIfNotExists(model, color, null);
                }

                // Handle case where both color and size are null
                createProductIfNotExists(model, null, null);
            }
        } catch (EntityNotFoundException e) {
            LOGGER.error("Error generating products: {}", e.getMessage());
        }

        return model;
    }

    private void createProductIfNotExists(Model model, Color color, Size size) {
        // Check if the product already exists
        Product existingProduct = productRepository.findByModelAndColorAndSize(
                model.getId(),
                color != null ? color.getId() : null,
                size != null ? size.getId() : null
        );

        if (existingProduct == null) {
            Product product = entityBuilderHelper.createProductBuilder(
                    size,
                    color,
                    0,
                    new Date(),
                    model).build();

            productRepository.save(product);
            LOGGER.info("Product created: {}", product.getColor()+" "+product.getSize()+" " +product);
        }
        else LOGGER.info("Product exist: {}",existingProduct);
    }

    @Override
    public void deleteModelById(Long idModel) {
        modelRepository.deleteById(idModel);
        LOGGER.info("Model with ID: {} deleted.", idModel);
    }

    @Override
    public void deleteSelectedModels(List<Long> modelsId) {
        modelRepository.deleteAllById(modelsId);
        LOGGER.info("Models with IDs: {} deleted.", modelsId);
    }

    @Override
    @Transactional("tenantTransactionManager")
    public List<ModelDTO> getModels() {
        List<ModelDTO> modelDTOs = modelRepository.findAll()
                .stream()
                .map(modelMapper::toDto)
                .collect(Collectors.toList());
        LOGGER.info("Found {} model DTOs.", modelDTOs.size());
        return modelDTOs;
    }
}
