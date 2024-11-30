package com.clothing.management.mappers;

import com.clothing.management.dto.*;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")

public interface StatTableMapper {

    StatTableMapper INSTANCE = Mappers.getMapper(StatTableMapper.class);


    @Mappings({
            @Mapping(target = "name", source = "offerDTO.name"),
            @Mapping(target = "purchasePrice", constant = "0.0"),
            @Mapping(target = "sellingPrice", constant = "0.0"),
            // Set default values explicitly for inherited fields from StatTableDTO
            @Mapping(target = "paid", constant = "0L"),
            @Mapping(target = "retour", constant = "0L"),
            @Mapping(target = "Min", constant = "1000L"),
            @Mapping(target = "Max", constant = "0L"),
            @Mapping(target = "Avg", constant = "0L"),
            @Mapping(target = "Per", constant = "0.0"),
            @Mapping(target = "progress", constant = "0L"),
            @Mapping(target = "received", constant = "0L"),
            @Mapping(target = "profits", constant = "0.0")
    })
    StatOfferTableDTO offerToStatOfferTableDTO(OfferDTO offerDTO);

    @Mappings({
            @Mapping(target = "purchasePrice", constant = "0.0"),
            @Mapping(target = "sellingPrice", constant = "0.0"),
            @Mapping(target = "paid", constant = "0L"),
            @Mapping(target = "retour", constant = "0L"),
            @Mapping(target = "Min", constant = "1000L"),
            @Mapping(target = "Max", constant = "0L"),
            @Mapping(target = "Avg", constant = "0L"),
            @Mapping(target = "Per", constant = "0.0"),
            @Mapping(target = "progress", constant = "0L"),
            @Mapping(target = "profits", constant = "0.0"),
            @Mapping(target = "name", constant = "nameRow"),
    })
    StatOfferTableDTO modelToStatModelTableDTO(String nameRow);

    @Mappings({
            @Mapping(target = "name", source = "nameRow"),
            @Mapping(target = "retour", constant = "0L"),
            @Mapping(target = "Min", constant = "1000L"),
            @Mapping(target = "Max", constant = "0L"),
            @Mapping(target = "Avg", constant = "0L"),
            @Mapping(target = "Per", constant = "0.0"),
            @Mapping(target = "progress", constant = "0L"),
            @Mapping(target = "received", constant = "0L"),
            @Mapping(target = "paid", constant = "0L"),
            @Mapping(target = "profits", constant = "0.0")
    })
    StatTableDTO toStatTableDTO(String nameRow);
}
