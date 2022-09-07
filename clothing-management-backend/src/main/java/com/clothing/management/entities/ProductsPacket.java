package com.clothing.management.entities;

import javax.persistence.*;
import java.util.Date;

@Entity
public class ProductsPacket {

    @EmbeddedId
    ProductsPacketKey id  = new ProductsPacketKey();

    @ManyToOne
    @MapsId("productId")
    @JoinColumn(name = "product_id")
    Product product;

    @ManyToOne
    @MapsId("packetId")
    @JoinColumn(name = "packet_id")
    Packet packet;

    Date packetDate;

    public ProductsPacket() {
    }

    public ProductsPacket(Product product, Packet packet, Date packetDate) {
        this.product = product;
        this.packet = packet;
        this.packetDate = packetDate;
    }

    public ProductsPacketKey getId() {
        return id;
    }

    public void setId(ProductsPacketKey id) {
        this.id = id;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public Packet getPacket() {
        return packet;
    }

    public void setPacket(Packet packet) {
        this.packet = packet;
    }

    public Date getPacketDate() {
        return packetDate;
    }

    public void setPacketDate(Date packetDate) {
        this.packetDate = packetDate;
    }

    @Override
    public String toString() {
        return "ProductsPacket{" +
                "id=" + id +
                ", product=" + product +
                ", packet=" + packet +
                ", packetDate=" + packetDate +
                '}';
    }
}
