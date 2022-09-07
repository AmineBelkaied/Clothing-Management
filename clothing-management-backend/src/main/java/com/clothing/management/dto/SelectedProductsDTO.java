package com.clothing.management.dto;

import java.util.ArrayList;
import java.util.List;

public class SelectedProductsDTO {

    private Long idPacket;
    private Double price;
    private List<String> productsRef = new ArrayList<>();
    private String packetRef;

    public SelectedProductsDTO() {
    }

    public SelectedProductsDTO(Long idPacket, Double price, String packetRef, List<String> productsRef) {
        this.idPacket = idPacket;
        this.price = price;
        this.productsRef = productsRef;
        this.packetRef = packetRef;
    }

    public Long getIdPacket() {
        return idPacket;
    }

    public void setIdPacket(Long idPacket) {
        this.idPacket = idPacket;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public String getPacketRef() { return packetRef; }

    public void setPacketRef(String packetRef) { this.packetRef = packetRef; }

    public List<String> getProductsRef() {
        return productsRef;
    }

    public void setProductsRef(List<String> productsRef) {
        this.productsRef = productsRef;
    }

    @Override
    public String toString() {
        return "SelectedProductsDTO{" +
                "idPacket=" + idPacket +
                ", price=" + price +
                ", productsRef=" + productsRef +
                ", packetRef='" + packetRef + '\'' +
                '}';
    }
}
