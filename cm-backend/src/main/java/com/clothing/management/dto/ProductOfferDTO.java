package com.clothing.management.dto;

public class ProductOfferDTO {

    private Long productId;
    private Long offerId;
    private Long packetOfferIndex;

    private  double profits;

    public ProductOfferDTO(Long productId, Long offerId,Long packetOfferIndex,double profits) {
        this.productId = productId;
        this.offerId = offerId;
        this.packetOfferIndex = packetOfferIndex;
        this.profits = profits;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public Long getOfferId() {
        return offerId;
    }

    public void setOfferId(Long offerId) {
        this.offerId = offerId;
    }

    public Long getPacketOfferIndex() {
        return packetOfferIndex;
    }

    public void setPacketOfferIndex(Long packetOfferIndex) {
        this.packetOfferIndex = packetOfferIndex;
    }

    public double getProfits() {
        return profits;
    }

    public void setProfits(double profits) {
        this.profits = profits;
    }
}
