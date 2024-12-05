package com.clothing.management.services;
import com.clothing.management.dto.StatDTO.StatesStatCountDTO;
import com.clothing.management.dto.StatDTO.ProductsDayCountDTO;
import com.clothing.management.dto.StatModelsDTO;
import com.clothing.management.dto.StatOffersDTO;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

public interface StatService {

    Map<String, List<?>> statAllPacketsChart(String beginDate, String endDate, String deliveryCompanyName);
    Map<String, List<?>> statAllPagesChart(String beginDate, String endDate);
    Map <String , List<?>> statModelSoldChart(Long modelId, String beginDate, String endDate);
    StatModelsDTO statModels(String beginDate, String endDate, Boolean countProgress);
    Map<String , List<?>> statStock(String beginDate, String endDate);
    @Transactional("tenantTransactionManager")
    StatOffersDTO statOffers(String beginDate, String endDate, Boolean countProgressEnabler);

    Map <String , List<?>> statAllColorsChart(String beginDate, String endDate);//,List<Long> lookForModelIds);
    List<ProductsDayCountDTO> productsCountByDate(Long modelId, String beginDate, String endDate);
    List<StatesStatCountDTO> findAllPacketsStates(String beginDate, String endDate);
}
