package com.clothing.management.servicesImpl;

import com.clothing.management.auth.util.SessionUtils;
import com.clothing.management.dto.DayCount.SoldProductsDayCountDTO;
import com.clothing.management.dto.ProductDTO;
import com.clothing.management.dto.ProductHistoryDTO;
import com.clothing.management.dto.StockDTO;
import com.clothing.management.dto.StockUpdateDto;
import com.clothing.management.entities.*;
import com.clothing.management.exceptions.custom.notfound.ProductNotFoundException;
import com.clothing.management.repository.*;
import com.clothing.management.services.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
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
    SessionUtils sessionUtils;

    public ProductServiceImpl(IProductRepository productRepository, IModelRepository modelRepository, IProductHistoryRepository productHistoryRepository, IProductsPacketRepository productsPacketRepository) {
        this.productRepository = productRepository;
        this.modelRepository = modelRepository;
        this.productHistoryRepository = productHistoryRepository;
        this.productsPacketRepository = productsPacketRepository;
    }

    @Override
    public List<ProductDTO> findAllProducts() {
        return productRepository.findAll().stream().map(product -> new ProductDTO(product,true)).collect(Collectors.toList());
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

    public StockDTO getStock(Long modelId,String beginDate, String endDate) {// correction ----------------------------
        StockDTO stockDTO = new StockDTO();
        List<List<SoldProductsDayCountDTO>> productsByColors = new ArrayList<>();
        //List<List<ProductDTO>> productsByColors2 = new ArrayList<>();

        modelRepository.findById(modelId).ifPresent((Model model) -> {
            // 1. Sort Sizes for the Model
            List<Size> orderedSizes = sortSizes(
                    model.getSizes().stream()
                            .filter((Size size) -> !size.getReference().equals("?"))
                            .collect(Collectors.toSet())
            );

            // 2. Fetch and Group Products by Color
            Map<Color, List<SoldProductsDayCountDTO>> groupedProductsPacket = productsPacketRepository
                    .soldProductsCountByDate(modelId, beginDate, endDate)
                    .stream()
                    .filter(product -> !product.getColor().getReference().equals("?") && !product.getSize().getReference().equals("?"))
                    .collect(groupingBy(SoldProductsDayCountDTO::getColor));

            // 3. Process Each Group of Products by Color
            groupedProductsPacket.forEach((color, products) -> {
                // 3.1 Sort products by size and add missing sizes
                List<SoldProductsDayCountDTO> productDTOList = sortSoldProductsDayCountDTOBySize2(products, orderedSizes, color);

                // 3.2 Add the sorted list to productsByColors
                productsByColors.add(productDTOList);
            });
            model.setColors(model.getColors().stream()
                    .filter(color -> !color.getName().equals("?")).collect(Collectors.toList()));
            // 4. Set the Data in stockDTO
            stockDTO.setModel(model);
            stockDTO.setProductsByColor(productsByColors);
            stockDTO.setSizes(orderedSizes);
        });

        /*modelRepository.findById(modelId).ifPresent(model -> {
            Map<Color, List<Product>> groupedProducts = model.getProducts()
                    .stream()
                    .filter(product -> !product.getColor().getReference().equals("?") && !product.getSize().getReference().equals("?"))
                    .collect(groupingBy(Product::getColor));

            groupedProducts.forEach((color, products) -> {
                List<ProductDTO> productDTOList2 = new ArrayList<>();
                //objects.add(color);
                productDTOList2.addAll(sortProductBySize(products.stream().map(product -> new ProductDTO(product,false)).collect(Collectors.toList())));
                productsByColors.add(productDTOList2);
            });
            stockDTO.setModel(model);
            stockDTO.setProductsByColor(productsByColors);
            stockDTO.setSizes(sortSizes(model.getSizes().stream().filter(size -> !size.getReference().equals("?")).collect(Collectors.toSet())));
        });*/
        return stockDTO;
    }


    private List<ProductDTO> sortProductBySize(List<ProductDTO> products) {
        Comparator<ProductDTO> sizeComparator = (product1, product2) -> {
            // Define the order of sizes
            String[] order = {"14","16","XS", "S", "M", "L", "XL", "2XL", "3XL", "4XL"};
            int index1 = getIndex(product1.getSize().getReference(), order);
            int index2 = getIndex(product2.getSize().getReference(), order);
            return Integer.compare(index1, index2);
        };
        return products.stream()
                .sorted(sizeComparator)
                .collect(Collectors.toList());
    }

    private List<SoldProductsDayCountDTO> sortSoldProductsDayCountDTOBySize(List<SoldProductsDayCountDTO> products, List<Size> orderedSizes) {
        Comparator<SoldProductsDayCountDTO> sizeComparator = (product1, product2) -> {
            // Define the order of sizes
            String[] order = {"14","16","XS", "S", "M", "L", "XL", "2XL", "3XL", "4XL"};
            int index1 = getIndex(product1.getSize().getReference(), order);
            int index2 = getIndex(product2.getSize().getReference(), order);
            return Integer.compare(index1, index2);
        };
        return products.stream()
                .sorted(sizeComparator)
                .collect(Collectors.toList());
    }

    private List<Size> sortSizes(Set<Size> sizes) {
        Comparator<Size> sizeComparator = (size1, size2) -> {
            // Define the order of sizes
            String[] order = {"14","16","XS", "S", "M", "L", "XL", "2XL", "3XL", "4XL", "?"};
            int index1 = getIndex(size1.getReference(), order);
            int index2 = getIndex(size2.getReference(), order);
            return Integer.compare(index1, index2);
        };
        return sizes.stream()
                .sorted(sizeComparator)
                .collect(Collectors.toList());
    }
    private List<SoldProductsDayCountDTO> sortSoldProductsDayCountDTOBySize2(List<SoldProductsDayCountDTO> products, List<Size> orderedSizes, Color color) {
        // Create a map to quickly find products by size reference
        Map<String, SoldProductsDayCountDTO> productMap = new HashMap<>();
        for (SoldProductsDayCountDTO product : products) {
            productMap.put(product.getSize().getReference(), product);
        }

        // Create the sorted list based on orderedSizes
        List<SoldProductsDayCountDTO> sortedProducts = new ArrayList<>();

        for (Size size : orderedSizes) {
            String sizeRef = size.getReference();
            SoldProductsDayCountDTO productsDayCountDTO = productMap.get(sizeRef);
            if(productsDayCountDTO != null){
                sortedProducts.add(productsDayCountDTO);
            }
            else {
                SoldProductsDayCountDTO defaultProduct = new SoldProductsDayCountDTO();
                defaultProduct.setSize(size);
                defaultProduct.setColor(color);
                sortedProducts.add(defaultProduct);
            }
        }
        return sortedProducts;
    }

    // Helper method to get the index of a size in the order array
    private static int getIndex0(String size, String[] order) {
        for (int i = 0; i < order.length; i++) {
            if (order[i].equals(size)) {
                return i;
            }
        }
        return -1; // Default index if not found (shouldn't happen in this example)
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


    public Page<ProductHistoryDTO> addStock(StockUpdateDto updateIdStockList) {
            // Iterate through the list of product IDs to update each product's stock
            updateIdStockList.getProductsId().forEach(productId -> {
                // Find the product by its ID
                Product product = findProductById(productId)
                        .orElseThrow(() -> new ProductNotFoundException(productId));

            product.setQuantity(product.getQuantity() + updateIdStockList.getQte());
            updateProduct(product);

            ProductHistory productHistory =
                    new ProductHistory(
                            product,
                            updateIdStockList.getQte(),
                            new Date(), product.getModel(),
                            sessionUtils.getCurrentUser(),
                            updateIdStockList.getComment()
                    );
            productHistoryRepository.save(productHistory);
        });

        Pageable paging = PageRequest.of(0, 10, Sort.by("lastModificationDate").descending());
        return productHistoryRepository.findAll(updateIdStockList.getModelId(), paging);
    }

    @Override
    public List<ModelStockHistory> countStock() {
        return productRepository.countStock();
    }

    @Override
    public Product findByModelAndColorAndSize(Long modelId, Long colorId, Long sizeId) {
        return productRepository.findByModelAndColorAndSize(modelId,colorId,sizeId);
    }
}
  /*
    private Product mapToProduct(Product product) {
        Product newProduct = new Product();
        newProduct.setId(product.getId());
        newProduct.setColor(product.getColor());
        newProduct.setSize(product.getSize());
        //newProduct.setReference(product.getReference());
        newProduct.setModel(mapToModel(product.getModel()));
        newProduct.setQuantity(product.getQuantity());
        newProduct.setDate(product.getDate());
        return newProduct;
    }

    private Model mapToModel(Model model) {
        Model newModel = new Model();
        newModel.setId(model.getId());
        newModel.setColors(model.getColors());
        newModel.setSizes(model.getSizes());
        newModel.setDescription(model.getDescription());
        newModel.setName(model.getName());
        newModel.setPurchasePrice(model.getPurchasePrice());
        newModel.setEarningCoefficient(model.getEarningCoefficient());
        return newModel;
    }
    @Override
    public Product findProductByReference(String reference) {
        return productRepository.findByReference(reference);
    }*/
