

package com.clothing.management.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import net.minidev.json.annotate.JsonIgnore;



public class DeliveryResponseJax extends DeliveryResponse {

    @JsonProperty("statut")
    private String state;

    @JsonProperty("updated_at")
    private String updatedAt;

    @JsonProperty("code")
    private String barCode;
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

    public DeliveryResponseJax() {
        super();
    }

    @Override
    public String getState() {
        return state;
    }

    @Override
    public void setState(String state) {
        this.state = state;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public String getBarCode() {
        return barCode;
    }

    @Override
    public void setBarCode(String barCode) {
        this.barCode = barCode;
    }

    public String getReferenceExterne() {
        return referenceExterne;
    }

    public void setReferenceExterne(String referenceExterne) {
        this.referenceExterne = referenceExterne;
    }

    public int getDepotId() {
        return depotId;
    }

    public void setDepotId(int depotId) {
        this.depotId = depotId;
    }

    public String getNomContact() {
        return nomContact;
    }

    public void setNomContact(String nomContact) {
        this.nomContact = nomContact;
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public String getTel2() {
        return tel2;
    }

    public void setTel2(String tel2) {
        this.tel2 = tel2;
    }

    public String getAdresseLivraison() {
        return adresseLivraison;
    }

    public void setAdresseLivraison(String adresseLivraison) {
        this.adresseLivraison = adresseLivraison;
    }

    public int getGovernorat() {
        return governorat;
    }

    public void setGovernorat(int governorat) {
        this.governorat = governorat;
    }

    public int getStateId() {
        return stateId;
    }

    @Override
    public String toString() {
        return "DeliveryResponseJax{" +
                "statut='" + state + '\'' +
                ", updatedAt='" + updatedAt + '\'' +
                ", barCode='" + barCode + '\'' +
                ", referenceExterne='" + referenceExterne + '\'' +
                ", depotId=" + depotId +
                ", nomContact='" + nomContact + '\'' +
                ", tel='" + tel + '\'' +
                ", tel2='" + tel2 + '\'' +
                ", adresseLivraison='" + adresseLivraison + '\'' +
                ", governorat=" + governorat +
                ", stateId=" + stateId +
                ", description='" + description + '\'' +
                ", cod=" + cod +
                ", clientId=" + clientId +
                ", echange=" + echange +
                ", userId=" + userId +
                ", ancienCod=" + ancienCod +
                ", createdBy='" + createdBy + '\'' +
                ", enCheque=" + enCheque +
                ", numCheque=" + numCheque +
                ", cash=" + cash +
                ", delegation='" + delegation + '\'' +
                ", commentaire='" + commentaire + '\'' +
                ", createdAt='" + createdAt + '\'' +
                ", id=" + id +
                ", responseCode=" + responseCode +
                ", responseMessage='" + responseMessage + '\'' +
                '}';
    }

    public void setStateId(int stateId) {
        this.stateId = stateId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getCod() {
        return cod;
    }

    public void setCod(int cod) {
        this.cod = cod;
    }

    public int getClientId() {
        return clientId;
    }

    public void setClientId(int clientId) {
        this.clientId = clientId;
    }

    public int getEchange() {
        return echange;
    }

    public void setEchange(int echange) {
        this.echange = echange;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getAncienCod() {
        return ancienCod;
    }

    public void setAncienCod(int ancienCod) {
        this.ancienCod = ancienCod;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public int getEnCheque() {
        return enCheque;
    }

    public void setEnCheque(int enCheque) {
        this.enCheque = enCheque;
    }

    public int getNumCheque() {
        return numCheque;
    }

    public void setNumCheque(int numCheque) {
        this.numCheque = numCheque;
    }

    public int getCash() {
        return cash;
    }

    public void setCash(int cash) {
        this.cash = cash;
    }

    public String getDelegation() {
        return delegation;
    }

    public void setDelegation(String delegation) {
        this.delegation = delegation;
    }

    public String getCommentaire() {
        return commentaire;
    }

    public void setCommentaire(String commentaire) {
        this.commentaire = commentaire;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }


}














