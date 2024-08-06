package com.clothing.management.controllers;

import com.clothing.management.entities.Governorate;
import com.clothing.management.services.GovernorateService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("${api.prefix}/governorates")
@CrossOrigin
public class GovernorateController {
    private final GovernorateService governorateService;

    public GovernorateController(GovernorateService governorateService) {
        this.governorateService = governorateService;
    }

    @GetMapping
    public ResponseEntity<List<Governorate>> getAllGovernorates() {
        List<Governorate> governorates = governorateService.findAllGovernorates();
        return new ResponseEntity<>(governorates, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Governorate> getGovernorateById(@PathVariable Long id) {
        return governorateService.findGovernorateById(id)
                .map(governorate -> new ResponseEntity<>(governorate, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PostMapping
    public ResponseEntity<Governorate> createGovernorate(@RequestBody Governorate governorate) {
        Governorate createdGovernorate = governorateService.addGovernorate(governorate);
        return new ResponseEntity<>(createdGovernorate, HttpStatus.CREATED);
    }

    @PutMapping
    public ResponseEntity<Governorate> updateGovernorate(@RequestBody Governorate governorate) {
        Governorate updatedGovernorate = governorateService.updateGovernorate(governorate);
        return new ResponseEntity<>(updatedGovernorate, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteGovernorateById(@PathVariable Long id) {
        governorateService.deleteGovernorateById(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping("/batch-delete")
    public ResponseEntity<Void> deleteSelectedGovernorates(@RequestBody List<Long> governorateIds) {
        governorateService.deleteSelectedGovernorates(governorateIds);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
