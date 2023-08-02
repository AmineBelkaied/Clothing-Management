package com.clothing.management.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
public class Size {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String reference;
    private String description;
    @ManyToMany(mappedBy = "colors")
    @JsonBackReference
    private Set<Model> models = new HashSet<>();;


    public Size() {
    }

    public Size(Long id, String reference, String description) {
        this.id = id;
        this.reference = reference;
        this.description = description;
    }

    public Size(String reference) {
        this.reference = reference;
    }

    public Size(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "Size{" +
                "id=" + id +
                ", reference='" + reference + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}
