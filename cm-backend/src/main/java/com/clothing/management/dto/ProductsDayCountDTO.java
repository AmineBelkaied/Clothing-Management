package com.clothing.management.dto;

import java.util.Date;

public class ProductsDayCountDTO {

    private Date packetDate;
    private Long productId;
    private Long modelId;
    private String color;
    private String size;
    private Long count;

    public ProductsDayCountDTO() {
    }

    public ProductsDayCountDTO(Date packetDate, Long productId, Long modelId, String color, String size, Long count) {
        this.packetDate = packetDate;
        this.productId = productId;
        this.modelId = modelId;
        this.color = color;
        this.size = size;
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

    public void setCount(Long count) {
        this.count = count;
    }
}
