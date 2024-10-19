package com.clothing.management.dto;

import com.clothing.management.dto.DayCount.SoldProductsDayCountDTO;
import com.clothing.management.entities.Color;
import com.clothing.management.entities.Model;
import com.clothing.management.entities.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.List;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StockDTO {

    ModelDTO model;
    HashMap<Long, HashMap<Long, SoldProductsDayCountDTO>> productsByColor;
}
