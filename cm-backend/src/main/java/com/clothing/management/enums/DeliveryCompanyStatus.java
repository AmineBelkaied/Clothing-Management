
package com.clothing.management.enums;

    public enum DeliveryCompanyStatus {

        WAITING("En attente"),

        EN_COURS("En cours"),
        LIVREE("Livré"),
        LIVRER("Livrer"),

        EXCHANGE("Echange"),
        RETOUR_EXPEDITEUR("Retour Expéditeur"),
        RETOUR_EXPEDITEUR_NAVEX("Retour Expediteur"),
        RETOUR_CLIENT_AGENCE("Rtn client/agence"),
        AU_MAGASIN("Au magasin"),

        RETOUR_DEPOT("Rtn dépôt"),
        RETOUR_DEPOT_NAVEX("Rtn dépôt"),

        A_VERIFIER("A vérifier"),
        A_VERIFIER_NAVEX("A verifier"),

        RETOUR_RECU("Retour reçu"),
        RETOUR_RECU_NAVEX("Retour recu"),
        RETOUR_DEFINITIF("Rtn définitif"),
        RETOUR_DEFINITIF_NAVEX("Rtn definitif"),
        OTHER_STATUS();

        private String status;

        DeliveryCompanyStatus(String status) {
            this.status = status;
        }

        DeliveryCompanyStatus() {
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        // From the String method, it will return you the Enum for the provided input string
        public static DeliveryCompanyStatus fromString(String status) {
            if (status != null) {
                for (DeliveryCompanyStatus deliveryCompanyStatus : DeliveryCompanyStatus.values()) {
                    if (status.equalsIgnoreCase(deliveryCompanyStatus.status)) {
                        return deliveryCompanyStatus;
                    }
                }
            }
            return OTHER_STATUS;
        }
    }
