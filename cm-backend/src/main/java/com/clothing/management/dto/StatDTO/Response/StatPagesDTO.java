package com.clothing.management.dto.StatDTO.Response;
import com.clothing.management.dto.StatDTO.ChartDTO.StatChartDTO;
import com.clothing.management.dto.StatDTO.TableDTO.PageTableDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor

public class StatPagesDTO {
    private StatChartDTO chart;
    private List<PageTableDTO> pagesStat;
}
