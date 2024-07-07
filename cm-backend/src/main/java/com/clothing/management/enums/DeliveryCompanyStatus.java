package com.clothing.management.enums;

import com.clothing.management.models.JaxStatusModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;

public enum DeliveryCompanyStatus {

    WAITING("En attente", "En attente", Arrays.asList("En attente", "à enlever")),
    EN_COURS("En cours", "En cours", List.of("En cours de livraison")),
    LIVREE("Livré", "Livrer", List.of("Livré")),
    EXCHANGE("Echange", "Echange", List.of("Echange")),
    RETOUR_EXPEDITEUR("Retour Expéditeur", "Retour Expediteur", Arrays.asList("En cours de préparation au retour vers l'expéditeur", "Colis à retourner", "Retour expéditeur")),
    RETOUR_CLIENT_AGENCE("Rtn client/agence", "Rtn client/agence", List.of("Rtn client/agence")),
    AU_MAGASIN("Au magasin", "Au magasin", Arrays.asList("En cours de prépartion à l'expédition", "En cours de préparation au transfert vers une autre agence","Transfert a une autre agence en cours", "Reçu à l'entrepôt", "Enlevé")),
    RETOUR_DEPOT("Rtn dépôt", "Rtn dépôt", List.of("Retour entrepôt")),
    A_VERIFIER("A vérifier", "A verifier", List.of("A verifier")),
    RETOUR_RECU("Retour reçu", "Retour recu", List.of("Reçu par l'expediteur")),
    RETOUR_DEFINITIF("Rtn définitif", "Rtn definitif", List.of("Rtn definitif")),

    ANNULER("Annulée", "Annulée", List.of("Annulé")),
    OTHER_STATUS("Other Status", "Other Status", List.of("Other Status"));

    private String first;
    private String navex;
    private List<String> jax;

    DeliveryCompanyStatus(String first, String navex, List<String> jax) {
        this.first = first;
        this.navex = navex;
        this.jax = jax;
    }

    public String getFirst() {
        return first;
    }

    public void setFirst(String first) {
        this.first = first;
    }

    public String getNavex() {
        return navex;
    }

    public List<String> getJax() {
        return jax;
    }

    public void setNavex(String navex) {
        this.navex = navex;
    }

    public void setJax(List<String> jax) {
        this.jax = jax;
    }

    public static DeliveryCompanyStatus fromString(String status, DeliveryCompanyName deliveryCompanyName) {
        if (status != null) {
            for (DeliveryCompanyStatus deliveryCompanyStatus : DeliveryCompanyStatus.values()) {
                switch (deliveryCompanyName) {
                    case NAVEX:
                        if (status.equalsIgnoreCase(deliveryCompanyStatus.getNavex())) {
                            return deliveryCompanyStatus;
                        }
                        break;
                    case FIRST:
                        if (status.equalsIgnoreCase(deliveryCompanyStatus.getFirst())) {
                            return deliveryCompanyStatus;
                        }
                        break;
                    case JAX:
                        if (deliveryCompanyStatus.getJax().contains(status)) {
                            return deliveryCompanyStatus;
                        }
                        break;
                    // Add more cases here for other delivery companies if needed
                }
            }
        }
        return A_VERIFIER;
    }

    public static Object getStatus(DeliveryCompanyName deliveryCompanyName, DeliveryCompanyStatus status) {
        switch (deliveryCompanyName) {
            case NAVEX:
                return status.getNavex();
            case FIRST:
                return status.getFirst();
            case JAX:
                return status.getJax();
            // Add more cases here for other delivery companies if needed
        }
        return "Unknown delivery company";
    }
}
