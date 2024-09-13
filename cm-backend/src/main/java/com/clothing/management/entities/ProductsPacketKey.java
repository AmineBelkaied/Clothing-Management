package com.clothing.management.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductsPacketKey implements Serializable {

    @Column(name = "product_id")
    Long productId;

    @Column(name = "packet_id")
    Long packetId;

    String reference;
}
