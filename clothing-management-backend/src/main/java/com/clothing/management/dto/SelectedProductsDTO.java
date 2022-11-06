package com.clothing.management.dto;

import java.util.ArrayList;
import java.util.List;

public class SelectedProductsDTO {

    private Long idPacket;
    private double totalPrice;
    private double deliveryPrice;
    private double discount;
    private List<String> productsRef = new ArrayList<>();
    private String packetRef;
    private String packetDescription;

    public SelectedProductsDTO() {
    }

    public SelectedProductsDTO(Long idPacket, double totalPrice, double deliveryPrice, double discount, List<String> productsRef, String packetRef, String packetDescription) {
        this.idPacket = idPacket;
        this.totalPrice = totalPrice;
        this.deliveryPrice = deliveryPrice;
        this.discount = discount;
        this.productsRef = productsRef;
        this.packetRef = packetRef;
        this.packetDescription = packetDescription;
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

    public List<String> getProductsRef() {
        return productsRef;
    }

    public void setProductsRef(List<String> productsRef) {
        this.productsRef = productsRef;
    }

    public String getPacketRef() {
        return packetRef;
    }

    public void setPacketRef(String packetRef) {
        this.packetRef = packetRef;
    }

    public String getPacketDescription() {
        return packetDescription;
    }

    public void setPacketDescription(String packetDescription) {
        this.packetDescription = packetDescription;
    }
}
