package com.clothing.management.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.*;

@Entity
@Table(name = "offer", indexes = {
        @Index(name = "idx_id", columnList = "id")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
public class Offer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @OneToMany(mappedBy = "offer", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @Builder.Default
    private Set<OfferModel> offerModels = new HashSet<>();

    @JsonIgnore
    @JsonBackReference
    @OneToMany(mappedBy = "offer", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<ProductsPacket> productsPacket = new ArrayList<>();

    @ManyToMany(cascade = { CascadeType.MERGE }, fetch = FetchType.EAGER)
    @JoinTable(
            name = "offer_fb_pages",
            joinColumns = { @JoinColumn(name = "offer_id") },
            inverseJoinColumns = { @JoinColumn(name = "fb_page_id") }
    )
    @Builder.Default
    private Set<FbPage> fbPages = new HashSet<>();

    @Column(nullable = false)
    private Double price;

    @Column(name = "is_enabled", nullable = false)
    @Builder.Default
    private boolean isEnabled = false;

    @Column(name = "is_deleted", nullable = false)
    @Builder.Default
    private boolean isDeleted = false;
}
