package com.clothing.management.controllers;

import com.clothing.management.entities.Governorate;
import com.clothing.management.services.GovernorateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("governorate")
@CrossOrigin
public class GovernorateController {

    @Autowired
    GovernorateService governorateService;

    @GetMapping(path = "/findAll")
    public List<Governorate> findAllGovernorates() {
        return governorateService.findAllGovernorates();
    }

    @GetMapping(path = "/findById/{id}")
    public Optional<Governorate> findByIdGovernorate(@PathVariable Long idGovernorate) {
        return governorateService.findGovernorateById(idGovernorate);
    }

    @PostMapping(value = "/add" , produces = "application/json")
    public Governorate addGovernorate(@RequestBody  Governorate governorate) {
        return governorateService.addGovernorate(governorate);
    }

    @PutMapping(value = "/update" , produces = "application/json")
    public Governorate updateGovernorate(@RequestBody Governorate governorate) {
        return governorateService.updateGovernorate(governorate);
    }

    @DeleteMapping(value = "/delete" , produces = "application/json")
    public void deleteGovernorate(@RequestBody Governorate governorate) {
        governorateService.deleteGovernorate(governorate);
    }

    @DeleteMapping(value = "/deleteSelectedGovernorates/{governoratesId}" , produces = "application/json")
    public void deleteSelectedGovernorates(@PathVariable List<Long> governoratesId) {
        governorateService.deleteSelectedGovernorates(governoratesId);
    }
}
