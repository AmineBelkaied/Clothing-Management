package com.clothing.management.dto;

import java.util.Date;

public class ProductsDayCountDTO {

    private Date packetDate;
    private Long productId;

    //private String productRef;
    private Long offerId;
    private Long modelId;
    private String modelName;
    private String color;
    private String size;

    private Long countExchange;
    private Long countProgress;
    private Long count;

    public ProductsDayCountDTO() {
    }

    public ProductsDayCountDTO(
            Date packetDate, Long productId,
            Long offerId, Long modelId, String modelName,
            String color, String size,
            Long countExchange,Long countProgress, Long count
    ) {
        this.packetDate = packetDate;
        this.productId = productId;
        //this.productRef = productRef;
        this.offerId = offerId;
        this.modelId = modelId;
        this.modelName = modelName;
        this.color = color;
        this.size = size;
        this.countExchange = countExchange;
        this.countProgress = countProgress;
        this.count = count;
    }

    public Date getPacketDate() {
        return packetDate;
    }

    public void setPacketDate(Date packetDate) {
        this.packetDate = packetDate;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public Long getModelId() {
        return modelId;
    }

    public void setModelId(Long modelId) {
        this.modelId = modelId;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public Long getCount() {
        return count;
    }

    public Long getCountExchange() {
        return countExchange;
    }

    public void setCountExchange(Long countExchange) {
        this.countExchange = countExchange;
    }

    public Long getCountProgress() {
        return countProgress;
    }
    public void setCountProgress(Long countProgress) {
        this.countProgress = countProgress;
    }
    public void setCount(Long count) {
        this.count = count;
    }
    public Long getOfferId() {
        return offerId;
    }
    public void setOfferId(Long offerId) {
        this.offerId = offerId;
    }

    /*public String getProductRef() {
        return productRef;
    }
    public void setProductRef(String productRef) {
        this.productRef = productRef;
    }*/
    public String getModelName() {
        return modelName;
    }
    public void setModelName(String modelName) {
        this.modelName = modelName;
    }
}
