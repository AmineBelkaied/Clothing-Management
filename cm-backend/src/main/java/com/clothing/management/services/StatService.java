package com.clothing.management.services;
import com.clothing.management.dto.DayCount.PagesStatCountDTO;
import com.clothing.management.dto.DayCount.StatesStatCountDTO;
import com.clothing.management.dto.DayCount.ProductsDayCountDTO;
import com.clothing.management.dto.DayCount.SoldProductsDayCountDTO;
import com.clothing.management.entities.Color;

import java.util.List;
import java.util.Map;

public interface StatService {

    Map<String, List<?>> statAllPacketsChart(String beginDate, String endDate, String deliveryCompanyName);
    Map <String , List<?>> statModelSoldChart(Long modelId, String beginDate, String endDate);
    Map <String , List<?>> statAllModelsChart(String beginDate, String endDate,Boolean countProgress);
    Map<String , List<?>> statAllStockChart(String beginDate, String endDate);
    Map <String , List<?>> statAllOffersChart(String beginDate, String endDate);
    Map <String , List<?>> statAllColorsChart(String beginDate, String endDate);//,List<Long> lookForModelIds);
    List<ProductsDayCountDTO> productsCountByDate(Long modelId, String beginDate, String endDate);
    List<PagesStatCountDTO> findAllPacketsPages(String beginDate, String endDate);
    List<StatesStatCountDTO> findAllPacketsStates(String beginDate, String endDate);
}
