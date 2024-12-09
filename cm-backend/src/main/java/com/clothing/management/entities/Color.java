package com.clothing.management.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Objects;
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
public class Color implements Comparable<Color>{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String hex;

    @JsonIgnore
    @ManyToMany(mappedBy = "colors")
    @Builder.Default
    private Set<Model> models = new HashSet<>();

    @Builder.Default
    private boolean deleted = false;

    public Color(Long id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Color color = (Color) o;
        return Objects.equals(id, color.id);
    }

    @Override
    public int hashCode() {
        return Long.hashCode(id);
    }

    @Override
    public int compareTo(Color o) {
        return o.getId().compareTo(this.getId());
    }
}
