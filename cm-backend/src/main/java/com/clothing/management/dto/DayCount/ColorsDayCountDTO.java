package com.clothing.management.dto.DayCount;

import com.clothing.management.entities.Color;

import java.util.Date;

public class ColorsDayCountDTO extends DayCountDTO {
    private Color color;

    public ColorsDayCountDTO() {
    }

    public ColorsDayCountDTO(
            Date packetDate,
            Color color,
            long countPayed, long countProgress, long countReturn, double profits
    ) {
        super(packetDate, countPayed, countProgress, countReturn, profits);
        this.color = color;
    }

    public Color getColor() {
        return color;
    }
    public void setColor(Color color) {
        this.color = color;
    }

    @Override
    public String toString() {
        return "ProductsDayCountDTO{" +
                "color=" + color +
                '}';
    }
}
