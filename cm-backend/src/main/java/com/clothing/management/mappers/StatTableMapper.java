package com.clothing.management.mappers;

import com.clothing.management.dto.ModelDTO;
import com.clothing.management.dto.OfferDTO;
import com.clothing.management.dto.StatOfferTableDTO;
import com.clothing.management.dto.StatTableDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface StatTableMapper {

    StatTableMapper INSTANCE = Mappers.getMapper(StatTableMapper.class);

    @Mappings({
            @Mapping(target = "name", source = "pageName"),
            @Mapping(target = "payed", constant = "0L"),
            @Mapping(target = "retour", constant = "0L"),
            @Mapping(target = "Min", constant = "1000L"),
            @Mapping(target = "Max", constant = "0L"),
            @Mapping(target = "Avg", constant = "0L"),
            @Mapping(target = "progress", constant = "0L"),
            @Mapping(target = "profits", constant = "0.0")
    })
    StatTableDTO pageToStatTableDTO(String pageName);
}
