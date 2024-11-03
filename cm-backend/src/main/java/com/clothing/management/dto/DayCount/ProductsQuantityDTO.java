package com.clothing.management.dto.DayCount;

import com.clothing.management.entities.Product;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductsQuantityDTO{

    private Long id;
    private Long color;
    private Long size;
    private long qte = 0;
    public ProductsQuantityDTO(Product product) {
        this.id = product.getId();
        this.color = product.getColor().getId();
        this.size = product.getSize().getId();
        this.qte = product.getQuantity();
    }
}
