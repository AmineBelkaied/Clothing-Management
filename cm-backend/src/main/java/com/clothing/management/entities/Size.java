package com.clothing.management.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "size", indexes = {
        @Index(name = "idx_id", columnList = "id")
})
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
@Data
@ToString(exclude = {"models"})
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Size implements Comparable<Size> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String reference;
    private String description;

    @JsonIgnore
    @ManyToMany(mappedBy = "sizes")
    @Builder.Default
    private Set<Model> models = new HashSet<>();

    public Size(Long id) {
        this.id = id;
    }

    @Override
    public int compareTo(Size o) {
        return o.getReference().compareTo(this.getReference());
    }
}
