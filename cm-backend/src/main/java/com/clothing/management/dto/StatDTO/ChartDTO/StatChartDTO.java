package com.clothing.management.dto.StatDTO.ChartDTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor

public class StatChartDTO {
    private List<Date> uniqueDates;
    private List<IDNameDTO> uniqueItems;
    private List<List<Long>> itemsCount;
}
