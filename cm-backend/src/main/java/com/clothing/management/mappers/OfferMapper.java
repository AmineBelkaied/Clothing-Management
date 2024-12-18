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

@Mapper(componentModel = "spring", uses = OfferMapperHelper.class)
public interface OfferMapper {

    OfferMapper INSTANCE = Mappers.getMapper(OfferMapper.class);

    @Mappings({
            @Mapping(target = "fbPages", source = "fbPages", qualifiedByName = "mapFbPagesToIds"),
            @Mapping(target = "offerModels", source = "offerModels", qualifiedByName = "mapToOfferDto")
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
}