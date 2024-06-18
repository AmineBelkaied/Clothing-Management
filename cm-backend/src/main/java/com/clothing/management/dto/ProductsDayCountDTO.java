package com.clothing.management.dto;

import com.clothing.management.entities.Color;
import com.clothing.management.entities.Offer;
import com.clothing.management.entities.Size;
import org.springframework.boot.context.properties.bind.DefaultValue;

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

    private long countExchange;
    private long countOos;
    private long countProgress;
    private long count;

    public ProductsDayCountDTO() {
    }

    public ProductsDayCountDTO(
            Date packetDate, Long productId,
            Offer offer, Long modelId, String modelName,
            Color color, Size size, long count
    ) {
        this.packetDate = packetDate;
        this.productId = productId;
        this.offer = offer;
        this.modelId = modelId;
        this.modelName = modelName;
        this.color = color;
        this.size = size;
        this.count = count;
    }

    public ProductsDayCountDTO(
            Date packetDate,
            Offer offer,
            long count
    ) {
        this.packetDate = packetDate;
        this.productId = null;
        //this.productRef = productRef;
        this.offer = offer;
        this.modelId = null;
        this.modelName = null;
        this.color = null;
        this.size = null;
        this.countExchange = 0;
        this.countOos = 0;
        this.countProgress = 0;
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

    public long getCount() {
        return count;
    }

    public long getCountExchange() {
        return countExchange;
    }
    public void setCountExchange(long countExchange) {
        this.countExchange = countExchange;
    }

    public long getCountOos() {
        return countOos;
    }
    public void setCountOos(long countOos) {
        this.countOos = countOos;
    }

    public long getCountProgress() {
        return countProgress;
    }
    public void setCountProgress(long countProgress) {
        this.countProgress = countProgress;
    }
    public void setCount(long count) {
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
                ", countRupture=" + countOos +
                ", countProgress=" + countProgress +
                ", count=" + count +
                '}';
    }
}
