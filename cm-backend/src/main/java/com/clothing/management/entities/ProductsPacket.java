package com.clothing.management.entities;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;


@Entity
@Table(name="products_packet", indexes = {
        @Index(name = "idx_packet_id", columnList = "packet_id")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"product","offer","packet"})
@Builder
public class ProductsPacket {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @JsonManagedReference("productsPacket-product")
    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    @JsonManagedReference(value = "productsPacket-offre")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "offer_id")
    private Offer offer;

    @JsonBackReference(value = "packet-productsPacket")
    @ManyToOne
    @JoinColumn(name = "packet_id")
    private Packet packet;

    @Column(name = "packet_offer_id")
    Long packetOfferId;

    double profits;
}
