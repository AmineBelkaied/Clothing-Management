package com.clothing.management.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "model_image")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ModelImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String type;

    @Column(name = "image_path")
    private String imagePath;

    @OneToOne
    @JoinColumn(name = "model_id")
    private Model model;
}