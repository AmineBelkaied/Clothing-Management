package com.clothing.management.entities;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;
import lombok.Value;
import net.minidev.json.annotate.JsonIgnore;

import java.util.*;

@Builder
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

    @Column(name = "date")
    @Builder.Default
    private Date date = new Date();

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

    public ModelStockHistory(Date date, Model model, Long quantity) {
        this.date = date;
        this.model= model;
        this.quantity = quantity;
    }

    public ModelStockHistory(Model model, Long quantity) {
        date = new Date();
        this.model = model;
        this.quantity = quantity;
    }

}
