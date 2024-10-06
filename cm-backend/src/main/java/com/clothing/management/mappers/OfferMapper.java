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


    @Mapping(target = "fbPages", source = "fbPages", qualifiedByName = "mapFbPagesToIds")
    @Mapping(target = "offerModels", source = "offerModels")
    OfferDTO toDto(Offer offer);

    @Mapping(target = "model.colors", source = "model.colors", qualifiedByName = "mapColorsToIds")
    @Mapping(target = "model.sizes", source = "model.sizes", qualifiedByName = "mapSizesToIds")
    OfferModelsDTO toOfferModelsDto(OfferModel offerModel);

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