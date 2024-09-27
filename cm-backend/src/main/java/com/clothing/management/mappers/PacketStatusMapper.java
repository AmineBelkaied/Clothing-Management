package com.clothing.management.mappers;

import com.clothing.management.dto.PacketStatusDTO;
import com.clothing.management.entities.PacketStatus;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface PacketStatusMapper {

    PacketStatusMapper INSTANCE = Mappers.getMapper(PacketStatusMapper.class);

    @Mappings({
            @Mapping(target = "user", source = "user.userName"),
    })
    PacketStatusDTO toDto(PacketStatus packetStatus);
}