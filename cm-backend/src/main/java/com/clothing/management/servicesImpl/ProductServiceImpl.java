package com.clothing.management.servicesImpl;

import com.clothing.management.dto.ProductHistoryDTO;
import com.clothing.management.dto.StockDTO;
import com.clothing.management.dto.StockUpdateDto;
import com.clothing.management.entities.*;
import com.clothing.management.repository.IModelRepository;
import com.clothing.management.repository.IOfferRepository;
import com.clothing.management.repository.IProductHistoryRepository;
import com.clothing.management.repository.IProductRepository;
import com.clothing.management.services.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;

@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    IProductRepository productRepository;

    @Autowired
    IModelRepository modelRepository;

    @Autowired
    IOfferRepository offerRepository;
    @Autowired
    IProductHistoryRepository productHistoryRepository;
    private Long productModel;

    @Override
    public List<Product> findAllProducts() {
        return productRepository.findAll().stream().map(product -> mapToProduct(product)).collect(Collectors.toList());
    }
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
        newModel.setReference(model.getReference());
        return newModel;
    }
    @Override
    public Optional<Product> findProductById(Long idProduct) {
        return productRepository.findById(idProduct);
    }

   /* @Override
    public Product findProductByReference(String reference) {
        return productRepository.findByReference(reference);
    }*/

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

    public StockDTO getStock(Long modelId) {
        StockDTO stockDTO = new StockDTO();
        List<List<Object>> productsByColors = new ArrayList<>();
        modelRepository.findById(modelId).ifPresent(model -> {
            Map<Color, List<Product>> groupedProducts = model.getProducts()
                    .stream()
                    .filter(product -> !product.getColor().getReference().equals("?") && !product.getSize().getReference().equals("?"))
                    .collect(groupingBy(Product::getColor));

            groupedProducts.forEach((color, products) -> {
                List<Object> objects = new ArrayList<>();
                //objects.add(color);
                objects.addAll(sortProductBySize((products.stream().map(product -> mapToProduct(product)).collect(Collectors.toList()))));
                productsByColors.add(objects);
            });
            stockDTO.setProductsByColor(productsByColors);
            stockDTO.setSizes(sortSizes(model.getSizes().stream().filter(size -> !size.getReference().equals("?")).collect(Collectors.toSet())));
        });
        return stockDTO;
    }


    private List<Product> sortProductBySize(List<Product> products) {
        Comparator<Product> sizeComparator = (product1, product2) -> {
            // Define the order of sizes
            String[] order = {"XS", "S", "M", "L", "XL", "2XL", "3XL", "4XL"};
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
            String[] order = {"XS", "S", "M", "L", "XL", "2XL", "3XL", "4XL", "?"};
            int index1 = getIndex(size1.getReference(), order);
            int index2 = getIndex(size2.getReference(), order);
            return Integer.compare(index1, index2);
        };
        return sizes.stream()
                .sorted(sizeComparator)
                .collect(Collectors.toList());
    }

    // Helper method to get the index of a size in the order array
    private static int getIndex(String size, String[] order) {
        for (int i = 0; i < order.length; i++) {
            if (order[i].equals(size)) {
                return i;
            }
        }
        return -1; // Default index if not found (shouldn't happen in this example)
    }

    public Page<ProductHistoryDTO> addStock(StockUpdateDto updateIdSockList) {
        updateIdSockList.getProductsId().forEach(productId -> {
            Optional<Product> product = findProductById(productId);
            if(product.isPresent()) {
                productModel = updateIdSockList.getModelId();//product.get().getModel().getId();
                product.get().setQuantity(product.get().getQuantity()+updateIdSockList.getQte());
                updateProduct(product.get());
                ProductHistory productHistory = new ProductHistory(product.get(),updateIdSockList.getQte(), new Date(), product.get().getModel(),updateIdSockList.getUserName());
                productHistoryRepository.save(productHistory);
            }
        });

        Pageable paging = PageRequest.of(0, 10, Sort.by("lastModificationDate").descending());
        return productHistoryRepository.findAll(updateIdSockList.getModelId(), paging);
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
