package com.clothing.management.enums;

public enum ProductType {
    COLOR("couleur"),
    SIZE("taille");

    private final String description;

    ProductType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
