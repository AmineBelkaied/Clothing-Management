package com.clothing.management.dto;

import com.clothing.management.entities.Color;
import com.clothing.management.entities.Offer;
import com.clothing.management.entities.Size;
import org.springframework.boot.context.properties.bind.DefaultValue;

import java.util.Date;

public class ProductsDayCountDTO {

    private Date packetDate;
    private Long productId;
    private Offer offer;
    private Long modelId;
    private String modelName;
    private Color color;
    private Size size;
    private long countExchange;
    private long countOos;
    private long countProgress;
    private long countPayed;
    private long countReturn;

    public ProductsDayCountDTO() {
    }

    /*public ProductsDayCountDTO(
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
        this.countPayed = count;
    }*/
    public ProductsDayCountDTO(
            Date packetDate, Long productId,
            Offer offer, Long modelId, String modelName,
            Color color, Size size,
            long countPayed, long countProgress, long countReturn
    ) {
        this.packetDate = packetDate;
        this.productId = productId;
        this.offer = offer;
        this.modelId = modelId;
        this.modelName = modelName;
        this.color = color;
        this.size = size;
        this.countPayed = countPayed;
        this.countProgress = countProgress;
        this.countReturn = countReturn;
    }

    public ProductsDayCountDTO(
            Date packetDate, Long productId,
            Offer offer, Long modelId, String modelName,
            Color color, Size size,
            long countPayed, long countProgress, long countOos, long countReturn
    ) {
        this.packetDate = packetDate;
        this.productId = productId;
        this.offer = offer;
        this.modelId = modelId;
        this.modelName = modelName;
        this.color = color;
        this.size = size;
        this.countPayed = countPayed;
        this.countProgress = countProgress;
        this.countOos = countOos;
        this.countReturn = countReturn;
    }

    public ProductsDayCountDTO(
            Date packetDate,
            Offer offer,
            long countPayed, long countProgress, long countReturn
    ) {
        this.packetDate = packetDate;
        this.offer = offer;
        this.countExchange = 0;
        this.countOos = 0;
        this.countPayed = countPayed;
        this.countProgress = countProgress;
        this.countReturn = countReturn;
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

    public Offer getOffer() {
        return offer;
    }

    public void setOffer(Offer offer) {
        this.offer = offer;
    }

    public String getModelName() {
        return modelName;
    }
    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    public long getCountPayed() {
        return countPayed;
    }

    public void setCountPayed(long countPayed) {
        this.countPayed = countPayed;
    }

    public long getCountReturn() {
        return countReturn;
    }

    public void setCountReturn(long countReturn) {
        this.countReturn = countReturn;
    }


    @Override
    public String toString() {
        return "ProductsDayCountDTO{" +
                "packetDate=" + packetDate +
                ", productId=" + productId +
                ", offer=" + offer +
                ", modelId=" + modelId +
                ", modelName='" + modelName + '\'' +
                ", color=" + color +
                ", size=" + size +
                ", countExchange=" + countExchange +
                ", countOos=" + countOos +
                ", countPayed=" + countPayed +
                ", countProgress=" + countProgress +
                ", countReturn=" + countReturn +
                '}';
    }
}
