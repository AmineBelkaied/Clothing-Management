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
@ToString(exclude = {"offerModels","fbPages"})
public class Offer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Builder.Default
    @OneToMany(mappedBy = "offer", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private Set<OfferModel> offerModels = new HashSet<>();

    @Builder.Default
    @JsonBackReference(value = "productsPacket-offre")
    @OneToMany(mappedBy = "offer", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ProductsPacket> productsPacket = new ArrayList<>();


    @JsonIgnore
    @ManyToMany(cascade = { CascadeType.PERSIST }, fetch = FetchType.EAGER)
    @JoinTable(
            name = "offer_fb_pages",
            joinColumns = { @JoinColumn(name = "offer_id") },
            inverseJoinColumns = { @JoinColumn(name = "fb_page_id") }
    )
    @Builder.Default
    private List<FbPage> fbPages = new ArrayList<>();

    @Column(nullable = false)
    private Double price;

    @Column(name = "is_enabled", nullable = false)
    @Builder.Default
    private boolean enabled = true;

    @Column(name = "is_deleted", nullable = false)
    @Builder.Default
    private boolean deleted = false;
}
