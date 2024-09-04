package com.clothing.management.dto.DayCount;

import com.clothing.management.entities.Color;
import com.clothing.management.entities.Product;
import com.clothing.management.entities.Size;

public class SoldProductsDayCountDTO extends DayCountDTO {

    private Long id;

    private long countExchange;
    private long countOos;

    private Color color;
    private Size size;

    private long qte;
    public SoldProductsDayCountDTO() {
        this.qte = 0;
        this.countExchange = 0;
        this.countOos = 0;
    }


    public SoldProductsDayCountDTO(
            Long id,
            Color color, Size size, long qte,
            long countPayed, long countProgress, long countOos, long countExchange
    ) {
        super(countPayed, countProgress);
        this.id = id;
        this.countExchange =countExchange;
        this.countOos = countOos;
        this.color = color;
        this.size = size;
        this.qte = qte;
    }
    public SoldProductsDayCountDTO(Product product) {
        super(0, 0);
        this.id = product.getId();
        this.countExchange =0;
        this.countOos = 0;
        this.color = product.getColor();
        this.size = product.getSize();
        this.qte = product.getQuantity();
    }

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
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

    public long getQte() {
        return qte;
    }

    public void setQte(long qte) {
        this.qte = qte;
    }

    @Override
    public String toString() {
        return "SoldProductsDayCountDTO{" +
                "id=" + id +
                ", color=" + color +
                ", size=" + size +
                '}';
    }
}
