package com.clothing.management.entities;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "city", indexes = {
        @Index(name = "idx_id", columnList = "id"),
        @Index(name = "idx_governorate_id", columnList = "governorate_id")
})
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class City {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @ManyToOne
    @JoinColumn(name="governorate_id")
    Governorate governorate;

    @Column(name = "postal_code")
    private String postalCode;
}
