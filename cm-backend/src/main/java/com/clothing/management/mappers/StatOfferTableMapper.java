package com.clothing.management.mappers;

import com.clothing.management.dto.ModelDTO;
import com.clothing.management.dto.OfferDTO;
import com.clothing.management.dto.StatOfferTableDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface StatOfferTableMapper {

    StatOfferTableMapper INSTANCE = Mappers.getMapper(StatOfferTableMapper.class);

    @Mappings({
            @Mapping(target = "name", source = "offerDTO.name"),
            @Mapping(target = "purchasePrice", constant = "0.0"),
            @Mapping(target = "sellingPrice", constant = "0.0"),
            // Set default values explicitly for inherited fields from StatTableDTO
            @Mapping(target = "Payed", constant = "0L"),
            @Mapping(target = "Retour", constant = "0L"),
            @Mapping(target = "Min", constant = "1000L"),
            @Mapping(target = "Max", constant = "0L"),
            @Mapping(target = "Avg", constant = "0L"),
            @Mapping(target = "Progress", constant = "0L"),
            @Mapping(target = "profits", constant = "0.0")
    })
    StatOfferTableDTO offerToStatOfferTableDTO(OfferDTO offerDTO);

    @Mappings({
            @Mapping(target = "name", source = "modelDTO.name"),
            @Mapping(target = "purchasePrice", constant = "0.0"),
            @Mapping(target = "sellingPrice", constant = "0.0"),
            // Set default values explicitly for inherited fields from StatTableDTO
            @Mapping(target = "Payed", constant = "0L"),
            @Mapping(target = "Retour", constant = "0L"),
            @Mapping(target = "Min", constant = "1000L"),
            @Mapping(target = "Max", constant = "0L"),
            @Mapping(target = "Avg", constant = "0L"),
            @Mapping(target = "Progress", constant = "0L"),
            @Mapping(target = "profits", constant = "0.0")
    })
    StatOfferTableDTO modelToStatModelTableDTO(ModelDTO modelDTO);
}
