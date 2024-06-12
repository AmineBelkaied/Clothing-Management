package com.clothing.management.dto;

import com.clothing.management.entities.Color;
import com.clothing.management.entities.Offer;
import com.clothing.management.entities.Size;

import java.util.Date;

public class ProductsDayCountDTO {

    private Date packetDate;
    private Long productId;

    //private String productRef;
    private Offer offer;
    private Long modelId;
    private String modelName;
    private Color color;
    private Size size;

    private Long countExchange;

    private Long countRupture;
    private Long countProgress;
    private Long count;

    public ProductsDayCountDTO() {
    }

    public ProductsDayCountDTO(
            Date packetDate, Long productId,
            Offer offer, Long modelId, String modelName,
            Color color, Size size,
            Long countExchange,Long countProgress, Long count,Long countRupture
    ) {
        this.packetDate = packetDate;
        this.productId = productId;
        //this.productRef = productRef;
        this.offer = offer;
        this.modelId = modelId;
        this.modelName = modelName;
        this.color = color;
        this.size = size;
        this.countExchange = countExchange;
        this.countProgress = countProgress;
        this.count = count;
        this.countRupture = countRupture;
    }

    public ProductsDayCountDTO(
            Date packetDate,
            Offer offer,
            Long count
    ) {
        this.packetDate = packetDate;
        this.productId = null;
        //this.productRef = productRef;
        this.offer = offer;
        this.modelId = null;
        this.modelName = null;
        this.color = null;
        this.size = null;
        this.countExchange = null;
        this.countRupture = null;
        this.countProgress = null;
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

    public Color getColor() {
        return color;
    }
    public void setColor(Color color) {
        this.color = color;
    }

    public Size getSize() {
        return size;
    }
    public void setSize(Size size) {
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

    public Long getCountRupture() {
        return countRupture;
    }
    public void setCountRupture(Long countRupture) {
        this.countRupture = countRupture;
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

    public Offer getOffer() {
        return offer;
    }

    public void setOffer(Offer offer) {
        this.offer = offer;
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

    @Override
    public String toString() {
        return "ProductsDayCountDTO{" +
                "packetDate=" + packetDate +
                ", productId=" + productId +
                ", offer=" + offer +
                ", modelName='" + modelName + '\'' +
                ", color='" + color + '\'' +
                ", size='" + size + '\'' +
                ", countExchange=" + countExchange +
                ", countRupture=" + countRupture +
                ", countProgress=" + countProgress +
                ", count=" + count +
                '}';
    }
}
