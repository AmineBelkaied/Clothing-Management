package com.clothing.management.dto;

public class ProductQuantity {

    private Long id;
    private int quantity;
    private int enteredQuantity;
    private String reference;
    public ProductQuantity() {
    }

    public ProductQuantity(Long id, int quantity, int enteredQuantity, String reference) {
        this.id = id;
        this.quantity = quantity;
        this.enteredQuantity = enteredQuantity;
        this.reference = reference;
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

    public int getEnteredQuantity() {
        return enteredQuantity;
    }

    public void setEnteredQuantity(int enteredQuantity) {
        this.enteredQuantity = enteredQuantity;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    @Override
    public String toString() {
        return "ProductQuantity{" +
                "id=" + id +
                ", quantity=" + quantity +
                ", enteredQuantity=" + enteredQuantity +
                ", reference='" + reference + '\'' +
                '}';
    }
}
