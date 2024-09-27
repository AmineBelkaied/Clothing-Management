package com.clothing.management.dto;

import com.clothing.management.entities.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PacketDTO {
    private long id;
    private Date date;
    private String customerName;
    private String customerPhoneNb;
    private Long cityId;

    private String cityName;
    private String address;
    private String packetDescription;
    private String barcode;
    private String lastDeliveryStatus;
    private Integer oldClient;
    private FbPageDTO fbPage;
    private String deliveryCompanyName;
    private double price;
    private double deliveryPrice;
    private double discount;
    private String status;
    private Date lastUpdateDate;
    private String printLink;
    private boolean valid;
    private long stock;
    private long exchangeId;
    private boolean haveExchange;
    private double totalPrice;

    private List<Note> notes;

    public PacketDTO(Packet packet) {
            FbPage fbPage =packet.getFbPage();
            City city = packet.getCity();
            this.id = packet.getId();
            this.customerName= packet.getCustomerName();
            this.customerPhoneNb= packet.getCustomerPhoneNb();
            this.oldClient= packet.getOldClient();
            this.cityId = city != null ? city.getId() : null;
            this.address= packet.getAddress();
            this.packetDescription= packet.getPacketDescription();
            this.barcode= packet.getBarcode();
            this.lastDeliveryStatus = packet.getLastDeliveryStatus();
            this.fbPage = fbPage != null ? FbPageDTO.builder().id(fbPage.getId()).name(fbPage.getName()).build() : null;
            this.price= packet.getPrice();
            this.deliveryPrice = packet.getDeliveryPrice();
            this.discount = packet.getDiscount();
            this.status= packet.getStatus();
            this.date= packet.getDate();
            this.lastUpdateDate = packet.getLastUpdateDate();
            this.valid= packet.isValid();
            this.stock= !packet.getProductsPackets().isEmpty() ? getStock(packet.getProductsPackets(), packet.getBarcode()):0;
            this.printLink = packet.getPrintLink();
            this.deliveryCompanyName =packet.getDeliveryCompany().getName();
            this.haveExchange=packet.isHaveExchange();
            this.notes = packet.getNotes();
            this.totalPrice = packet.getPrice()+packet.getDeliveryPrice()-packet.getDiscount();
            this.cityName = city != null ? city.getGovernorate().getName() + '-' + city.getName() : "";
    }

    private long getStock(List<ProductsPacket> productsPackets, String barcode) {
        return (barcode == null || barcode.equals("")) ? productsPackets.stream()
                .mapToLong(productsPacket -> productsPacket.getProduct().getQuantity()) // Assuming getQte() returns the quantity
                .min()
                .orElse(-1) : 100;
    }
}
