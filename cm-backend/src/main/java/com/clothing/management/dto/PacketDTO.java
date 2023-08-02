package com.clothing.management.dto;

import java.util.List;

public class PacketDTO {

    private double totalPrice;
    private double deliveryPrice;
    private double discount;
    private List<OfferUpdateDTO> offerUpdateDTOList;


    public PacketDTO() {
    }

    public PacketDTO(double totalPrice, double deliveryPrice, double discount, List<OfferUpdateDTO> offerUpdateDTOList) {
        this.totalPrice = totalPrice;
        this.deliveryPrice = deliveryPrice;
        this.discount = discount;
        this.offerUpdateDTOList = offerUpdateDTOList;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public double getDeliveryPrice() {
        return deliveryPrice;
    }

    public void setDeliveryPrice(double deliveryPrice) {
        this.deliveryPrice = deliveryPrice;
    }

    public double getDiscount() {
        return discount;
    }

    public void setDiscount(double discount) {
        this.discount = discount;
    }

    public List<OfferUpdateDTO> getOfferUpdateDTOList() {
        return offerUpdateDTOList;
    }

    public void setOfferUpdateDTOList(List<OfferUpdateDTO> offerUpdateDTOList) {
        this.offerUpdateDTOList = offerUpdateDTOList;
    }

    @Override
    public String toString() {
        return "PacketDTO{" +
                "totalPrice=" + totalPrice +
                ", deliveryPrice=" + deliveryPrice +
                ", discount=" + discount +
                ", offerUpdateDTOList=" + offerUpdateDTOList +
                '}';
    }
}
