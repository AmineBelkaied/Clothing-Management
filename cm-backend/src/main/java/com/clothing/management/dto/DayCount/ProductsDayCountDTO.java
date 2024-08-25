package com.clothing.management.dto.DayCount;

import com.clothing.management.dto.DeliveryCompanyDTOs.DeliveryResponse;
import com.clothing.management.dto.OfferDTO;
import com.clothing.management.entities.Color;
import com.clothing.management.entities.Offer;
import com.clothing.management.entities.Size;

import java.util.Date;

public class ProductsDayCountDTO extends DayCountDTO {

    private Long productId;
    private Long modelId;
    private String modelName;
    private Color color;
    private Size size;
    private long countExchange;
    private long countOos;

    public ProductsDayCountDTO() {
    }

    public ProductsDayCountDTO(
            Date packetDate, Long productId,
            Long modelId, String modelName,
            Color color, Size size,
            long countPayed, long countProgress, long countReturn, double profits
    ) {
        super(packetDate, countPayed, countProgress, countReturn, profits);
        this.productId = productId;
        this.modelId = modelId;
        this.modelName = modelName;
        this.color = color;
        this.size = size;
    }

    public ProductsDayCountDTO(
            Date packetDate, Long productId,
            Long modelId, String modelName,
            Color color, Size size,
            long countPayed, long countProgress, long countOos, long countReturn, double profits
    ) {
        super(packetDate, countPayed, countProgress, countReturn, profits);
        this.productId = productId;
        this.modelId = modelId;
        this.modelName = modelName;
        this.color = color;
        this.size = size;
        this.countOos = countOos;
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


    public String getModelName() {
        return modelName;
    }
    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    @Override
    public String toString() {
        return "ProductsDayCountDTO{" +
                ", productId=" + productId +
                ", modelId=" + modelId +
                ", modelName='" + modelName + '\'' +
                ", color=" + color +
                ", size=" + size +
                ", countExchange=" + countExchange +
                ", countOos=" + countOos +
                '}';
    }
}
