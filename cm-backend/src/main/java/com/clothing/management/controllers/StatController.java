package com.clothing.management.controllers;

import com.clothing.management.dto.ProductsDayCountDTO;
import com.clothing.management.services.StatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("${api.prefix}/stats")
@CrossOrigin
public class StatController {

    @Autowired
    private StatService statService;

    @GetMapping("/model-sold/{modelId}")
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

    @GetMapping("/all-models")
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
            @RequestParam String endDate,
            @RequestParam List<Long> modelIds) {
        try {
            Map<String, List<?>> stats = statService.statAllColorsChart(beginDate, endDate, modelIds);
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/packets")
    public ResponseEntity<Map<String, List<?>>> getPacketsStats(
            @RequestParam String beginDate,
            @RequestParam String endDate) {
        try {
            Map<String, List<?>> stats = statService.statAllPacketsChart(beginDate, endDate);
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

    @GetMapping("/products-count/{modelId}")
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
}
