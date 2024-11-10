package com.clothing.management.servicesImpl;

import com.clothing.management.dto.ModelDTO;
import com.clothing.management.dto.ModelDeleteDTO;
import com.clothing.management.entities.*;
import com.clothing.management.exceptions.custom.alreadyexists.ModelAlreadyExistsException;
import com.clothing.management.exceptions.custom.notfound.ModelNotFoundException;
import com.clothing.management.mappers.ModelMapper;
import com.clothing.management.repository.*;
import com.clothing.management.services.ModelService;
import com.clothing.management.utils.EntityBuilderHelper;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Transactional("tenantTransactionManager")
@Service
public class ModelServiceImpl implements ModelService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ModelServiceImpl.class);

    private final IModelRepository modelRepository;
    private final IProductRepository productRepository;
    private final EntityBuilderHelper entityBuilderHelper;
    private final ModelMapper modelMapper;
    private final IProductsPacketRepository productsPacketRepository;
    private final IOfferRepository offerRepository;
    private final IOfferModelRepository offerModelRepository;

    public ModelServiceImpl(IModelRepository modelRepository, IProductRepository productRepository, EntityBuilderHelper entityBuilderHelper, ModelMapper modelMapper,
                            IProductsPacketRepository productsPacketRepository, IOfferRepository offerRepository, IOfferModelRepository offerModelRepository) {
        this.modelRepository = modelRepository;
        this.productRepository = productRepository;
        this.entityBuilderHelper = entityBuilderHelper;
        this.modelMapper = modelMapper;
        this.productsPacketRepository = productsPacketRepository;
        this.offerRepository = offerRepository;
        this.offerModelRepository = offerModelRepository;
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
    public ModelDTO saveModel(ModelDTO modelDTO) {
        if (modelDTO.getId() == null) {
            modelRepository.findByNameIsIgnoreCase(modelDTO.getName())
                    .ifPresent(existingModel -> {
                        LOGGER.error("Model already exists with ID: {} and name: {}", existingModel.getId(), existingModel.getName());
                        throw new ModelAlreadyExistsException(existingModel.getId(), existingModel.getName());
                    });
        }
        Model model = modelRepository.save(modelMapper.toEntity(modelDTO));
        LOGGER.info("Model saved with ID: {}", model.getId());

        // Generate products
        LOGGER.info("Generating products for model ID: {}", model.getId());
        generateModelProducts(model);

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
    public void deleteModelById(Long idModel, boolean isSoftDelete) {
        Set<OfferModel> offerModels = offerModelRepository.findByModelId(idModel);
        if (isSoftDelete) {
            modelRepository.softDeleteModel(idModel);
            offerRepository.softDeletedByIds(offerModels.stream().map(offerModel -> offerModel.getOffer().getId()).toList());
        } else {
            modelRepository.deleteById(idModel);
            offerRepository.deleteAllById(offerModels.stream().map(offerModel -> offerModel.getOffer().getId()).toList());
            LOGGER.info("Model with ID: {} deleted.", idModel);
        }
    }

    @Override
    public ModelDeleteDTO checkModelUsage(Long idModel) {
        long usedOffersCount = productsPacketRepository.countProductsPacketByModelId(idModel);
        Set<OfferModel> offerModels = offerModelRepository.findByModelId(idModel);

        return ModelDeleteDTO.builder()
                .usedOffersCount(usedOffersCount)
                .usedOffersNames(offerModels.stream().map(offerModel -> offerModel.getOffer().getName()).toList())
                .build();
    }

    @Override
    public void rollBackModel(Long id) {
        modelRepository.rollBackModel(id);
        LOGGER.info("Models with ID: {} rolled back.", id);
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
