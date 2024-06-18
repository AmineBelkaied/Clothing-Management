package com.clothing.management.enums;

public enum SystemStatus {

    CREATION("Creation"),
    LIVREE("Livrée"),
    UNREACHABLE("Injoignable"),
    CONFIRMED("Confirmée"),
    NOT_CONFIRMED("Non confirmée"),
    IN_PROGRESS_1("En cours (1)"),
    IN_PROGRESS_2("En cours (2)"),
    IN_PROGRESS_3("En cours (3)"),

    AU_MAGASIN("Au magasin"),
    TO_VERIFY("A verifier"),
    RETURN("Retour"),
    PROBLEM("Problème"),

    RETOUR_EXCHANGE("Retour Echange"),
    RETURN_RECEIVED("Retour reçu"),
    NOTSERIOUS("Pas Serieux"),
    CANCELED("Annuler"),
    RETOUR_EXPEDITEUR("Retour Expediteur"),
    PAID("Payée"),

    EXCHANGE("Echange"),
    INCORRECT_BARCODE("Code à barre incorrect"),
    DELETED("Supprimé"),
    OOS("En rupture"),
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
