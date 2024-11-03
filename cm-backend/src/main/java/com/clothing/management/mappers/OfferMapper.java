package com.clothing.management.mappers;

import com.clothing.management.dto.OfferDTO;
import com.clothing.management.dto.OfferModelsDTO;
import com.clothing.management.entities.*;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface OfferMapper {

    OfferMapper INSTANCE = Mappers.getMapper(OfferMapper.class);

    @Mappings({
            @Mapping(target = "fbPages", source = "fbPages", qualifiedByName = "mapFbPagesToIds"),
            @Mapping(target = "offerModels", source = "offerModels", qualifiedByName = "mapToOfferDto"),
            @Mapping(target = "isDeleted", source = "deleted"),
            @Mapping(target = "isEnabled", source = "enabled")
    })
    OfferDTO toDto(Offer offer);

    @Named("mapToOfferDto")
    @Mappings({
            @Mapping(target = "model.colors", source = "model.colors", qualifiedByName = "mapColorsToIds"),
            @Mapping(target = "model.sizes", source = "model.sizes", qualifiedByName = "mapSizesToIds"),
            @Mapping(target = "model.defaultId", expression = "java(mapToDefaultId(model.getProducts(), model.getColors()))")
    })
    OfferModelsDTO toOfferModelsDto(OfferModel offerModel);

    @Named("mapToDefaultId")
    default Long mapToDefaultId(List<Product> products, List<Color> colors) {
        // Extract the first color from the List<Color> if present, otherwise set it to null
        Color targetColor = colors.size()>1 ? null : colors.get(0);

        return products
                .stream()
                .filter(product -> (product.getColor() == null ? targetColor == null : product.getColor().equals(targetColor))
                        && product.getSize() == null)
                .findFirst()
                .map(Product::getId)
                .orElse(null);
    }

    @Named("mapFbPagesToIds")
    default Set<Long> mapFbPagesToIds(Set<FbPage> fbPages) {
        return fbPages.stream()
                .map(FbPage::getId)
                .collect(Collectors.toSet());
    }

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