package com.clothing.management.controllers;

import com.clothing.management.entities.Size;
import com.clothing.management.services.SizeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("${api.prefix}/sizes")
@CrossOrigin
@Secured({"ROLE_ADMIN", "ROLE_USER"})
public class SizeController {

    private final SizeService sizeService;
    private static final Logger LOGGER = LoggerFactory.getLogger(SizeController.class);

    public SizeController(SizeService sizeService) {
        this.sizeService = sizeService;
    }

    @GetMapping
    public ResponseEntity<List<Size>> getAllSizes() {
        LOGGER.info("Fetching all sizes");
        try {
            List<Size> sizes = sizeService.findAllSizes();
            LOGGER.info("Successfully fetched {} sizes", sizes.size());
            return ResponseEntity.ok(sizes);
        } catch (Exception e) {
            LOGGER.error("Error fetching sizes: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Size> getSizeById(@PathVariable Long id) {
        LOGGER.info("Fetching size with id: {}", id);
        try {
            Size size = sizeService.findSizeById(id);
            return ResponseEntity.ok(size);
        } catch (Exception e) {
            LOGGER.error("Error fetching size with id {}: ", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping
    public ResponseEntity<Size> createSize(@RequestBody Size size) {
        LOGGER.info("Creating new size: {}", size);
        try {
            Size createdSize = sizeService.addSize(size);
            LOGGER.info("Successfully created size: {}", createdSize);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdSize);
        } catch (Exception e) {
            LOGGER.error("Error creating size: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping
    public ResponseEntity<Size> updateSize(@RequestBody Size size) {
        LOGGER.info("Updating size: {}", size);
        try {
            Size updatedSize = sizeService.updateSize(size);
            if (updatedSize != null) {
                LOGGER.info("Successfully updated size: {}", updatedSize);
                return ResponseEntity.ok(updatedSize);
            } else {
                LOGGER.warn("Size to update not found: {}", size);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
        } catch (Exception e) {
            LOGGER.error("Error updating size: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/check-size-usage/{id}")
    public ResponseEntity<Long> checkSizeUsage(@PathVariable Long id) {
        LOGGER.info("Checking size with id: {}", id);
        Long sizeUsage = sizeService.checkSizeUsage(id);
        LOGGER.info("Size with id used : {} .", sizeUsage);
        return new ResponseEntity<>(sizeUsage, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSizeById(@PathVariable Long id) {
        LOGGER.info("Deleting size with id: {}", id);
        try {
            sizeService.deleteSizeById(id);
            LOGGER.info("Successfully deleted size with id: {}", id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            LOGGER.error("Error deleting size with id {}: ", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
