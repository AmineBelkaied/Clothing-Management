package com.clothing.management.dto;

import com.clothing.management.dto.StatDTO.SoldProductsDayCountDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StockDTO {

    ModelDTO model;
    HashMap<Long, HashMap<Long, SoldProductsDayCountDTO>> productsByColor;
}
