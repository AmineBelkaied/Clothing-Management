package com.clothing.management.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name="packet_details")
public class PacketDetails {

    @Id
    private Long packetId;
    private String offerName;
    private String modelName;
    private int quantity;
    private String colors;
    private String sizes;
    private String barcode;

    public PacketDetails() {
    }

    public PacketDetails(Long packetId, String offerName, String modelName, int quantity, String colors, String sizes) {
        this.packetId = packetId;
        this.offerName = offerName;
        this.modelName = modelName;
        this.quantity = quantity;
        this.colors = colors;
        this.sizes = sizes;
    }

    public Long getPacketId() {
        return packetId;
    }

    public void setPacketId(Long packetId) {
        this.packetId = packetId;
    }

    public String getOfferName() {
        return offerName;
    }

    public void setOfferName(String offerName) {
        this.offerName = offerName;
    }

    public String getModelName() {
        return modelName;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getColors() {
        return colors;
    }

    public void setColors(String colors) {
        this.colors = colors;
    }

    public String getSizes() {
        return sizes;
    }

    public void setSizes(String sizes) {
        this.sizes = sizes;
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    @Override
    public String toString() {
        return "PacketDetails{" +
                "packetId=" + packetId +
                ", offerName='" + offerName + '\'' +
                ", modelName='" + modelName + '\'' +
                ", quantity=" + quantity +
                ", colors='" + colors + '\'' +
                ", sizes='" + sizes + '\'' +
                ", barcode='" + barcode + '\'' +
                '}';
    }
}
