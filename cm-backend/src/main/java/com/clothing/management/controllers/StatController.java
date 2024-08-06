package com.clothing.management.controllers;
import com.clothing.management.dto.ProductsDayCountDTO;
import com.clothing.management.services.StatService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("stat")
@CrossOrigin
public class StatController {
    private final StatService statService;
    public StatController(StatService statService){
        this.statService = statService;
    }

    @GetMapping(path = "/statModelSold/{modelId}")
    public Map<String , List<?>> statModelSold(
            @PathVariable Long modelId ,
            @RequestParam String beginDate,
            @RequestParam String endDate) {
        return statService.statModelSoldChart(modelId,beginDate,endDate);

    }

    @GetMapping(path = "/statAllModels")
    public Map <String , List<?>> statAllModels(
            @RequestParam String beginDate,
            @RequestParam String endDate,
            @RequestParam Boolean countProgress) {
        return statService.statAllModelsChart(beginDate,endDate,countProgress);
    }

    @GetMapping(path = "/statStock")
    public Map <String , List<?>> statAStock(
            @RequestParam String beginDate,
            @RequestParam String endDate) {
        return statService.statAllStockChart(beginDate,endDate);
    }

    @GetMapping(path = "/statAllColors")
    public Map <String , List<?>> statAllColors(
            @RequestParam String beginDate,
            @RequestParam String endDate,
            @RequestParam List<Long> modelIds) {
        return statService.statAllColorsChart(beginDate,endDate,modelIds);
    }

    @GetMapping(path = "/statAllPackets")
    public Map <String , List<?>> statAllPackets(
            @RequestParam String beginDate,
            @RequestParam String endDate,
            @RequestParam(required = false) String deliveryCompanyName) {
        return statService.statAllPacketsChart(beginDate,endDate,deliveryCompanyName);
    }
    @GetMapping(path = "/statAllOffers")
    public Map <String , List<?>> statAllOffers(
            @RequestParam String beginDate,
            @RequestParam String endDate) {
        return statService.statAllOffersChart(beginDate,endDate);
    }

    @GetMapping(path = "/productsCount/{modelId}")
    public List<ProductsDayCountDTO> productsCount(
            @PathVariable Long modelId ,
            @RequestParam String beginDate,
            @RequestParam String endDate) {
        return statService.productsCountByDate(modelId,beginDate,endDate);
    }


}
