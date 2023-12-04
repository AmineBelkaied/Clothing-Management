package com.clothing.management.controllers;

import com.clothing.management.entities.Color;
import com.clothing.management.services.ColorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("color")
@CrossOrigin
@Secured({"ROLE_ADMIN", "ROLE_USER"})
public class ColorController {

    @Autowired
    ColorService colorService;

    @GetMapping(path = "/findAll")
    public List<Color> findAllColors() {
        return colorService.findAllColors();
    }

    @GetMapping(path = "/findById/{id}")
    public Optional<Color> findByIdColor(@PathVariable Long idColor) {
        return colorService.findColorById(idColor);
    }

    @PostMapping(value = "/add" , produces = "application/json")
    public Color addColor(@RequestBody  Color color) throws Exception {
        return colorService.addColor(color);
    }

    @PutMapping(value = "/update" , produces = "application/json")
    public Color updateColor(@RequestBody Color color) throws Exception {
        return colorService.updateColor(color);
    }

    @DeleteMapping(value = "/delete" , produces = "application/json")
    public void deleteColor(@RequestBody Color color) {
        colorService.deleteColor(color);
    }

    @DeleteMapping(value = "/deleteById/{idColor}")
    public void deleteColorById(@PathVariable Long idColor) {
        colorService.deleteColorById(idColor);
    }
}
