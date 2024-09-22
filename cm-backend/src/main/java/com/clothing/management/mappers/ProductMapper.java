package com.clothing.management.mappers;

import com.clothing.management.dto.ProductDTO;
import com.clothing.management.entities.Product;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    ProductMapper INSTANCE = Mappers.getMapper(ProductMapper.class);

    ProductDTO toDto(Product product);

    Product toEntity(ProductDTO productDTO);
}