package com.clothing.management.controllers;

import com.clothing.management.entities.Governorate;
import com.clothing.management.services.GovernorateService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("${api.prefix}/governorates")
@CrossOrigin
public class GovernorateController {

    private final GovernorateService governorateService;
    private static final Logger LOGGER = LoggerFactory.getLogger(GovernorateController.class);

    public GovernorateController(GovernorateService governorateService) {
        this.governorateService = governorateService;
    }

    @GetMapping
    public ResponseEntity<List<Governorate>> getAllGovernorates() {
        LOGGER.info("Fetching all governorates.");
        List<Governorate> governorates = governorateService.findAllGovernorates();
        LOGGER.info("Successfully fetched {} governorates.", governorates.size());
        return new ResponseEntity<>(governorates, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Governorate> getGovernorateById(@PathVariable Long id) {
        LOGGER.info("Fetching governorate with id: {}", id);
        return governorateService.findGovernorateById(id)
                .map(governorate -> {
                    LOGGER.info("Governorate found: {}", governorate);
                    return new ResponseEntity<>(governorate, HttpStatus.OK);
                })
                .orElseGet(() -> {
                    LOGGER.warn("Governorate with id: {} not found.", id);
                    return new ResponseEntity<>(HttpStatus.NOT_FOUND);
                });
    }

    @PostMapping
    public ResponseEntity<Governorate> createGovernorate(@RequestBody Governorate governorate) {
        LOGGER.info("Creating a new governorate: {}", governorate);
        Governorate createdGovernorate = governorateService.addGovernorate(governorate);
        LOGGER.info("Governorate created successfully: {}", createdGovernorate);
        return new ResponseEntity<>(createdGovernorate, HttpStatus.CREATED);
    }

    @PutMapping
    public ResponseEntity<Governorate> updateGovernorate(@RequestBody Governorate governorate) {
        LOGGER.info("Updating governorate: {}", governorate);
        Governorate updatedGovernorate = governorateService.updateGovernorate(governorate);
        LOGGER.info("Governorate updated successfully: {}", updatedGovernorate);
        return new ResponseEntity<>(updatedGovernorate, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteGovernorateById(@PathVariable Long id) {
        LOGGER.info("Deleting governorate with id: {}", id);
        governorateService.deleteGovernorateById(id);
        LOGGER.info("Governorate with id: {} deleted successfully.", id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping("/batch-delete")
    public ResponseEntity<Void> deleteSelectedGovernorates(@RequestBody List<Long> governorateIds) {
        LOGGER.info("Batch deleting governorates with ids: {}", governorateIds);
        governorateService.deleteSelectedGovernorates(governorateIds);
        LOGGER.info("Successfully deleted governorates with ids: {}", governorateIds);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
