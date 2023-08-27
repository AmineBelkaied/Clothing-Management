package com.clothing.management.enums;

public enum DiggieStatus {
    LIVREE("Livrée"),
    RETOUR("Retour"),

    RETOUR_RECU("Retour reçu"),
    CONFIRMEE("Confirmée"),
    EN_COURS_1("En cours (1)"),
    EN_COURS_2("En cours (2)"),
    EN_COURS_3("En cours (3)"),

    AU_MAGASIN("Au magasin"),

    A_VERIFIER("A verifier"),

    RETOUR_EXPEDITEUR("Retour Expediteur"),

    PAYEE("Payée"),

    EXCHANGE("Echange"),

    RETOUR_EXCHANGE("Retour Echange"),
    INCORRECT_BARCODE("Code à barre incorrect"),

    DELETED("Supprimé"),
    OTHER_STATUS();

    private String status;

    DiggieStatus(String status) {
        this.status = status;
    }

    DiggieStatus() {
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }


    // From the String method, it will return you the Enum for the provided input string
    public static DiggieStatus fromString(String status) {
        if (status != null) {
            for (DiggieStatus diggieStatus : DiggieStatus.values()) {
                if (status.equalsIgnoreCase(diggieStatus.status)) {
                    return diggieStatus;
                }
            }
        }
        return OTHER_STATUS;
    }
}
