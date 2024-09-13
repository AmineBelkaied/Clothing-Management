package com.clothing.management.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Entity
@Table(name="product_history", indexes = {
        @Index(name = "idx_product_id", columnList = "product_id")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    private Long quantity;

    @Column(name = "last_modification_date")
    private Date lastModificationDate;

    @JoinColumn(name = "model_id")
    @JsonIgnore
    @ManyToOne
    Model model;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    private String comment;
}
