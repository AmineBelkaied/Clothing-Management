package com.clothing.management.dto.DayCount;

import com.clothing.management.entities.Color;
import com.clothing.management.entities.Size;

import java.util.Date;

public class SoldProductsDayCountDTO extends DayCountDTO {

    private Long productId;

    private long countExchange;
    private long countOos;

    private Color color;
    private Size size;


    public SoldProductsDayCountDTO() {
    }

    public SoldProductsDayCountDTO(
            Date packetDate, Long productId,
            Color color, Size size,
            long countPayed, long countProgress, long countExchange, long countOos
    ) {
        super(packetDate, countPayed, countProgress);
        this.productId = productId;
        this.countExchange =countExchange;
        this.countOos = countOos;
        this.color = color;
        this.size = size;
    }

    public Long getProductId() {
        return productId;
    }
    public void setProductId(Long productId) {
        this.productId = productId;
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

    @Override
    public String toString() {
        return "ProductsDayCountDTO{" +
                ", productId=" + productId +
                ", color=" + color +
                ", size=" + size +
                '}';
    }
}
