package com.clothing.management.mappers;

import com.clothing.management.dto.ProductDTO;
import com.clothing.management.entities.Color;
import com.clothing.management.entities.Product;
import com.clothing.management.entities.Size;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    ProductMapper INSTANCE = Mappers.getMapper(ProductMapper.class);

    // Method that includes ModelDTO in the mapping
    @Mappings({
            @Mapping(target = "qte", source = "quantity"),  // Map 'quantity' to 'qte'
            @Mapping(target = "modelId", source = "model.id"),
            @Mapping(target = "model.colors", source = "model.colors", qualifiedByName = "mapColorsToIds"),
            @Mapping(target = "model.sizes", source = "model.sizes", qualifiedByName = "mapSizesToIds")
    })
    ProductDTO toDto(Product product);

    // Method that excludes ModelDTO in the mapping
    @Mappings({
            @Mapping(target = "qte", source = "quantity"),
            @Mapping(target = "modelId", source = "model.id"),
            @Mapping(target = "model", ignore = true)       // Exclude 'model' from the mapping
    })
    ProductDTO toDtoWithoutModel(Product product);

    @Named("mapColorsToIds")
    default List<Long> mapColorsToIds(List<Color> colors) {
        return colors.stream()
                .map(Color::getId)
                .collect(Collectors.toList());
    }

    @Named("mapSizesToIds")
    default List<Long> mapSizesToIds(List<Size> sizes) {
        return sizes.stream()
                .map(Size::getId)
                .collect(Collectors.toList());
    }

}