package com.clothing.management.controllers;

import com.clothing.management.entities.Color;
import com.clothing.management.services.ColorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("${api.prefix}/colors")
@CrossOrigin
@Secured({"ROLE_ADMIN", "ROLE_USER"})
public class ColorController {

    @Autowired
    ColorService colorService;

    @GetMapping
    public ResponseEntity<List<Color>> getAllColors() {
        List<Color> colors = colorService.findAllColors();
        return new ResponseEntity<>(colors, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Color> getColorById(@PathVariable Long id) {
        Optional<Color> color = colorService.findColorById(id);
        return color.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PostMapping
    public ResponseEntity<Color> createColor(@RequestBody Color color) throws Exception {
        Color createdColor = colorService.addColor(color);
        return new ResponseEntity<>(createdColor, HttpStatus.CREATED);
    }

    @PutMapping
    public ResponseEntity<Color> updateColor(@RequestBody Color color) throws Exception {
        Color updatedColor = colorService.updateColor(color);
        return new ResponseEntity<>(updatedColor, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteColorById(@PathVariable Long id) {
        colorService.deleteColorById(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteColor(@RequestBody Color color) {
        colorService.deleteColor(color);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
