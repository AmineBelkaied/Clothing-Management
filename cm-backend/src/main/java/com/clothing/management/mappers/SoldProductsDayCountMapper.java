package com.clothing.management.mappers;

import com.clothing.management.dto.DayCount.SoldProductsDayCountDTO;
import com.clothing.management.entities.Product;
import lombok.NoArgsConstructor;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface SoldProductsDayCountMapper {

    SoldProductsDayCountMapper INSTANCE = Mappers.getMapper(SoldProductsDayCountMapper.class);

    @Mappings({
            @Mapping(target = "qte", source = "quantity"),
            @Mapping(target = "colorId", source = "color.id"),
            @Mapping(target = "sizeId", source = "size.id"),
    })
    SoldProductsDayCountDTO produtToSoldProductsDayCountDTO(Product product);
}
