package com.clothing.management.entities;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Table(name = "delivery_company")
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DeliveryCompany {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String token;

    @Column(name = "api_name")
    private String apiName;

    @Column(name = "barre_code_url")
    private String barreCodeUrl;

    @Builder.Default
    private boolean deleted = false;

    @Column(name = "additional_name")
    private String additionalName;
}
