package com.clothing.management.entities;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import java.util.Objects;

@Entity
@Table(name="products_packet", indexes = {
        @Index(name = "idx_packet_id", columnList = "packet_id")
})
public class ProductsPacket {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @JsonManagedReference
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "product_id")
    private Product product;
    @JsonManagedReference
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "offer_id")
    private Offer offer;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "packet_id")
    private Packet packet;



    @Column(name = "packet_offer_id")
    Long packetOfferId;

    Integer status;
    double profits;

    public ProductsPacket() {
        this.id = null;
        this.status = 0;
    }

    public ProductsPacket(ProductsPacket productPacket,Packet packet) {
        this.packet = packet;
        this.product = productPacket.getProduct();
        this.packetOfferId = productPacket.getPacketOfferId();
        this.offer = productPacket.getOffer();
        this.status = 0;
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
                ", packet=" + packet.getId() +
                ", product=" + product.getId() +
                ", offer=" + offer.getId() +
                ", packetOfferId=" + packetOfferId +
                ", status=" + status +
                ", profits=" + profits +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ProductsPacket that)) return false;
        return Double.compare(that.profits, profits) == 0
                && product.getId().equals(that.product.getId())
                && packet.getId().equals(that.packet.getId())
                && offer.getId().equals(that.offer.getId())
                && packetOfferId.equals(that.packetOfferId)
                && Objects.equals(status, that.status);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, product, packet, offer, packetOfferId, status, profits);
    }
}
