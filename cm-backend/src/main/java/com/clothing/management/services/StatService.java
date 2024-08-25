package com.clothing.management.services;
import com.clothing.management.dto.DayCount.DayCountDTO;
import com.clothing.management.dto.DayCount.ProductsDayCountDTO;
import com.clothing.management.dto.DayCount.SoldProductsDayCountDTO;

import java.util.List;
import java.util.Map;

public interface StatService {

    Map<String, List<?>> statAllPacketsChart(String beginDate, String endDate, String deliveryCompanyName);
    Map <String , List<?>> statModelSoldChart(Long modelId, String beginDate, String endDate);
    Map <String , List<?>> statAllModelsChart(String beginDate, String endDate,Boolean countProgress);
    Map<String , List<?>> statAllStockChart(String beginDate, String endDate);
    Map <String , List<?>> statAllOffersChart(String beginDate, String endDate);
    Map <String , List<?>> statAllColorsChart(String beginDate, String endDate,List<Long> lookForModelIds);
    //Map <String , List<?>> statAllPacketsChart(String beginDate, String endDate);
    List<ProductsDayCountDTO> productsCountByDate(Long modelId, String beginDate, String endDate);

    List<SoldProductsDayCountDTO> soldProductsCountByDate(Long modelId, String beginDate, String endDate);
}
