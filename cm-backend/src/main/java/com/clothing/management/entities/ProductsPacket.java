package com.clothing.management.entities;

import jakarta.persistence.*;
import java.util.Date;
import java.util.Objects;

@Entity
@Table(name="products_packet")
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

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "offer_id")
    Offer offer;

    @Column(name = "packet_offer_id")
    Long packetOfferId;

    Integer status;
    double profits;

    public ProductsPacket() {
    }

    public ProductsPacket(Product product, Packet packet, Offer offer, Long packetOfferId, double profits) {
        this.product = product;
        this.packet = packet;
        this.offer = offer;
        this.packetOfferId = packetOfferId;
        this.profits = profits;
    }


    public ProductsPacket(Product product, Packet packet, Offer offer, Long packetOfferId, Integer status) {
        this.product = product;
        this.packet = packet;
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

    public Offer getOffer() {
        return offer;
    }

    public void setOffer(Offer offer) {
        this.offer = offer;
    }

    public Long getPacketOfferId() {
        return packetOfferId;
    }

    public void setPacketOfferId(Long packetOfferId) {
        this.packetOfferId = packetOfferId;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public double getProfits() {
        return profits;
    }

    public void setProfits(double profits) {
        this.profits = profits;
    }

    @Override
    public String toString() {
        return "ProductsPacket{" +
                "id=" + id +
                ", product=" + product +
                ", packet=" + packet +
                ", offer=" + offer +
                ", packetOfferId=" + packetOfferId +
                ", status=" + status +
                ", profits=" + profits +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ProductsPacket that)) return false;
        return Double.compare(that.profits, profits) == 0 && product.equals(that.product) && packet.equals(that.packet) && offer.equals(that.offer) && packetOfferId.equals(that.packetOfferId) && Objects.equals(status, that.status);
    }

    @Override
    public int hashCode() {
        return Objects.hash(product, packet, offer, packetOfferId, status, profits);
    }
}
