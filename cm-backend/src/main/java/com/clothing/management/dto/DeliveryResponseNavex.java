package com.clothing.management.dto;

import com.clothing.management.repository.IGlobalConfRepository;
import net.minidev.json.annotate.JsonIgnore;
import org.springframework.beans.factory.annotation.Autowired;

public class DeliveryResponseNavex extends DeliveryResponse{

    int status;
    String lien;
    String status_message;
    String etat;
    @JsonIgnore
    int responseCode;
    @JsonIgnore
    String responseMessage;
    public DeliveryResponseNavex() {
    }

    public DeliveryResponseNavex(int status, String lien, String status_message, String etat, int responseCode, String responseMessage) {
        this.status = status;
        this.lien = lien;
        this.status_message = status_message;
        this.etat = etat;
        this.responseCode = responseCode;
        this.responseMessage = responseMessage;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getLien() {
        return lien;
    }

    public void setLien(String lien) {
        this.lien = lien;
    }

    public String getStatus_message() {
        return status_message;
    }

    public void setStatus_message(String status_message) {
        this.status_message = status_message;
    }

    public String getEtat() {
        return etat;
    }

    public void setEtat(String etat) {
        this.etat = etat;
    }

    public int getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(int responseCode) {
        this.responseCode = responseCode;
    }

    public String getResponseMessage() {
        return responseMessage;
    }

    public void setResponseMessage(String responseMessage) {
        this.responseMessage = responseMessage;
    }

    @Override
    public String toString() {
        return "DeliveryResponseNavex{" +
                "status='" + status + '\'' +
                ", lien='" + lien + '\'' +
                ", status_message='" + status_message + '\'' +
                ", etat='" + etat + '\'' +
                ", responseCode=" + responseCode +'\''+
                ", responseMessage='" + responseMessage + '\'' +
                '}';
    }
}
