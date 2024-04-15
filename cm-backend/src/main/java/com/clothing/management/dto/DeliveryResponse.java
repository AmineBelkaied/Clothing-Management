package com.clothing.management.dto;

import net.minidev.json.annotate.JsonIgnore;

public class DeliveryResponse {
    @JsonIgnore
    public int responseCode;
    @JsonIgnore
    public String responseMessage;

    public int status;

    public String link;

    public String state;

    public String barCode;

    public Boolean isError;

    public String message;
    public DeliveryResponseFirst.Result result;

    public DeliveryResponse() {
    }

    public DeliveryResponse (int status, Boolean isError, String message, String link, String state, String barCode) {
        this.status = status;
        this.link = link;
        this.state = state;
        this.barCode = barCode;
        this.isError = isError;
        this.message = message;
    }

    public DeliveryResponse (int status, Boolean isError, String message, DeliveryResponseFirst.Result result) {
        this.status = status;
        this.link = result.getLink();
        this.state = result.getState();
        this.barCode = result.getBarCode();
        this.isError = isError;
        this.message = message;
    }

    public DeliveryResponse(DeliveryResponseFirst deliveryResponseFirst) {
        this.status = deliveryResponseFirst.getStatus();
        this.link = deliveryResponseFirst.getResult().getLink();
        this.state = deliveryResponseFirst.getResult().getState();
        this.barCode = deliveryResponseFirst.getResult().getBarCode();
        this.isError = deliveryResponseFirst.isError();
        this.message = deliveryResponseFirst.getMessage();
        this.responseCode = deliveryResponseFirst.getResponseCode();
        this.responseMessage = deliveryResponseFirst.getResponseMessage();
    }

    public DeliveryResponse(DeliveryResponseNavex deliveryResponse) {
        this.status = deliveryResponse.getResponseCode();
        this.link = deliveryResponse.getLien();
        this.state = deliveryResponse.getEtat();
        this.barCode = deliveryResponse.getStatus_message();
        this.isError = false;
        this.message = deliveryResponse.getStatus_message();
        this.responseCode = deliveryResponse.getResponseCode();
        this.responseMessage = deliveryResponse.getResponseMessage();
    }

    public String getBarCode() {
        return barCode;
    }

    public void setBarCode(String barCode) {
        this.barCode = barCode;
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

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public Boolean getError() {
        return isError;
    }

    public void setError(Boolean error) {
        isError = error;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public DeliveryResponseFirst.Result getResult() {
        return result;
    }

    public void setResult(DeliveryResponseFirst.Result result) {
        this.result = result;
    }

    @Override
    public String toString() {
        return "DeliveryResponse{" +
                "responseCode=" + responseCode + '\'' +
                ", responseMessage='" + responseMessage + '\'' +
                ", status=" + status +
                ", link='" + link + '\'' +
                ", state='" + state + '\'' +
                ", barCode='" + barCode + '\'' +
                ", isError=" + isError + '\'' +
                ", message='" + message + '\'' +
                ", result=" + result + '\'' +
                '}';
    }
}