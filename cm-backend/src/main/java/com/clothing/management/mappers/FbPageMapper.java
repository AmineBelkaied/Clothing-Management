package com.clothing.management.mappers;

import com.clothing.management.dto.FbPageDTO;
import com.clothing.management.entities.FbPage;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface FbPageMapper {

    FbPageMapper INSTANCE = Mappers.getMapper(FbPageMapper.class);

    FbPageDTO toDto(FbPage fbPage);

    FbPage toEntity(FbPageDTO fbPageDTO);
}