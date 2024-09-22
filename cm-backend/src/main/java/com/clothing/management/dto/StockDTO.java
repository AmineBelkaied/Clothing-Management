package com.clothing.management.dto;

import com.clothing.management.dto.DayCount.SoldProductsDayCountDTO;
import com.clothing.management.entities.Model;
import com.clothing.management.entities.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StockDTO {

    Model model;
    List<List<SoldProductsDayCountDTO>> productsByColor;
    List<Size> sizes;
}
