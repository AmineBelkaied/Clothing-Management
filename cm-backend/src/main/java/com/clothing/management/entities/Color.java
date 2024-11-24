package com.clothing.management.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "color", indexes = {
        @Index(name = "idx_id", columnList = "id")
})
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
@Data
@ToString(exclude = {"models"})
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Color {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String hex;

    @ManyToMany(mappedBy = "colors")
    @JsonBackReference
    @Builder.Default
    private Set<Model> models = new HashSet<>();

    @Builder.Default
    private boolean deleted = false;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Color color = (Color) o;
        return id == color.id;
    }

    @Override
    public int hashCode() {
        return Long.hashCode(id);
    }
}
