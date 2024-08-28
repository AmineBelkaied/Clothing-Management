package com.clothing.management.controllers;

import com.clothing.management.dto.DayCount.*;
import com.clothing.management.services.StatService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("${api.prefix}/stats")
@CrossOrigin
public class StatController {
    private final StatService statService;
    public StatController(StatService statService){
        this.statService = statService;
    }

    @GetMapping("/model/{modelId}")
    public ResponseEntity<Map<String, List<?>>> getModelSoldStats(
            @PathVariable Long modelId,
            @RequestParam String beginDate,
            @RequestParam String endDate) {
        try {
            Map<String, List<?>> stats = statService.statModelSoldChart(modelId, beginDate, endDate);
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/models")
    public ResponseEntity<Map<String, List<?>>> getAllModelsStats(
            @RequestParam String beginDate,
            @RequestParam String endDate,
            @RequestParam Boolean countProgress) {
        try {
            Map<String, List<?>> stats = statService.statAllModelsChart(beginDate, endDate, countProgress);
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/stock")
    public ResponseEntity<Map<String, List<?>>> getStockStats(
            @RequestParam String beginDate,
            @RequestParam String endDate) {
        try {
            Map<String, List<?>> stats = statService.statAllStockChart(beginDate, endDate);
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/colors")
    public ResponseEntity<Map<String, List<?>>> getColorsStats(
            @RequestParam String beginDate,
            @RequestParam String endDate) {//,@RequestParam List<Long> modelIds
        try {
            Map<String, List<?>> stats = statService.statAllColorsChart(beginDate, endDate);//, modelIds
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/packets")
    public ResponseEntity<Map<String, List<?>>> getPacketsStats(
            @RequestParam String beginDate,
            @RequestParam String endDate,
            @RequestParam(required = false) String deliveryCompanyName) {
        try {
            Map<String, List<?>> stats = statService.statAllPacketsChart(beginDate, endDate, deliveryCompanyName);
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/offers")
    public ResponseEntity<Map<String, List<?>>> getOffersStats(
            @RequestParam String beginDate,
            @RequestParam String endDate) {
        try {
            Map<String, List<?>> stats = statService.statAllOffersChart(beginDate, endDate);
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/products/{modelId}")
    public ResponseEntity<List<ProductsDayCountDTO>> getProductsCount(
            @PathVariable Long modelId,
            @RequestParam String beginDate,
            @RequestParam String endDate) {
        try {
            List<ProductsDayCountDTO> counts = statService.productsCountByDate(modelId, beginDate, endDate);
            return ResponseEntity.ok(counts);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/sold-products/{modelId}")
    public ResponseEntity<List<SoldProductsDayCountDTO>> getSoldProductsCount(
            @PathVariable Long modelId,
            @RequestParam String beginDate,
            @RequestParam String endDate) {
        try {
            List<SoldProductsDayCountDTO> counts = statService.soldProductsCountByDate(modelId, beginDate, endDate);
            return ResponseEntity.ok(counts);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/pages")
    public ResponseEntity<List<PagesStatCountDTO>> getPacketsPagesCount(
            @RequestParam String beginDate,
            @RequestParam String endDate) {
        try {
            List<PagesStatCountDTO> pagesStatCountDTO = statService.findAllPacketsPages( beginDate, endDate);
            return ResponseEntity.ok(pagesStatCountDTO);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/states")
    public ResponseEntity<List<StatesStatCountDTO>> getPacketsStatesCount(
            @RequestParam String beginDate,
            @RequestParam String endDate) {
        try {
            List<StatesStatCountDTO> statesStatCountDTO = statService.findAllPacketsStates( beginDate, endDate);
            return ResponseEntity.ok(statesStatCountDTO);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
