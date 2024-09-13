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
    public List<ProductDTO> findAllProducts() {
        return productRepository.findAll().stream().map(productMapper::toDto).collect(Collectors.toList());
    }
    
    public List<ProductResponse> findAllProducts() {
        return productRepository.findAll().stream().map(product -> new ProductResponse(product)).collect(Collectors.toList());
    }

    @Override
    public List<ProductResponse> fingProductsByModelIds(ModelIdsRequest request) {
        List<Long> modelIds = request.getModelIds();
        return productRepository.getProductsByModelIds(modelIds).stream().map(product -> new ProductResponse(product)).collect(Collectors.toList());
    }

    @Override
    public int fingNullProductsByModelId(Long modelId) {
        return productRepository.findNullProductsByModelId(modelId);
    }

    @Override
    public Optional<Product> findProductById(Long idProduct) {
        return productRepository.findById(idProduct);
    }

    @Override
    public Product addProduct(Product product) {
        return productRepository.save(product);
    }

    @Override
    public Product updateProduct(Product product) {
        return productRepository.save(product);
    }

    @Override
    public void deleteProduct(Product product) {
        productRepository.delete(product);
    }

    @Override
    public void deleteSelectedProducts(List<Long> productsId) {
        productRepository.deleteAllById(productsId);
    }

    @Transactional("tenantTransactionManager")
    public StockDTO getStock(Long modelId,String beginDate, String endDate) {// correction ----------------------------
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
                // 3.1 Sort products by size and add missing sizes
                List<SoldProductsDayCountDTO> productsDayCountDTOByColor = productsDayCountDTO.stream().filter(productDayCountDTO -> productDayCountDTO.getColor().equals(color)).collect(Collectors.toList());
                List<SoldProductsDayCountDTO> productDTOList = sortSoldProductsDayCountDTOBySize(productsDayCountDTOByColor,products, orderedSizes);

                // 3.2 Add the sorted list to productsByColors
                productsByColors.add(productDTOList);
            });
            model.setColors(model.getColors().stream()
                    .filter(color -> !color.equals(null)).collect(Collectors.toList()));
            // 4. Set the Data in stockDTO
            stockDTO.setModel(model);
            stockDTO.setProductsByColor(productsByColors);
            stockDTO.setSizes(orderedSizes);
        });
        return stockDTO;
    }

    private List<Size> sortSizes(Set<Size> sizes) {
        Comparator<Size> sizeComparator = (size1, size2) -> {
            // Define the order of sizes
            String[] order = {"14","16","XS", "S", "M", "L", "XL", "2XL", "3XL", "4XL"};
            int index1 = getIndex(size1.getReference(), order);
            int index2 = getIndex(size2.getReference(), order);
            return Integer.compare(index1, index2);
        };
        return sizes.stream()
                .sorted(sizeComparator)
                .collect(Collectors.toList());
    }
    private List<SoldProductsDayCountDTO> sortSoldProductsDayCountDTOBySize(List<SoldProductsDayCountDTO> productsSold,List<Product> products, List<Size> orderedSizes) {
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
            if(productsDayCountDTO != null){
                sortedProducts.add(productsDayCountDTO);
            }
            else {
                Product productBySize = products.stream()
                        .filter(product -> size.equals(product.getSize()))
                        .findFirst()
                        .orElse(null);
                if(productBySize != null)
                {
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
        // If reference is not found, return a high value to sort it at the end
        return order.length; // Effectively placing it after all defined sizes
    }



    @Transactional("tenantTransactionManager")
    public Page<ProductHistoryDTO> addStock(StockUpdateDTO updateIdStockList) {
            // Iterate through the list of product IDs to update each product's stock
            User currentUser = sessionUtils.getCurrentUser();
            updateIdStockList.getProductsId().forEach(productId -> {
                // Find the product by its ID
                Product product = findProductById(productId)
                        .orElseThrow(() -> new ProductNotFoundException(productId));

            product.setQuantity(product.getQuantity() + updateIdStockList.getQte());
            updateProduct(product);

                ProductHistory productHistory =
                        entityBuilderHelper.createProductHistoryBuilder(
                                        product,
                                        updateIdStockList.getQte(),
                                        new Date(),
                                        product.getModel(),
                                        currentUser,
                                        updateIdStockList.getComment()
                                ).build();
            productHistoryRepository.save(productHistory);
        });

        Pageable paging = PageRequest.of(0, 10, Sort.by("lastModificationDate").descending());
        return productHistoryRepository.findAll(updateIdStockList.getModelId(), paging);
    }
    @Override
    public List<ModelStockHistory> countStock() {
        return productRepository.countStock();
    }

}