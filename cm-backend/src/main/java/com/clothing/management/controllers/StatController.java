package com.clothing.management.controllers;

import com.clothing.management.dto.DayCount.*;
import com.clothing.management.dto.ModelStockValueDTO;
import com.clothing.management.services.StatService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("${api.prefix}/stats")
@CrossOrigin
public class StatController {

    private final StatService statService;
    private static final Logger LOGGER = LoggerFactory.getLogger(StatController.class);

    public StatController(StatService statService) {
        this.statService = statService;
    }

    @GetMapping("/model/{modelId}")
    public ResponseEntity<Map<String, List<?>>> getModelSoldStats(
            @PathVariable Long modelId,
            @RequestParam String beginDate,
            @RequestParam String endDate) {
        LOGGER.info("Fetching sold stats for model ID: {} from {} to {}", modelId, beginDate, endDate);
        try {
            Map<String, List<?>> stats = statService.statModelSoldChart(modelId, beginDate, endDate);
            LOGGER.info("Successfully fetched sold stats for model ID: {}", modelId);
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            LOGGER.error("Error fetching sold stats for model ID {}: ", modelId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/models")
    public ResponseEntity<Map<String, List<?>>> getAllModelsStats(
            @RequestParam String beginDate,
            @RequestParam String endDate,
            @RequestParam Boolean countProgress) {
        LOGGER.info("Fetching stats for all models from {} to {} with countProgress: {}", beginDate, endDate, countProgress);
        try {
            Map<String, List<?>> stats = statService.statAllModelsChart(beginDate, endDate,countProgress);
            LOGGER.info("Successfully fetched stats for all models");
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            LOGGER.error("Error fetching stats for all models: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/stock")
    public ResponseEntity<Map<String, List<?>>> getStockStats(
            @RequestParam String beginDate,
            @RequestParam String endDate) {
        LOGGER.info("Fetching stock stats from {} to {}", beginDate, endDate);
        try {
            Map<String, List<?>> stats = statService.statAllStockChart(beginDate, endDate);
            LOGGER.info("Successfully fetched stock stats");
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            LOGGER.error("Error fetching stock stats: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/values")
    public ResponseEntity<ArrayList<ModelStockValueDTO>> getValuesStats() {
        Date today = new Date();

        try {
            ArrayList<ModelStockValueDTO> stats = statService.statValuesDashboard();
            LOGGER.info("Successfully fetched stock stats");
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            LOGGER.error("Error fetching stock stats: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/colors")
    public ResponseEntity<Map<String, List<?>>> getColorsStats(
            @RequestParam String beginDate,
            @RequestParam String endDate) {
        LOGGER.info("Fetching color stats from {} to {}", beginDate, endDate);
        try {
            Map<String, List<?>> stats = statService.statAllColorsChart(beginDate, endDate);
            LOGGER.info("Successfully fetched color stats");
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            LOGGER.error("Error fetching color stats: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/packets")
    public ResponseEntity<Map<String, List<?>>> getPacketsStats(
            @RequestParam String beginDate,
            @RequestParam String endDate,
            @RequestParam(required = false) String deliveryCompanyName) {
        LOGGER.info("Fetching packet stats from {} to {} for delivery company: {}", beginDate, endDate, deliveryCompanyName);
        try {
            Map<String, List<?>> stats = statService.statAllPacketsChart(beginDate, endDate, deliveryCompanyName);
            LOGGER.info("Successfully fetched packet stats");
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            LOGGER.error("Error fetching packet stats: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/offers")
    public ResponseEntity<Map<String, List<?>>> getOffersStats(
            @RequestParam String beginDate,
            @RequestParam String endDate) {
        LOGGER.info("Fetching offers stats from {} to {}", beginDate, endDate);
        try {
            Map<String, List<?>> stats = statService.statAllOffersChart(beginDate, endDate);
            LOGGER.info("Successfully fetched offers stats");
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            LOGGER.error("Error fetching offers stats: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/products/{modelId}")
    public ResponseEntity<List<ProductsDayCountDTO>> getProductsCount(
            @PathVariable Long modelId,
            @RequestParam String beginDate,
            @RequestParam String endDate) {
        LOGGER.info("Fetching product counts for model ID: {} from {} to {}", modelId, beginDate, endDate);
        try {
            List<ProductsDayCountDTO> counts = statService.productsCountByDate(modelId, beginDate, endDate);
            LOGGER.info("Successfully fetched product counts for model ID: {}", modelId);
            return ResponseEntity.ok(counts);
        } catch (Exception e) {
            LOGGER.error("Error fetching product counts for model ID {}: ", modelId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/pages")
    public ResponseEntity<Map<String, List<?>>> getPagesCountChart(
            @RequestParam String beginDate,
            @RequestParam String endDate) {
        LOGGER.info("Fetching packet pages stats from {} to {}", beginDate, endDate);
        try {
            Map<String, List<?>> pagesStatCountDTO = statService.statAllPagesChart(beginDate, endDate);
            LOGGER.info("Successfully fetched packet pages stats");
            return ResponseEntity.ok(pagesStatCountDTO);
        } catch (Exception e) {
            LOGGER.error("Error fetching packet pages stats: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/states")
    public ResponseEntity<List<StatesStatCountDTO>> getPacketsStatesCount(
            @RequestParam String beginDate,
            @RequestParam String endDate) {
        LOGGER.info("Fetching packet states stats from {} to {}", beginDate, endDate);
        try {
            List<StatesStatCountDTO> statesStatCountDTO = statService.findAllPacketsStates(beginDate, endDate);
            LOGGER.info("Successfully fetched packet states stats");
            return ResponseEntity.ok(statesStatCountDTO);
        } catch (Exception e) {
            LOGGER.error("Error fetching packet states stats: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
