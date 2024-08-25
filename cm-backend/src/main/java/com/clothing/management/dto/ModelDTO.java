package com.clothing.management.dto;

import com.clothing.management.entities.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class ModelDTO {

    private Long id;
    private String  name;
    private String description;
    private float purchasePrice;
    private double earningCoefficient;
    private boolean deleted;
    private List<Color> colors = new ArrayList<>();
    private List<Size> sizes = new ArrayList<>();
    private boolean enabled;

    public ModelDTO(){

    }

    public ModelDTO(Model model){
        this.id = model.getId();
        this.name = model.getName();
        this.description = model.getDescription();
        this.purchasePrice = model.getPurchasePrice();
        this.earningCoefficient = model.getEarningCoefficient();
        this.deleted = model.isDeleted();
        this.colors = model.getColors();
        this.sizes = model.getSizes();
        this.enabled = model.isEnabled();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public float getPurchasePrice() {
        return purchasePrice;
    }

    public void setPurchasePrice(float purchasePrice) {
        this.purchasePrice = purchasePrice;
    }

    public double getEarningCoefficient() {
        return earningCoefficient;
    }

    public void setEarningCoefficient(double earningCoefficient) {
        this.earningCoefficient = earningCoefficient;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public List<Color> getColors() {
        return colors;
    }

    public void setColors(List<Color> colors) {
        this.colors = colors;
    }

    public List<Size> getSizes() {
        return sizes;
    }

    public void setSizes(List<Size> sizes) {
        this.sizes = sizes;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    public String toString() {
        return "ModelDTO{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", purchasePrice=" + purchasePrice +
                ", earningCoefficient=" + earningCoefficient +
                ", deleted=" + deleted +
                ", colors=" + colors +
                ", sizes=" + sizes +
                ", enabled=" + enabled +
                '}';
    }
}
