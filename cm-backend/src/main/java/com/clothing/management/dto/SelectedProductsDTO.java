package com.clothing.management.dto;

import com.clothing.management.entities.Product;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SelectedProductsDTO {

    private Long idPacket;
    private double totalPrice;
    private double deliveryPrice;
    private double discount;

    private String packetDescription;
    private List<ProductOfferDTO> productsOffers;

    private String status;

    public SelectedProductsDTO() {
    }

    public SelectedProductsDTO(Long idPacket, double totalPrice, double deliveryPrice, double discount, String packetDescription, List<ProductOfferDTO> productsOffers,String status) {
        this.idPacket = idPacket;
        this.totalPrice = totalPrice;
        this.deliveryPrice = deliveryPrice;
        this.discount = discount;
        this.packetDescription = packetDescription;
        this.productsOffers = productsOffers;
        this.status = status;
    }

    public Long getIdPacket() {
        return idPacket;
    }

    public void setIdPacket(Long idPacket) {
        this.idPacket = idPacket;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public double getDeliveryPrice() {
        return deliveryPrice;
    }

    public void setDeliveryPrice(double deliveryPrice) {
        this.deliveryPrice = deliveryPrice;
    }

    public double getDiscount() {
        return discount;
    }

    public void setDiscount(double discount) {
        this.discount = discount;
    }

    public List<ProductOfferDTO> getProductsOffers() {
        return productsOffers;
    }

    public void setProductsOffers(List<ProductOfferDTO> productsOffers) {
        this.productsOffers = productsOffers;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPacketDescription() {
        return packetDescription;
    }

    public void setPacketDescription(String packetDescription) {
        this.packetDescription = packetDescription;
    }

    @Override
    public String toString() {
        return "SelectedProductsDTO{" +
                "idPacket=" + idPacket +
                ", totalPrice=" + totalPrice +
                ", deliveryPrice=" + deliveryPrice +
                ", discount=" + discount +
                ", packetDescription='" + packetDescription + '\'' +
                ", productsOffers=" + productsOffers +
                ", status='" + status + '\'' +
                '}';
    }
}

