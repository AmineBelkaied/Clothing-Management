package com.clothing.management.servicesImpl;

import com.clothing.management.auth.util.SessionUtils;
import com.clothing.management.dto.*;
import com.clothing.management.dto.DayCount.ProductsQuantityDTO;
import com.clothing.management.dto.DayCount.SoldProductsDayCountDTO;
import com.clothing.management.entities.*;
import com.clothing.management.exceptions.custom.notfound.ProductNotFoundException;
import com.clothing.management.mappers.ModelMapper;
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
    private final IProductRepository productsRepository;
    private final SessionUtils sessionUtils;
    private final EntityBuilderHelper entityBuilderHelper;
    private final SoldProductsDayCountMapper soldProductsDayCountMapper;
    private final ModelMapper modelMapper;


    public ProductServiceImpl(IProductRepository productRepository, IModelRepository modelRepository,
                              IProductHistoryRepository productHistoryRepository,
                              IProductsPacketRepository productsPacketRepository,
                              IProductRepository productsRepository, SessionUtils sessionUtils,
                              EntityBuilderHelper entityBuilderHelper,
                              SoldProductsDayCountMapper soldProductsDayCountMapper, ModelMapper modelMapper) {
        this.productRepository = productRepository;
        this.modelRepository = modelRepository;
        this.productHistoryRepository = productHistoryRepository;
        this.productsPacketRepository = productsPacketRepository;
        this.productsRepository = productsRepository;
        this.sessionUtils = sessionUtils;
        this.entityBuilderHelper = entityBuilderHelper;
        this.soldProductsDayCountMapper = soldProductsDayCountMapper;
        this.modelMapper = modelMapper;
    }

    @Override
    public List<ProductResponse> findAllProducts() {
        List<ProductResponse> products = productRepository.findAll().stream()
                .map(ProductResponse::new)
                .collect(Collectors.toList());
        LOGGER.info("Fetched {} products", products.size());
        return products;
    }

    @Override
    public List<ProductResponse> fingProductsByModelIds(ModelIdsRequest request) {
        List<ProductResponse> products = productRepository.getProductsByModelIds(request.getModelIds()).stream()
                .map(ProductResponse::new)
                .collect(Collectors.toList());
        LOGGER.info("Fetched {} products for model IDs", products.size());
        return products;
    }

    @Override
    public int fingNullProductsByModelId(Long modelId) {
        int count = productRepository.findNullProductsByModelId(modelId);
        LOGGER.info("Found {} null products for model ID: {}", count, modelId);
        return count;
    }

    @Override
    public Optional<Product> findProductById(Long idProduct) {
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
        Product savedProduct = productRepository.save(product);
        LOGGER.info("Product added with ID: {}", savedProduct.getId());
        return savedProduct;
    }

    @Override
    public Product updateProduct(Product product) {
        Product updatedProduct = productRepository.save(product);
        LOGGER.info("Product updated with ID: {}", updatedProduct.getId());
        return updatedProduct;
    }

    @Override
    public void deleteProduct(Product product) {
        productRepository.delete(product);
        LOGGER.info("Product deleted with ID: {}", product.getId());
    }

    @Override
    public void deleteSelectedProducts(List<Long> productsId) {
        productRepository.deleteAllById(productsId);
        LOGGER.info("Deleted products with IDs: {}", productsId);
    }

    @Transactional("tenantTransactionManager")
    public StockDTO getStock(Long modelId, String beginDate, String endDate) {
        StockDTO stockDTO = new StockDTO();
        // HashMap to store products grouped by Color, then Size, and their respective SoldProductsDayCountDTO
        HashMap<Long, HashMap<Long, SoldProductsDayCountDTO>> productsByColor = new HashMap<>();

        modelRepository.findById(modelId).ifPresent((Model model) -> {
            // 1. Sort Sizes for the Model

            List<Product> listProducts = model.getProducts();
            List<Size> sizes = model.getSizes();
            List<Long> orderedSizes = sortSizes(
                    model.getSizes().stream()
                            .filter(size-> Objects.nonNull(size)
                                    && sizes.contains(size))
                            .collect(Collectors.toSet())
            );
            List<Color> colors = model.getColors();
            // Group products by color
            Map<Color, List<Product>> groupedProductsByColor = listProducts.stream()
                    .filter(product ->
                            Objects.nonNull(product.getColor())
                            && Objects.nonNull(product.getSize())
                            && colors.contains(product.getColor()))
                    .collect(groupingBy(Product::getColor));

            // 2. Fetch and Group Products by Color
            List<SoldProductsDayCountDTO> productsDayCountDTO = productsPacketRepository.soldProductsCountByDate(modelId, beginDate, endDate);

            // 3. Process Each Group of Products by Color
            groupedProductsByColor.forEach((color, products) -> {
                    //LOGGER.info("Processing products for color: {}", color.getId());
                    List<SoldProductsDayCountDTO> productsDayCountDTOByColor = productsDayCountDTO.stream()
                            .filter(productDayCountDTO -> productDayCountDTO.getColorId().equals(color.getId()))
                            .collect(Collectors.toList());
                    List<SoldProductsDayCountDTO> sortedProducts = sortSoldProductsDayCountDTOBySize(productsDayCountDTOByColor, products, orderedSizes);
                    HashMap<Long, SoldProductsDayCountDTO> productsBySize = new HashMap<>();
                    for (SoldProductsDayCountDTO soldProduct : sortedProducts) {
                        productsBySize.put(soldProduct.getSizeId(), soldProduct);
                    }
                    productsByColor.put(color.getId(), productsBySize);
            });

            model.setColors(model.getColors().stream()
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList()));

            // 4. Set the Data in stockDTO
            stockDTO.setModel(modelMapper.toDto(model));
            stockDTO.setProductsByColor(productsByColor);
        });

        return stockDTO;
    }


    @Transactional("tenantTransactionManager")
    public List<ProductsQuantityDTO> getStockQuantity(Long modelId) {
            return productsRepository.productsQuantity(modelId)
                    .stream()
                    .map(ProductsQuantityDTO::new)
                    .toList();
    }

    private List<Long> sortSizes(Set<Size> sizes) {
        Comparator<Size> sizeComparator = (size1, size2) -> {
            // Define the order of sizes
            String[] order = {"1", "2", "3", "4", "5", "6", "7", "8", "9", "10",
                    "11", "12", "13", "14", "15", "16", "17", "18",
                    "XXS", "XS", "S", "M", "L", "XL", "2XL", "3XL", "4XL", "5XL", "6XL",
                    "26", "27", "28", "30", "32", "33", "34", "35", "36", "37", "38", "39", "40", "41",
                    "42", "43", "44", "45", "46", "47", "48", "49", "50", "51", "52", "53", "54", "55", "56"
            };
            int index1 = getIndex(size1.getReference(), order);
            int index2 = getIndex(size2.getReference(), order);
            return Integer.compare(index1, index2);
        };

        // Sort the sizes by their reference and map them to their IDs
        return sizes.stream()
                .sorted(sizeComparator)
                .map(Size::getId)  // Map each sorted Size object to its ID
                .collect(Collectors.toList());
    }

    //Correction stock
  private List<SoldProductsDayCountDTO> sortSoldProductsDayCountDTOBySize(List<SoldProductsDayCountDTO> productsSold, List<Product> products, List<Long> orderedSizes) {
        // Create a map to quickly find products by size reference
        Map<Long, SoldProductsDayCountDTO> soldProductMap = new HashMap<>();
        for (SoldProductsDayCountDTO product : productsSold) {
            soldProductMap.put(product.getSizeId(), product);
        }
        // Create the sorted list based on orderedSizes
        List<SoldProductsDayCountDTO> sortedProducts = new ArrayList<>();
        for (long size : orderedSizes) {

            SoldProductsDayCountDTO productsDayCountDTO = soldProductMap.get(size);
            if (productsDayCountDTO != null) {
                sortedProducts.add(productsDayCountDTO);
            } else {
                Product productBySize = products.stream()
                        .filter(product -> size == product.getSize().getId())
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
        List<ModelStockHistory> stockHistory = productRepository.countStock();
        LOGGER.info("Counted stock history entries: {}", stockHistory.size());
        return stockHistory;
    }
}
