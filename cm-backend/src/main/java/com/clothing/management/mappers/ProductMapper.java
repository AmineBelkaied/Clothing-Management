package com.clothing.management.mappers;

import com.clothing.management.dto.ProductDTO;
import com.clothing.management.entities.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    ProductMapper INSTANCE = Mappers.getMapper(ProductMapper.class);

    // Method that includes ModelDTO in the mapping
    @Mappings({
            @Mapping(target = "qte", source = "quantity"),  // Map 'quantity' to 'qte'
            @Mapping(target = "modelId", source = "model.id"),
            @Mapping(target = "model", source = "model")    // Include 'model' in the mapping
    })
    ProductDTO toDto(Product product);

    // Method that excludes ModelDTO in the mapping
    @Mappings({
            @Mapping(target = "qte", source = "quantity"),
            @Mapping(target = "modelId", source = "model.id"),
            @Mapping(target = "model", ignore = true)       // Exclude 'model' from the mapping
    })
    ProductDTO toDtoWithoutModel(Product product);

}