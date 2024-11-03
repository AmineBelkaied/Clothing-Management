package com.clothing.management.entities;

import com.fasterxml.jackson.annotation.*;
import jakarta.persistence.*;
import lombok.*;

import java.util.*;

@Entity
@Table(name = "model", indexes = {
        @Index(name = "idx_id", columnList = "id")
})
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class,
        property = "id",
        scope = Model.class)
@Data
@Builder
@ToString(exclude = {"modelOffers","products","colors","sizes","productHistories"})
@NoArgsConstructor
@AllArgsConstructor
public class Model {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @JsonBackReference
    @OneToMany(mappedBy = "model", fetch = FetchType.LAZY)
    private Set<OfferModel> modelOffers = new HashSet<>();

    @JsonIgnore
    @OneToMany(mappedBy = "model", cascade = CascadeType.MERGE, fetch = FetchType.LAZY)
    private List<Product> products;

    private String description;

    @Column(name = "purchase_price")
    @Builder.Default
    private float purchasePrice = 15;

    @Column(name = "earning_coefficient")
    @Builder.Default
    private double earningCoefficient = 1.5;

    @Column(name = "is_enabled", nullable = false)
    @Builder.Default
    private boolean isEnabled = false;

    @Column(name = "is_deleted", nullable = false)
    @Builder.Default
    private boolean isDeleted = false;

    @ManyToMany(cascade = {CascadeType.MERGE}, fetch = FetchType.EAGER)
    @JoinTable(
            name = "model_colors",
            joinColumns = @JoinColumn(name = "model_id"),
            inverseJoinColumns = @JoinColumn(name = "color_id")
    )
    private List<Color> colors = new ArrayList<>();

    @ManyToMany(cascade = {CascadeType.MERGE}, fetch = FetchType.EAGER)
    @JoinTable(
            name = "model_sizes",
            joinColumns = @JoinColumn(name = "model_id"),
            inverseJoinColumns = @JoinColumn(name = "size_id")
    )
    private List<Size> sizes = new ArrayList<>();

    @JsonIgnore
    @OneToMany(mappedBy = "model", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ProductHistory> productHistories;

    @JsonIgnore
    @OneToMany(mappedBy = "model", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ModelStockHistory> modelStockHistories;


}
