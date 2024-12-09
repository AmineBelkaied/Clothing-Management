package com.clothing.management.servicesImpl;

import com.clothing.management.dto.ModelDTO;
import com.clothing.management.dto.ModelDeleteDTO;
import com.clothing.management.entities.*;
import com.clothing.management.enums.ProductType;
import com.clothing.management.exceptions.custom.alreadyexists.ModelAlreadyExistsException;
import com.clothing.management.exceptions.custom.notfound.ModelNotFoundException;
import com.clothing.management.mappers.ModelMapper;
import com.clothing.management.models.AttributeKey;
import com.clothing.management.models.ModelSaveResponse;
import com.clothing.management.repository.*;
import com.clothing.management.services.ModelService;
import com.clothing.management.utils.EntityBuilderHelper;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Transactional("tenantTransactionManager")
@Service
public class ModelServiceImpl implements ModelService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ModelServiceImpl.class);

    private final IModelRepository modelRepository;
    private final IColorRepository colorRepository;
    private final ISizeRepository sizeRepository;
    private final IProductRepository productRepository;
    private final EntityBuilderHelper entityBuilderHelper;
    private final ModelMapper modelMapper;
    private final IProductsPacketRepository productsPacketRepository;
    private final IOfferRepository offerRepository;
    private final IOfferModelRepository offerModelRepository;
    private static List<String> confirmedPacketStatus = List.of(new String[]{"Livrée", "Payée","En cours (1)", "En cours (2)", "En cours (3)", "A verifier"});
    private final static String EMPTY_STRING = "";

    public ModelServiceImpl(IModelRepository modelRepository, IColorRepository colorRepository, ISizeRepository sizeRepository,
                            IProductRepository productRepository,
                            EntityBuilderHelper entityBuilderHelper,
                            ModelMapper modelMapper,
                            IProductsPacketRepository productsPacketRepository,
                            IOfferRepository offerRepository,
                            IOfferModelRepository offerModelRepository) {
        this.modelRepository = modelRepository;
        this.colorRepository = colorRepository;
        this.sizeRepository = sizeRepository;
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
    public ModelSaveResponse saveModel(ModelDTO modelDTO) {
        if (modelDTO.getId() == null) {
            modelRepository.findByNameIsIgnoreCase(modelDTO.getName())
                    .ifPresent(existingModel -> {
                        LOGGER.error("Model already exists with ID: {} and name: {}", existingModel.getId(), existingModel.getName());
                        throw new ModelAlreadyExistsException(existingModel.getId(), existingModel.getName());
                    });
        }

        StringBuilder errors = new StringBuilder();
        boolean success = true;
        if (Objects.nonNull(modelDTO.getId())) {
            Model oldModel = modelRepository.findById(modelDTO.getId())
                    .orElseThrow(() -> {
                        LOGGER.error("Model with ID: {} not found", modelDTO.getId());
                        return new ModelNotFoundException(modelDTO.getId(), "Model not found!");
                    });


            List<Long> oldColorIds = oldModel.getColors().stream().map(Color::getId).toList();
            List<Long> oldSizeIds = oldModel.getSizes().stream().map(Size::getId).toList();

            List<Long> deletedColorIds = getDeletedIds(oldColorIds, modelDTO.getColors());
            List<Long> deletedSizeIds = getDeletedIds(oldSizeIds, modelDTO.getSizes());

            if (!deletedColorIds.isEmpty()) {
                List<Long> verifiedColorsList = verifyDeletedColorsUsage(modelDTO.getId(), deletedColorIds, errors);
                if (!errors.isEmpty()) {
                    modelDTO.getColors().addAll(verifiedColorsList); // Adjust colors to be deleted after verification
                }
            }

            if (!deletedSizeIds.isEmpty()) {
                List<Long> verifiedSizesList = verifyDeletedSizeUsage(modelDTO.getId(), deletedSizeIds, errors);
                if (!errors.isEmpty()) {
                    errors.append("\n");
                    modelDTO.getSizes().addAll(verifiedSizesList); // Adjust sizes to be deleted after verification
                }
            }

            if (!errors.isEmpty()) {
                LOGGER.error("Errors encountered: {}", errors.toString().trim());
                success = false;
            }
        }


        Model updatedModel = modelRepository.save(modelMapper.toEntity(modelDTO));

        generateModelProducts(updatedModel);

        return ModelSaveResponse.builder()
                .modelDTO(modelMapper.toDto(updatedModel))
                .success(success)
                .errors(errors.toString())
                .build();
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

    private List<Long> verifyDeletedSizeUsage(Long modelId, List<Long> deletedSizeIds, StringBuilder errors) {
        List<Product> products = productRepository.findByModelAndSizes(modelId, deletedSizeIds);
        return validateProductsStockAndUsage(products, deletedSizeIds, ProductType.SIZE, errors);
    }

    private List<Long> verifyDeletedColorsUsage(Long modelId, List<Long> deletedColorIds, StringBuilder errors) {
        List<Product> products = productRepository.findByModelAndColors(modelId, deletedColorIds);
        return validateProductsStockAndUsage(products, deletedColorIds, ProductType.COLOR, errors);
    }

    private List<Long> getDeletedIds(List<Long> oldIds, List<Long> newIds) {
        return oldIds.stream()
                .filter(id -> newIds.stream().noneMatch(id::equals))
                .toList();
    }

    private List<Long> filterIds(List<Long> ids, List<Long> idsToRemove) {
        return ids.stream()
                .filter(id -> idsToRemove.stream().noneMatch(id::equals))
                .toList();
    }

    private Map<AttributeKey, List<Product>> groupProductsByAttribute(List<Product> products, Function<Product, AttributeKey> attributeExtractor) {
        return products.stream()
                .collect(Collectors.groupingBy(attributeExtractor));
    }

    public List<Long> validateProductsStockAndUsage(List<Product> products , List<Long> deletedIds, ProductType type, StringBuilder errors) {
        Map<AttributeKey, List<Product>> groupedProducts = switch (type) {
            case COLOR -> groupProductsByAttribute(products, product ->
                    new AttributeKey(product.getColor().getId(), product.getColor().getName()));
            case SIZE -> groupProductsByAttribute(products, product ->
                    new AttributeKey(product.getSize().getId(), product.getSize().getReference()));
        };

        for (Map.Entry<AttributeKey, List<Product>> entry : groupedProducts.entrySet()) {
            AttributeKey attributeKey = entry.getKey();
            List<Product> productList = entry.getValue();
            long countAvailable = productList.stream().filter(product -> product.getQuantity() > 0).count();
            long countUsed = productList.stream().filter(product -> isProductUsed(product.getId())).count();

            if (countAvailable > 0 || countUsed > 0) {
                errors.append(String.format("La %s %s ne peut pas être supprimée car :%n", type.getDescription(), attributeKey.getName()));
            }

            if (countAvailable > 0) {
                errors.append(String.format("- Elle contient %d produits avec un stock supérieur à 0.%n", countAvailable));
            }

            if (countUsed > 0) {
                errors.append(String.format("- Elle a %d produits qui sont déjà commandés.%n", countUsed));
            }

            if (countAvailable <= 0 || countUsed <= 0) {
                deletedIds = deletedIds.stream().filter(id -> !attributeKey.getId().equals(id)).toList();
            }
            errors.append(System.lineSeparator());
        }

        return deletedIds;
    }

    private boolean isProductUsed(Long productId) {
        return productsPacketRepository.countProductsPacketByProductId(productId) > 0;
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
