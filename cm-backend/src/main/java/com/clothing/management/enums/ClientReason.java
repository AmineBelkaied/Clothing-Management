package com.clothing.management.enums;

public enum ClientReason {

    UNREACHABLE("Client unreachable"),
    WILL_CALL_BACK("Will call back"),
    PRODUCT_VERIFICATION("Change of product"),
    SIZE_VERIFICATION("Change of size"),
    INCORRECT_NUMBER("Incorrect number provided"),
    BUSY_NUMBER("Number busy"),
    PRODUCT_OOS("Product out of stock"),
    UNAVAILABLE_SIZE("Unavailable size"),
    PRODUCT_OOS_CANCEL("Produit indisponible"),
    NO_PHONE_NUMBER("Pas de téléphone"),
    CANCELED_BY_CLIENT("Annuler par le client"),
    WANT_TO_OPEN("Il veut ouvrir");

    private final String description;

    ClientReason(String description) {
        this.description = description;;
    }

    public String getDescription() {
        return description;
    }
}