package com.clothing.management.dto;

import com.clothing.management.entities.Product;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductResponse {
    private Long id;
    private Long modelId;
    private Long sizeId;
    private Long colorId;
    private Long qte;

    public ProductResponse(Product product) {
        this.id = product.getId();
        this.modelId = product.getModel().getId();
        this.sizeId = product.getSize() != null ? product.getSize().getId() : null;
        this.colorId = product.getColor() != null ? product.getColor().getId() : null;
        this.qte = product.getQuantity();
    }
}
