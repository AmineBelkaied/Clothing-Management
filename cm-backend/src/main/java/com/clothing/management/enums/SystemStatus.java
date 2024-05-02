package com.clothing.management.enums;

public enum SystemStatus {

    CREATION("Creation"),
    LIVREE("Livrée"),
    INJOIGNABLE("Injoignable"),
    CONFIRMEE("Confirmée"),
    NON_CONFIRMEE("Non confirmée"),
    EN_COURS_1("En cours (1)"),
    EN_COURS_2("En cours (2)"),
    EN_COURS_3("En cours (3)"),

    AU_MAGASIN("Au magasin"),
    A_VERIFIER("A verifier"),
    RETOUR("Retour"),
    PROBLEM("Problème"),

    RETOUR_EXCHANGE("Retour Echange"),
    RETOUR_RECU("Retour reçu"),

    NOTSERIOUS("Pas Serieux"),

    CANCELED("Annuler"),
    RETOUR_EXPEDITEUR("Retour Expediteur"),
    PAYEE("Payée"),

    EXCHANGE("Echange"),
    INCORRECT_BARCODE("Code à barre incorrect"),
    DELETED("Supprimé"),
    ENDED("En rupture"),
    OTHER_STATUS();

    private String status;

    SystemStatus(String status) {
        this.status = status;
    }

    SystemStatus() {
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }


    // From the String method, it will return you the Enum for the provided input string
    public static SystemStatus fromString(String status) {
        if (status != null) {
            for (SystemStatus systemStatus : SystemStatus.values()) {
                if (status.equalsIgnoreCase(systemStatus.status)) {
                    return systemStatus;
                }
            }
        }
        return OTHER_STATUS;
    }
}
