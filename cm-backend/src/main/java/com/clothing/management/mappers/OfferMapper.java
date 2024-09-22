package com.clothing.management.mappers;

import com.clothing.management.dto.OfferDTO;
import com.clothing.management.entities.Offer;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface OfferMapper {

    OfferMapper INSTANCE = Mappers.getMapper(OfferMapper.class);

    OfferDTO toDto(Offer offer);

    Offer toEntity(OfferDTO offerDTO);
}