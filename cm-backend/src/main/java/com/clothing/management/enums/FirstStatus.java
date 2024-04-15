package com.clothing.management.enums;

import java.util.Arrays;
import java.util.Optional;

public enum FirstStatus {
    EN_ATTENTE("En attente"),

    EN_COURS("En cours"),
    LIVREE("Livré"),

    LIVRER("Livrer"),

    EXCHANGE("Echange"),
    RETOUR_EXPEDITEUR("Retour Expéditeur"),
    RETOUR_CLIENT_AGENCE("Rtn client/agence"),
    AU_MAGASIN("Au magasin"),

    RETOUR_DEPOT("Rtn dépôt"),
    A_VERIFIER("A vérifier"),

    RETOUR_RECU("Retour reçu"),
    RETOUR_DEFINITIF("Rtn définitif"),
    OTHER_STATUS();

    private String status;

    FirstStatus(String status) {
        this.status = status;
    }

    FirstStatus() {
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    // From the String method, it will return you the Enum for the provided input string
    public static FirstStatus fromString(String status) {
        if (status != null) {
            for (FirstStatus firstStatus : FirstStatus.values()) {
                if (status.equalsIgnoreCase(firstStatus.status)) {
                    return firstStatus;
                }
            }
        }
        return OTHER_STATUS;
    }
}
