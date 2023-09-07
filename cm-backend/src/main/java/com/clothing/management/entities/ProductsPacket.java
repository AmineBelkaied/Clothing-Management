package com.clothing.management.entities;

import javax.persistence.*;
import java.util.Date;
import java.util.Objects;

@Entity
public class ProductsPacket {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @ManyToOne
    @JoinColumn(name = "product_id")
    Product product;

    @ManyToOne
    @JoinColumn(name = "packet_id")
    Packet packet;

    Date packetDate;

    @OneToOne
    @JoinColumn(name = "offer_id")
    Offer offer;

    Integer packetOfferId;

    Integer status;

    public ProductsPacket() {
    }

    public ProductsPacket(Product product, Packet packet, Date packetDate, Offer offer, Integer packetOfferId) {
        this.product = product;
        this.packet = packet;
        this.packetDate = packetDate;
        this.offer = offer;
        this.packetOfferId = packetOfferId;
    }


    public ProductsPacket(Product product, Packet packet, Date packetDate, Offer offer, Integer packetOfferId, Integer status) {
        this.product = product;
        this.packet = packet;
        this.packetDate = packetDate;
        this.offer = offer;
        this.packetOfferId = packetOfferId;
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
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

    public Offer getOffer() {
        return offer;
    }

    public void setOffer(Offer offer) {
        this.offer = offer;
    }

    public Integer getPacketOfferId() {
        return packetOfferId;
    }

    public void setPacketOfferId(Integer packetOfferId) {
        this.packetOfferId = packetOfferId;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "ProductsPacket{" +
                "id=" + id +
                ", product=" + product +
                ", packet=" + packet +
                ", packetDate=" + packetDate +
                ", offer=" + offer +
                ", packetOfferId=" + packetOfferId +
                ", status=" + status +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ProductsPacket)) return false;
        ProductsPacket that = (ProductsPacket) o;
        return getId().equals(that.getId()) && getProduct().equals(that.getProduct()) && getPacket().equals(that.getPacket()) && getPacketDate().equals(that.getPacketDate()) && getOffer().equals(that.getOffer());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getProduct(), getPacket(), getPacketDate(), getOffer());
    }
}
