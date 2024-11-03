package com.clothing.management.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;
import net.minidev.json.annotate.JsonIgnore;

import java.util.*;

@Entity
@Table(name = "model_stock_history", indexes = {
        @Index(name = "idx_model_id", columnList = "model_id")
})
@Data
@ToString(exclude = {"model"})
public class ModelStockHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Date date;

    @JsonIgnore
    @JoinColumn(name = "model_id")
    @ManyToOne
    private Model model;

    private Long quantity;

    public ModelStockHistory() {
    }


    public ModelStockHistory(Long id,Date date,Model model, Long quantity) {
        this.id = id;
        this.date = date;
        this.model= model;
        this.quantity = quantity;
    }

    public ModelStockHistory(Model model, Long quantity) {
        this.model = model;
        this.quantity = quantity;
    }

}
