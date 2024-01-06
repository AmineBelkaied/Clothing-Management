package com.clothing.management.services;

import com.clothing.management.dto.GroupedCitiesDTO;
import com.clothing.management.dto.ProductsDayCountDTO;
import com.clothing.management.entities.City;
import com.clothing.management.entities.ModelStockHistory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface StatService {

    Map <String , List<?>> statModelSoldChart(Long modelId,String beginDate, String endDate);
    Map <String , List<?>> statAllModelsChart(String beginDate, String endDate);
    public Map<String , List<?>> statAllStockChart(String beginDate, String endDate);
    Map <String , List<?>> statAllOffersChart(String beginDate, String endDate);
    Map <String , List<?>> statAllColorsChart(String beginDate, String endDate,List<Long> lookForModelIds);
    Map <String , List<?>> statAllPacketsChart(String beginDate, String endDate);
    List<ProductsDayCountDTO> productsCountByDate(Long modelId, String beginDate, String endDate);
}
