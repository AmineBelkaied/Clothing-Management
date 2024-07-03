package com.clothing.management.enums;

public enum DeliveryCompanyName {
    FIRST("First"),
    EXPRESS("Express"),
    JAX("Jax"),
    NAVEX("Navex");


    private String name;

    DeliveryCompanyName() {
    }

    DeliveryCompanyName(String name) {  this.name = name; }

    public String getName() {  return name; }

    public void setName(String name) {
        this.name = name;
    }

    public static DeliveryCompanyName fromString(String name) {
        if (name != null) {
            for (DeliveryCompanyName deliveryCompanyName : DeliveryCompanyName.values()) {
                if (name.equalsIgnoreCase(deliveryCompanyName.name)) {
                    return deliveryCompanyName;
                }
            }
        }
        throw new IllegalArgumentException("No enum constant " + DeliveryCompanyName.class.getCanonicalName() + "." + name);
    }
}
