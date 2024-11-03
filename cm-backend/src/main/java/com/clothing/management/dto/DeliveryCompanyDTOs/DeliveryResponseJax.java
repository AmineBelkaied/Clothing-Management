

package com.clothing.management.dto.DeliveryCompanyDTOs;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import lombok.experimental.SuperBuilder;
import net.minidev.json.annotate.JsonIgnore;


@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@EqualsAndHashCode(callSuper = false)
public class DeliveryResponseJax extends DeliveryResponse {

    @JsonProperty("statut")
    private String state;

    @JsonProperty("updated_at")
    private String updatedAt;

    @JsonProperty("code")
    private String barcode;
    @JsonProperty("referenceExterne")
    private String referenceExterne;
    @JsonProperty("depot_id")
    private int depotId;
    @JsonProperty("nomContact")
    private String nomContact;
    @JsonProperty("tel")
    private String tel;
    @JsonProperty("tel2")
    private String tel2;
    @JsonProperty("adresseLivraison")
    private String adresseLivraison;
    @JsonProperty("governorat")
    private int governorat;
    @JsonProperty("statut_id")
    private int stateId;
    @JsonProperty("description")
    private String description;
    @JsonProperty("cod")
    private int cod;
    @JsonProperty("client_id")
    private int clientId;
    @JsonProperty("echange")
    private int echange;
    @JsonProperty("user_id")
    private int userId;
    @JsonProperty("ancien_cod")
    private int ancienCod;
    @JsonProperty("created_by")
    private String createdBy;
    @JsonProperty("enCheque")
    private int enCheque;
    @JsonProperty("numCheque")
    private int numCheque;
    @JsonProperty("cash")
    private int cash;
    @JsonProperty("delegation")
    private String delegation;
    @JsonProperty("commentaire")
    private String commentaire;
    @JsonProperty("created_at")
    private String createdAt;
    @JsonProperty("id")
    private int id;

    @JsonIgnore
    public int responseCode;
    @JsonIgnore
    public String responseMessage;
}














