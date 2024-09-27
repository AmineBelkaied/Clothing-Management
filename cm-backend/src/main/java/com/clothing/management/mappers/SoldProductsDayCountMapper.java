package com.clothing.management.mappers;

import com.clothing.management.dto.DayCount.SoldProductsDayCountDTO;
import com.clothing.management.entities.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface SoldProductsDayCountMapper {

    SoldProductsDayCountMapper INSTANCE = Mappers.getMapper(SoldProductsDayCountMapper.class);

    @Mappings({
            @Mapping(target = "qte", source = "quantity")
    })
    SoldProductsDayCountDTO produtToSoldProductsDayCountDTO(Product product);
}
