package com.clothing.management.utils;

import com.clothing.management.entities.*;
import com.clothing.management.enums.SystemStatus;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.clothing.management.enums.SystemStatus.CONFIRMED;
import static com.clothing.management.enums.SystemStatus.NOT_CONFIRMED;

@Component
public class PacketBuilderHelper {

    private static final String exchangeIdLabel = "   echange id: ";

    public Packet createNewPacket(Long id, String customerName, String customerPhoneNb, Integer oldClient, City city, String address, String packetDescription, String barcode, String lastDeliveryStatus, List<ProductsPacket> productsPackets, List<PacketStatus> packetStatus, FbPage fbPage, double price, double deliveryPrice, double discount, Date date, SystemStatus status, Date lastUpdateDate, boolean exchange, boolean valid, String printLink, DeliveryCompany deliveryCompany, Long exchangeId) {
        return Packet.builder()
                .id(id)
                .customerName(customerName)
                .customerPhoneNb(customerPhoneNb)
                .oldClient(oldClient)
                .city(city)
                .address(address)
                .packetDescription(packetDescription)
                .barcode(barcode)
                .lastDeliveryStatus(lastDeliveryStatus)
                .productsPackets(productsPackets)
                .packetStatus(packetStatus)
                .fbPage(fbPage)
                .price(price)
                .deliveryPrice(deliveryPrice)
                .discount(discount)
                .date(date)
                .status(status.name())
                .lastUpdateDate(lastUpdateDate)
                .exchange(exchange)
                .valid(valid)
                .printLink(printLink)
                .deliveryCompany(deliveryCompany)
                .exchangeId(exchangeId)
                .build();
    }

    public Packet duplicatePacket(Packet packet, DeliveryCompany deliveryCompany) {
        return Packet.builder()
                .customerName(packet.getCustomerName() + exchangeIdLabel + packet.getId())
                .customerPhoneNb(packet.getCustomerPhoneNb())
                .address(packet.getAddress())
                .packetDescription(packet.getPacketDescription())
                .price(packet.getPrice())
                .status(CONFIRMED.name())
                .fbPage(packet.getFbPage())
                .city(packet.getCity())
                .deliveryPrice(packet.getDeliveryPrice())
                .exchangeId(packet.getId())
                .deliveryCompany(deliveryCompany)
                .discount(packet.getDiscount())
                .barcode("")
                .build();
    }

}
