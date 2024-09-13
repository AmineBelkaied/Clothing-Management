package com.clothing.management.dto;

import com.clothing.management.entities.DeliveryCompany;

public class DeliveryCompanyDTO {

    private String name;
    private String barCodeUrl;
    public DeliveryCompanyDTO() {
    }

    public DeliveryCompanyDTO(DeliveryCompany deliveryCompany) {
        this.name = deliveryCompany.getName();
        this.barCodeUrl = deliveryCompany.getBarreCodeUrl();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBarCodeUrl() {
        return barCodeUrl;
    }

    public void setBarCodeUrl(String barCodeUrl) {
        this.barCodeUrl = barCodeUrl;
    }
}
