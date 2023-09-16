package com.clothing.management.enums;

public enum DiggieStatus {
    LIVREE("Livrée"),
    CONFIRMEE("Confirmée"),
    NON_CONFIRMEE("Non confirmée"),
    EN_COURS_1("En cours (1)"),
    EN_COURS_2("En cours (2)"),
    EN_COURS_3("En cours (3)"),

    AU_MAGASIN("Au magasin"),
    A_VERIFIER("A verifier"),
    RETOUR("Retour"),
    RETOUR_EXPEDITEUR("Retour Expediteur"),
    RETOUR_EXCHANGE("Retour Echange"),
    RETOUR_RECU("Retour reçu"),
    PAYEE("Payée"),

    EXCHANGE("Echange"),
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
