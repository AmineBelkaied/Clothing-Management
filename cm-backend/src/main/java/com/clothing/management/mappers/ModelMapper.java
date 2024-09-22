package com.clothing.management.mappers;

import com.clothing.management.dto.ModelDTO;
import com.clothing.management.entities.Model;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface ModelMapper {

    ModelMapper INSTANCE = Mappers.getMapper(ModelMapper.class);

    ModelDTO toDto(Model model);

    Model toEntity(ModelDTO modelDTO);
}