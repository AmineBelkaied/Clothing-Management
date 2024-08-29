package com.clothing.management.dto.DayCount;

import com.clothing.management.entities.Color;
import com.clothing.management.entities.Size;

import java.util.Date;

public class ProductsDayCountDTO extends DayCountDTO {

    private Long id;
    private Long modelId;
    private String modelName;
    private Color color;
    private Size size;
    private long countExchange;
    private long countOos;

    private long countReturn;
    private double profits;

    public ProductsDayCountDTO() {
    }

    public ProductsDayCountDTO(
            Date packetDate, Long id,
            Long modelId, String modelName,
            Color color, Size size,
            long countPayed, long countProgress, long countOos, long countReturn, double profits
    ) {
        super(packetDate, countPayed, countProgress);
        this.id = id;
        this.modelId = modelId;
        this.modelName = modelName;
        this.color = color;
        this.size = size;
        this.countOos = countOos;
        this.countReturn = countReturn;
        this.profits = profits;
    }

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
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

    public long getCountReturn() {
        return countReturn;
    }

    public void setCountReturn(long countReturn) {
        this.countReturn = countReturn;
    }

    public double getProfits() {
        return profits;
    }

    public void setProfits(double profits) {
        this.profits = profits;
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
                ", id=" + id +
                ", modelId=" + modelId +
                ", modelName='" + modelName + '\'' +
                ", color=" + color +
                ", size=" + size +
                ", countExchange=" + countExchange +
                ", countOos=" + countOos +
                '}';
    }
}
