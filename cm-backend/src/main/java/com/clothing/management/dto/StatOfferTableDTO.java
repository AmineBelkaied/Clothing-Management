package com.clothing.management.dto;

import com.clothing.management.entities.Offer;

public class StatOfferTableDTO extends StatTableDTO{

    private Offer offer;


    private Double purchasePrice;
    private Double sellingPrice;

    public StatOfferTableDTO(Offer offer, Integer min, Integer max, Long avg, Long payed, Long progress, Long retour, Offer offer1, Double purchasePrice, Double sellingPrice) {
        super(offer.getName(), min, max, avg, payed, progress, retour);
        this.offer = offer1;
        this.purchasePrice = purchasePrice;
        this.sellingPrice = sellingPrice;
    }

    public StatOfferTableDTO(Offer offer) {
        super(offer.getName());
        this.offer = offer;
        this.purchasePrice = 0.0;
        this.sellingPrice = 0.0;
    }

    public Offer getOffer() {
        return offer;
    }

    public void setOffer(Offer offer) {
        this.offer = offer;
    }

    public Double getPurchasePrice() {
        return purchasePrice;
    }

    public void setPurchasePrice(Double purchasePrice) {
        this.purchasePrice = purchasePrice;
    }

    public Double getSellingPrice() {
        return sellingPrice;
    }

    public void setSellingPrice(Double sellingPrice) {
        this.sellingPrice = sellingPrice;
    }

    @Override
    public String toString() {
        return "StatOfferTableDTO{" +
                "offer=" + offer +
                ", purchasePrice=" + purchasePrice +
                ", sellingPrice=" + sellingPrice +
                '}';
    }
}
