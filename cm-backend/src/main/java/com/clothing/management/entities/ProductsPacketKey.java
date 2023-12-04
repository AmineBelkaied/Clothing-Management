package com.clothing.management.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class ProductsPacketKey implements Serializable {

    @Column(name = "product_id")
    Long productId;

    @Column(name = "packet_id")
    Long packetId;

    String reference;

    public ProductsPacketKey() {
    }



    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public Long getPacketId() {
        return packetId;
    }

    public void setPacketId(Long packetId) {
        this.packetId = packetId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ProductsPacketKey)) return false;
        ProductsPacketKey that = (ProductsPacketKey) o;
        return Objects.equals(getProductId(), that.getProductId()) &&
                Objects.equals(getPacketId(), that.getPacketId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getProductId(), getPacketId());
    }

    @Override
    public String toString() {
        return "ProductsPacketKey{" +
                "productId=" + productId +
                ", packetId=" + packetId +
                '}';
    }
}
