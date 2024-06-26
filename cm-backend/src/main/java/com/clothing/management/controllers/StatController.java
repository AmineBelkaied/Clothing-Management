package com.clothing.management.controllers;
import com.clothing.management.dto.ProductsDayCountDTO;
import com.clothing.management.services.StatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("stat")
@CrossOrigin
public class StatController {
    @Autowired
    StatService statService;

    @GetMapping(path = "/statModelSold/{modelId}")
    public Map<String , List<?>> statModelSold(
            @PathVariable Long modelId ,
            @RequestParam(required = true) String beginDate,
            @RequestParam(required = true) String endDate) {
        return statService.statModelSoldChart(modelId,beginDate,endDate);

    }

    @GetMapping(path = "/statAllModels")
    public Map <String , List<?>> statAllModels(
            @RequestParam(required = true) String beginDate,
            @RequestParam(required = true) String endDate,
            @RequestParam(required = true) Boolean countProgress) {
        return statService.statAllModelsChart(beginDate,endDate,countProgress);
    }

    @GetMapping(path = "/statStock")
    public Map <String , List<?>> statAStock(
            @RequestParam(required = true) String beginDate,
            @RequestParam(required = true) String endDate) {
        return statService.statAllStockChart(beginDate,endDate);
    }

    @GetMapping(path = "/statAllColors")
    public Map <String , List<?>> statAllColors(
            @RequestParam(required = true) String beginDate,
            @RequestParam(required = true) String endDate,
            @RequestParam(required = true) List<Long> modelIds) {
        return statService.statAllColorsChart(beginDate,endDate,modelIds);
    }

    @GetMapping(path = "/statAllPackets")
    public Map <String , List<?>> statAllPackets(
            @RequestParam(required = true) String beginDate,
            @RequestParam(required = true) String endDate) {
        return statService.statAllPacketsChart(beginDate,endDate);
    }
    @GetMapping(path = "/statAllOffers")
    public Map <String , List<?>> statAllOffers(
            @RequestParam(required = true) String beginDate,
            @RequestParam(required = true) String endDate) {
        return statService.statAllOffersChart(beginDate,endDate);
    }

    @GetMapping(path = "/productsCount/{modelId}")
    public List<ProductsDayCountDTO> productsCount(
            @PathVariable Long modelId ,
            @RequestParam(required = true) String beginDate,
            @RequestParam(required = true) String endDate) {
        return statService.productsCountByDate(modelId,beginDate,endDate);
    }


}
