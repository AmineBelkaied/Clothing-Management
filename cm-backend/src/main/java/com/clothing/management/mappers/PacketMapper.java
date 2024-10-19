package com.clothing.management.mappers;

import com.clothing.management.dto.PacketDTO;
import com.clothing.management.dto.PacketValidationDTO;
import com.clothing.management.entities.Packet;
import com.clothing.management.entities.ProductsPacket;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring", uses = {DeliveryCompanyMapper.class})
public interface PacketMapper {
    PacketMapper INSTANCE = Mappers.getMapper(PacketMapper.class);

    @Mappings({
            @Mapping(target = "deliveryCompanyId", source = "deliveryCompany.id"),
            @Mapping(target = "fbPageId", source = "fbPage.id"),
            @Mapping(target = "cityId", source = "city.id"),
            @Mapping(target = "cityName", expression = "java(packet.getCity() != null ? packet.getCity().getGovernorate().getName() + '-' + packet.getCity().getName() : \"\")"),
            @Mapping(target = "totalPrice", expression = "java(packet.getPrice() + packet.getDeliveryPrice() - packet.getDiscount())"),
            @Mapping(target = "stock", expression = "java(packet.getProductsPackets().size() > 0 ? PacketMapper.getStock(packet.getProductsPackets(), packet.getBarcode()) : 0)")
    })
    PacketDTO toDto(Packet packet);

    @Mappings({
            @Mapping(target = "fbPageName", source = "fbPage.name"),
            @Mapping(target = "deliveryCompanyId", source = "deliveryCompany.id"),
            @Mapping(target = "price", expression = "java(packet.getPrice() + packet.getDeliveryPrice() - packet.getDiscount())")
    })
    PacketValidationDTO toValidationDto(Packet packet);

    static long getStock(List<ProductsPacket> productsPackets, String barcode) {
        return (barcode == null || barcode.isEmpty()) ? productsPackets.stream()
                .mapToLong(productsPacket -> productsPacket.getProduct().getQuantity()) // Assuming getQte() returns the quantity
                .min()
                .orElse(-1) : 100;
    }
}
