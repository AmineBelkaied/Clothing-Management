package com.clothing.management.enums;

public enum ClientReason {

    UNREACHABLE("Client unreachable"),
    WILL_CALL_BACK("Will call back"),
    PRODUCT_VERIFICATION("Change of product"),
    SIZE_VERIFICATION("Change of size"),
    INCORRECT_NUMBER("Incorrect number provided"),
    BUSY_NUMBER("Number busy"),

    PRODUCT_OOS("Product out of stock"),
    UNAVAILABLE_SIZE("Unavailable size");

    private final String description;

    ClientReason(String description) {
        this.description = description;;
    }

    public String getDescription() {
        return description;
    }
}