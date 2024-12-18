package com.clothing.management.controllers;

import com.clothing.management.entities.GlobalConf;
import com.clothing.management.services.GlobalConfService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("${api.prefix}/global-config")
public class GlobalConfController {

    private final GlobalConfService globalConfService;
    private static final Logger LOGGER = LoggerFactory.getLogger(GlobalConfController.class);

    public GlobalConfController(GlobalConfService globalConfService) {
        this.globalConfService = globalConfService;
    }

    @GetMapping
    public ResponseEntity<GlobalConf> getGlobalConfig() {
        LOGGER.info("Fetching global configuration.");
        GlobalConf globalConf = globalConfService.getGlobalConf();
        if (globalConf != null) {
            LOGGER.info("Global configuration retrieved successfully.");
            return new ResponseEntity<>(globalConf, HttpStatus.OK);
        } else {
            LOGGER.warn("Global configuration not found.");
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> upsertGlobalConfig(@RequestBody GlobalConf globalConf) {
        LOGGER.info("Upserting global configuration: {}", globalConf);
        try {
            globalConfService.updateGlobalConf(globalConf);
            LOGGER.info("Global configuration updated successfully.");
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            LOGGER.error("Error occurred while updating global configuration: {}", e.getMessage());
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
}
