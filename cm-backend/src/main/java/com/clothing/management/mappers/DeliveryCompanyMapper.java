package com.clothing.management.mappers;

import com.clothing.management.dto.DeliveryCompanyDTO;
import com.clothing.management.entities.DeliveryCompany;
import org.mapstruct.Mapper;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface DeliveryCompanyMapper {

    DeliveryCompanyMapper INSTANCE = Mappers.getMapper(DeliveryCompanyMapper.class);

    @Named("toDeliveryCompanyDTO")
    DeliveryCompanyDTO toDto(DeliveryCompany deliveryCompany);
}