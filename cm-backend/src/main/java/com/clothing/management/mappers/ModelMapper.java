package com.clothing.management.mappers;

import com.clothing.management.dto.ModelDTO;
import com.clothing.management.entities.*;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface ModelMapper {

    ModelMapper INSTANCE = Mappers.getMapper(ModelMapper.class);

    @Mappings({
            @Mapping(target = "colors", source = "colors", qualifiedByName = "mapColorsToIds"),
            @Mapping(target = "sizes", source = "sizes", qualifiedByName = "mapSizesToIds"),
            @Mapping(target = "defaultId", source = "products", qualifiedByName = "mapToDefaultId")
    })
    ModelDTO toDto(Model model);

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

    @Named("mapToDefaultId")
    default Long mapToDefaultId(List<Product> products) {
        return products
                .stream()
                .filter(product -> product.getColor() == null && product.getSize() == null)
                .findFirst()
                .map(Product::getId)
                .orElse(null);
    }
}