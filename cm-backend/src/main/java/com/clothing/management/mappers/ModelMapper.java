package com.clothing.management.mappers;

import com.clothing.management.dto.ModelDTO;
import com.clothing.management.entities.*;
import com.clothing.management.repository.IColorRepository;
import com.clothing.management.repository.ISizeRepository;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", uses = ModelMapperHelper.class)
public interface ModelMapper {

    ModelMapper INSTANCE = Mappers.getMapper(ModelMapper.class);
    @Mappings({
            @Mapping(target = "colors", source = "colors", qualifiedByName = "mapIdsToColors"),
            @Mapping(target = "sizes", source = "sizes", qualifiedByName = "mapIdsToSizes"),
            @Mapping(target = "deleted", source = "deleted")
    })
    Model toEntity(ModelDTO modelDTO);

    @Mappings({
            @Mapping(target = "colors", source = "colors", qualifiedByName = "mapColorsToIds"),
            @Mapping(target = "sizes", source = "sizes", qualifiedByName = "mapSizesToIds"),
            @Mapping(target = "defaultId", source = "products", qualifiedByName = "mapToDefaultId")
    })
    ModelDTO toDto(Model model);
}