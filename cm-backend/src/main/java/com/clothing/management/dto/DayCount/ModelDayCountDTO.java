package com.clothing.management.dto.DayCount;

import com.clothing.management.entities.Color;
import com.clothing.management.entities.Size;

import java.util.Date;

public class ModelDayCountDTO extends DayCountDTO {

    private Long productId;
    private Color color;
    private Size size;

    public ModelDayCountDTO() {
    }

    public ModelDayCountDTO(
            Date packetDate, Long productId,
            Color color, Size size,
            long countPayed, long countProgress, long countReturn
    ) {
        super(packetDate, countPayed, countProgress, countReturn);
        this.productId = productId;
        this.color = color;
        this.size = size;
    }

    public Long getProductId() {
        return productId;
    }
    public void setProductId(Long productId) {
        this.productId = productId;
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
