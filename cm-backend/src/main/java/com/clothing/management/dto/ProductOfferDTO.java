package com.clothing.management.dto;

public class ProductOfferDTO {

    private Long productId;
    private Long offerId;
    private Integer packetOfferIndex;

    public ProductOfferDTO(Long productId, Long offerId,Integer packetOfferIndex) {
        this.productId = productId;
        this.offerId = offerId;
        this.packetOfferIndex = packetOfferIndex;
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

    public Integer getPacketOfferIndex() {
        return packetOfferIndex;
    }

    public void setPacketOfferIndex(Integer packetOfferIndex) {
        this.packetOfferIndex = packetOfferIndex;
    }
}
