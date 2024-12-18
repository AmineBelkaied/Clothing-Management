package com.clothing.management.dto.StatDTO.Response;
import com.clothing.management.dto.ModelStockValueDTO;
import com.clothing.management.dto.StatDTO.ChartDTO.StatChartDTO;
import com.clothing.management.dto.StatDTO.TableDTO.ModelTableDTO;
import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor

public class StatModelsDTO {
    private StatChartDTO chart;
    private List<ModelTableDTO> modelsStat;
    private List<ModelStockValueDTO> statValuesDashboard;
}
