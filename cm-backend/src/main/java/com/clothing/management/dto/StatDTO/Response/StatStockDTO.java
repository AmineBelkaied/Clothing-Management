package com.clothing.management.dto.StatDTO.Response;

import com.clothing.management.dto.ModelStockValueDTO;
import com.clothing.management.dto.StatDTO.ChartDTO.StatChartDTO;
import com.clothing.management.dto.StatDTO.TableDTO.OfferTableDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor

public class StatStockDTO {
    private StatChartDTO chart;
    private ArrayList<ModelStockValueDTO> stockTable;
}
