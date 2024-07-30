package com.clothing.management.dto;

import com.clothing.management.entities.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.util.Date;
import java.util.List;

public class PacketValidationDTO {
    private Long id;

    private String status;
    private String customerName;
    private String packetDescription;
    private String barcode;
    private String fbPageName;
    private String deliveryCompanyName;
    private double price;
    private Date date;
    private boolean valid;
    private boolean haveExchange;

    public PacketValidationDTO(){}

    public PacketValidationDTO(Packet packet){
        this.id = packet.getId();
        this.status = packet.getStatus();
        this.customerName = packet.getCustomerName();
        this.packetDescription = packet.getPacketDescription();
        this.barcode = packet.getBarcode();
        this.fbPageName = packet.getFbPage().getName();
        this.deliveryCompanyName = packet.getDeliveryCompany().getName();
        this.price = packet.getPrice()- packet.getDiscount();
        this.date = packet.getDate();
        this.valid = packet.isValid();
        this.haveExchange = packet.isHaveExchange();
    }

    public PacketValidationDTO(Long id, String status, String customerName, String packetDescription, String barcode, String fbPageName, String deliveryCompanyName, double price, Date date, boolean valid, boolean haveExchange) {
        this.id = id;
        this.status = status;
        this.customerName = customerName;
        this.packetDescription = packetDescription;
        this.barcode = barcode;
        this.fbPageName = fbPageName;
        this.deliveryCompanyName = deliveryCompanyName;
        this.price = price;
        this.date = date;
        this.valid = valid;
        this.haveExchange = haveExchange;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getPacketDescription() {
        return packetDescription;
    }

    public void setPacketDescription(String packetDescription) {
        this.packetDescription = packetDescription;
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public String getFbPageName() {
        return fbPageName;
    }

    public void setFbPageName(String fbPageName) {
        this.fbPageName = fbPageName;
    }

    public String getDeliveryCompanyName() {
        return deliveryCompanyName;
    }

    public void setDeliveryCompanyName(String deliveryCompanyName) {
        this.deliveryCompanyName = deliveryCompanyName;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public boolean isValid() {
        return valid;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }

    public boolean isHaveExchange() {
        return haveExchange;
    }

    public void setHaveExchange(boolean haveExchange) {
        this.haveExchange = haveExchange;
    }
}
