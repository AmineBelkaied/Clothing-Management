package com.clothing.management.controllers;

import com.clothing.management.dto.ModelDeleteDTO;
import com.clothing.management.entities.Color;
import com.clothing.management.exceptions.custom.notfound.PacketNotFoundException;
import com.clothing.management.services.ColorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("${api.prefix}/colors")
@CrossOrigin
public class ColorController {

    private final ColorService colorService;
    private static final Logger LOGGER = LoggerFactory.getLogger(ColorController.class);

    public ColorController(ColorService colorService) {
        this.colorService = colorService;
    }

    @GetMapping
    public ResponseEntity<List<Color>> getAllColors() {
        LOGGER.info("Fetching all colors.");
        List<Color> colors = colorService.findAllColors();
        return new ResponseEntity<>(colors, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Color> getColorById(@PathVariable Long id) {
        LOGGER.info("Fetching color with ID: {}", id);
        Color color = colorService.findColorById(id).orElseThrow(() -> {
            LOGGER.error("Color with ID: {} not found", id);
            return new PacketNotFoundException(id, "Packet not found!");
        });;
        return new ResponseEntity<>(color, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<Color> createColor(@RequestBody Color color) throws Exception {
        LOGGER.info("Creating a new color with name: {}", color.getName());
        Color createdColor = colorService.addColor(color);
        return new ResponseEntity<>(createdColor, HttpStatus.CREATED);
    }

    @PutMapping
    public ResponseEntity<Color> updateColor(@RequestBody Color color) {
        LOGGER.info("Updating color with ID: {}", color.getId());
        Color updatedColor = colorService.updateColor(color);
        return new ResponseEntity<>(updatedColor, HttpStatus.OK);
    }

    @GetMapping("/check-color-usage/{id}")
    public ResponseEntity<Long> checkColorUsage(@PathVariable Long id) {
        LOGGER.info("Checking color with id: {}", id);
        Long colorUsage = colorService.checkColorUsage(id);
        LOGGER.info("Color with id used : {} .", colorUsage);
        return new ResponseEntity<>(colorUsage, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteColorById(@PathVariable Long id) {
        LOGGER.info("Deleting color with ID: {}", id);
        colorService.deleteColorById(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteColor(@RequestBody Color color) {
        LOGGER.info("Deleting color with ID: {}", color.getId());
        colorService.deleteColor(color);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
