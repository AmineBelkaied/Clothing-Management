package com.clothing.management.dto;

public class ProductQuantity {

    private Long id;
    private int quantity;
    public ProductQuantity() {
    }

    public ProductQuantity(Long id, int quantity, String reference) {
        this.id = id;
        this.quantity = quantity;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    @Override
    public String toString() {
        return "ProductQuantity{" +
                "id=" + id +
                ", quantity=" + quantity +
                '}';
    }
}
