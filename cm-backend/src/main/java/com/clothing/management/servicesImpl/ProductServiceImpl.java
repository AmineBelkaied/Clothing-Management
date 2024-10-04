package com.clothing.management.servicesImpl;

import com.clothing.management.auth.util.SessionUtils;
import com.clothing.management.dto.*;
import com.clothing.management.dto.DayCount.SoldProductsDayCountDTO;
import com.clothing.management.entities.*;
import com.clothing.management.exceptions.custom.notfound.ProductNotFoundException;
import com.clothing.management.mappers.ProductMapper;
import com.clothing.management.mappers.SoldProductsDayCountMapper;
import com.clothing.management.repository.*;
import com.clothing.management.services.ProductService;
import com.clothing.management.utils.EntityBuilderHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;

@Service
@Transactional("tenantTransactionManager")
public class ProductServiceImpl implements ProductService {


    private static final Logger LOGGER = LoggerFactory.getLogger(ProductServiceImpl.class);

    private final IProductRepository productRepository;
    private final IModelRepository modelRepository;
    private final IProductHistoryRepository productHistoryRepository;
    private final IProductsPacketRepository productsPacketRepository;
    private final SessionUtils sessionUtils;
    private final EntityBuilderHelper entityBuilderHelper;
    private final ProductMapper productMapper;
    private final SoldProductsDayCountMapper soldProductsDayCountMapper;

    public ProductServiceImpl(IProductRepository productRepository, IModelRepository modelRepository, IProductHistoryRepository productHistoryRepository, IProductsPacketRepository productsPacketRepository, SessionUtils sessionUtils, EntityBuilderHelper entityBuilderHelper, ProductMapper productMapper, SoldProductsDayCountMapper soldProductsDayCountMapper) {
        this.productRepository = productRepository;
        this.modelRepository = modelRepository;
        this.productHistoryRepository = productHistoryRepository;
        this.productsPacketRepository = productsPacketRepository;
        this.sessionUtils = sessionUtils;
        this.entityBuilderHelper = entityBuilderHelper;
        this.productMapper = productMapper;
        this.soldProductsDayCountMapper = soldProductsDayCountMapper;
    }

    @Override
    public List<ProductResponse> findAllProducts() {
        LOGGER.info("Fetching all products");
        List<ProductResponse> products = productRepository.findAll().stream()
                .map(ProductResponse::new)
                .collect(Collectors.toList());
        LOGGER.info("Fetched {} products", products.size());
        return products;
    }

    @Override
    public List<ProductResponse> fingProductsByModelIds(ModelIdsRequest request) {
        LOGGER.info("Fetching products by model IDs: {}", request.getModelIds());
        List<ProductResponse> products = productRepository.getProductsByModelIds(request.getModelIds()).stream()
                .map(ProductResponse::new)
                .collect(Collectors.toList());
        LOGGER.info("Fetched {} products for model IDs", products.size());
        return products;
    }

    @Override
    public int fingNullProductsByModelId(Long modelId) {
        LOGGER.info("Fetching null products by model ID: {}", modelId);
        int count = productRepository.findNullProductsByModelId(modelId);
        LOGGER.info("Found {} null products for model ID: {}", count, modelId);
        return count;
    }

    @Override
    public Optional<Product> findProductById(Long idProduct) {
        LOGGER.info("Fetching product with ID: {}", idProduct);
        Optional<Product> product = productRepository.findById(idProduct);
        if (product.isPresent()) {
            LOGGER.info("Product found with ID: {}", idProduct);
        } else {
            LOGGER.warn("Product not found with ID: {}", idProduct);
        }
        return product;
    }

    @Override
    public Product addProduct(Product product) {
        LOGGER.info("Adding product: {}", product);
        Product savedProduct = productRepository.save(product);
        LOGGER.info("Product added with ID: {}", savedProduct.getId());
        return savedProduct;
    }

    @Override
    public Product updateProduct(Product product) {
        LOGGER.info("Updating product: {}", product);
        Product updatedProduct = productRepository.save(product);
        LOGGER.info("Product updated with ID: {}", updatedProduct.getId());
        return updatedProduct;
    }

    @Override
    public void deleteProduct(Product product) {
        LOGGER.info("Deleting product: {}", product);
        productRepository.delete(product);
        LOGGER.info("Product deleted with ID: {}", product.getId());
    }

    @Override
    public void deleteSelectedProducts(List<Long> productsId) {
        LOGGER.info("Deleting products with IDs: {}", productsId);
        productRepository.deleteAllById(productsId);
        LOGGER.info("Deleted products with IDs: {}", productsId);
    }

    @Transactional("tenantTransactionManager")
    public StockDTO getStock(Long modelId, String beginDate, String endDate) {
        LOGGER.info("Fetching stock for model ID: {}, between dates: {} and {}", modelId, beginDate, endDate);
        StockDTO stockDTO = new StockDTO();
        List<List<SoldProductsDayCountDTO>> productsByColors = new ArrayList<>();

        modelRepository.findById(modelId).ifPresent((Model model) -> {
            // 1. Sort Sizes for the Model
            List<Product> listProducts = model.getProducts();
            List<Size> orderedSizes = sortSizes(
                    model.getSizes().stream()
                            .filter(Objects::nonNull)
                            .collect(Collectors.toSet())
            );

            Map<Color, List<Product>> groupedProductsByColor = listProducts.stream()
                    .filter(product -> Objects.nonNull(product.getColor()) && Objects.nonNull(product.getSize()))
                    .collect(groupingBy(Product::getColor));

            // 2. Fetch and Group Products by Color
            List<SoldProductsDayCountDTO> productsDayCountDTO = productsPacketRepository.soldProductsCountByDate(modelId, beginDate, endDate);

            // 3. Process Each Group of Products by Color
            groupedProductsByColor.forEach((color, products) -> {
                LOGGER.info("Processing products for color: {}", color);
                List<SoldProductsDayCountDTO> productsDayCountDTOByColor = productsDayCountDTO.stream()
                        .filter(productDayCountDTO -> productDayCountDTO.getColor().equals(color))
                        .collect(Collectors.toList());

                List<SoldProductsDayCountDTO> productDTOList = sortSoldProductsDayCountDTOBySize(productsDayCountDTOByColor, products, orderedSizes);
                productsByColors.add(productDTOList);
            });

            model.setColors(model.getColors().stream()
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList()));

            // 4. Set the Data in stockDTO
            stockDTO.setModel(model);
            stockDTO.setProductsByColor(productsByColors);
            stockDTO.setSizes(orderedSizes);

            LOGGER.info("Stock fetched successfully for model ID: {}", modelId);
        });
        return stockDTO;
    }

    private List<Size> sortSizes(Set<Size> sizes) {
        LOGGER.debug("Sorting sizes: {}", sizes);
        Comparator<Size> sizeComparator = (size1, size2) -> {
            // Define the order of sizes
            String[] order = {"14", "16", "XS", "S", "M", "L", "XL", "2XL", "3XL", "4XL"};
            int index1 = getIndex(size1.getReference(), order);
            int index2 = getIndex(size2.getReference(), order);
            return Integer.compare(index1, index2);
        };
        return sizes.stream()
                .sorted(sizeComparator)
                .collect(Collectors.toList());
    }

    private List<SoldProductsDayCountDTO> sortSoldProductsDayCountDTOBySize(List<SoldProductsDayCountDTO> productsSold, List<Product> products, List<Size> orderedSizes) {
        LOGGER.debug("Sorting sold products by size: {}", productsSold);
        // Create a map to quickly find products by size reference
        Map<String, SoldProductsDayCountDTO> soldProductMap = new HashMap<>();
        for (SoldProductsDayCountDTO product : productsSold) {
            soldProductMap.put(product.getSize().getReference(), product);
        }
        // Create the sorted list based on orderedSizes
        List<SoldProductsDayCountDTO> sortedProducts = new ArrayList<>();
        for (Size size : orderedSizes) {
            String sizeRef = size.getReference();
            SoldProductsDayCountDTO productsDayCountDTO = soldProductMap.get(sizeRef);
            if (productsDayCountDTO != null) {
                sortedProducts.add(productsDayCountDTO);
            } else {
                Product productBySize = products.stream()
                        .filter(product -> size.equals(product.getSize()))
                        .findFirst()
                        .orElse(null);
                if (productBySize != null) {
                    SoldProductsDayCountDTO defaultProduct = soldProductsDayCountMapper.produtToSoldProductsDayCountDTO(productBySize);
                    sortedProducts.add(defaultProduct);
                }
            }
        }
        return sortedProducts;
    }

    private int getIndex(String reference, String[] order) {
        // Loop through the order array to find the index of the reference
        for (int i = 0; i < order.length; i++) {
            if (order[i].equals(reference)) {
                return i;
            }
        }
        return order.length; // Effectively placing it after all defined sizes
    }

    @Transactional("tenantTransactionManager")
    public Page<ProductHistoryDTO> addStock(StockUpdateDTO updateIdStockList) {
        LOGGER.info("Adding stock. Model ID: {}, Products IDs: {}, Quantity: {}, Comment: {}",
                updateIdStockList.getModelId(),
                updateIdStockList.getProductsId(),
                updateIdStockList.getQte(),
                updateIdStockList.getComment());

        User currentUser = sessionUtils.getCurrentUser();
        updateIdStockList.getProductsId().forEach(productId -> {
            LOGGER.debug("Processing stock update for product ID: {}", productId);
            Product product = findProductById(productId)
                    .orElseThrow(() -> new ProductNotFoundException(productId));
            product.setQuantity(product.getQuantity() + updateIdStockList.getQte());
            updateProduct(product);

            ProductHistory productHistory = entityBuilderHelper.createProductHistoryBuilder(product, updateIdStockList.getQte(), new Date(), product.getModel(), currentUser, updateIdStockList.getComment()).build();
            productHistoryRepository.save(productHistory);

            LOGGER.info("Stock updated and history recorded for product ID: {}", productId);
        });

        Pageable paging = PageRequest.of(0, 10, Sort.by("lastModificationDate").descending());
        Page<ProductHistoryDTO> result = productHistoryRepository.findAll(updateIdStockList.getModelId(), paging);
        LOGGER.info("Fetched updated product history. Result size: {}", result.getSize());
        return result;
    }

    @Override
    public List<ModelStockHistory> countStock() {
        LOGGER.info("Counting stock for all products");
        List<ModelStockHistory> stockHistory = productRepository.countStock();
        LOGGER.info("Counted stock history entries: {}", stockHistory.size());
        return stockHistory;
    }
}
