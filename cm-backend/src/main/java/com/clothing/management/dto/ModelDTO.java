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
    private List<Long> colors = new ArrayList<>();
    private List<Long> sizes = new ArrayList<>();
    private boolean enabled;

    private Long defaultId;

    public ModelDTO(){

    }

    public ModelDTO(Model model){
        this.id = model.getId();
        this.name = model.getName();
        this.description = model.getDescription();
        this.purchasePrice = model.getPurchasePrice();
        this.earningCoefficient = model.getEarningCoefficient();
        this.deleted = model.isDeleted();
        this.colors = model.getColors().stream().map(color -> color.getId()).collect(Collectors.toList());
        this.sizes = model.getSizes().stream().map(size -> size.getId()).collect(Collectors.toList());
        this.enabled = model.isEnabled();
    }
    public ModelDTO(Model model,Boolean needDefault){
        this.id = model.getId();
        this.name = model.getName();
        this.description = model.getDescription();
        this.purchasePrice = model.getPurchasePrice();
        this.earningCoefficient = model.getEarningCoefficient();
        this.deleted = model.isDeleted();
        this.colors = model.getColors().stream().map(color -> color.getId()).collect(Collectors.toList());
        this.sizes = model.getSizes().stream().map(size -> size.getId()).collect(Collectors.toList());
        this.enabled = model.isEnabled();
        this.defaultId = model.getProducts()
                .stream()
                .filter(product -> product.getColor() == null && product.getSize() == null)
                .findFirst()
                .map(Product::getId) // Assuming you want the ID of the product
                .orElse(null); ;
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

    public List<Long> getColors() {
        return colors;
    }

    public void setColors(List<Long> colors) {
        this.colors = colors;
    }

    public List<Long> getSizes() {
        return sizes;
    }

    public void setSizes(List<Long> sizes) {
        this.sizes = sizes;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public Long getDefaultId() {
        return defaultId;
    }

    public void setDefaultId(Long defaultId) {
        this.defaultId = defaultId;
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
                ", defaultId=" + defaultId +
                '}';
    }
}
