package com.clothing.management.mappers;

import com.clothing.management.dto.FbPageDTO;
import com.clothing.management.entities.FbPage;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface FbPageMapper {

    FbPageMapper INSTANCE = Mappers.getMapper(FbPageMapper.class);

    @Named("toFbPageDTO")
    FbPageDTO toDto(FbPage fbPage);
}