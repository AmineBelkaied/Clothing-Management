package com.clothing.management.enums;

public enum ClientReason {

    UNREACHABLE("Client unreachable"),
    WILL_CALL_BACK("Will call back"),
    CANCELED("Order canceled"),
    PRODUCT_CHANGE("Change of product"),
    SIZE_CHANGE("Change of size"),
    COLOR_CHANGE("Change of color"),
    INCORRECT_NUMBER("Incorrect number provided"),
    BUSY_NUMBER("Number busy"),
    NOT_SERIOUS("Not serious about purchase");

    private final String description;

    ClientReason(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}