package com.clothing.management.controllers;

import com.clothing.management.entities.DeliveryCompany;
import com.clothing.management.services.DeliveryCompanyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("deliveryCompany")
@CrossOrigin
public class DeliveryCompanyController {

    @Autowired
    DeliveryCompanyService deliveryCompanyService;

    @GetMapping(path = "/findAll")
    public List<DeliveryCompany> findAllDC() {
        return deliveryCompanyService.findAllStesLivraison();
    }

    @GetMapping(path = "/findById/{id}")
    public Optional<DeliveryCompany> findByIdDC(@PathVariable Long idCompany) {
        return deliveryCompanyService.findSteById(idCompany);
    }

    @PostMapping(value = "/add" , produces = "application/json")
    public DeliveryCompany addDC(@RequestBody  DeliveryCompany ste) {
        return deliveryCompanyService.addSte(ste);
    }

    @PutMapping(value = "/update" , produces = "application/json")
    public DeliveryCompany updateDC(@RequestBody DeliveryCompany ste) {
        return deliveryCompanyService.updateSte(ste);
    }

    @DeleteMapping(value = "/delete" , produces = "application/json")
    public void deleteDC(@RequestBody DeliveryCompany ste) {
        deliveryCompanyService.deleteSte(ste);
    }

    @DeleteMapping(value = "/deleteById/{idSte}")
    public void deleteDCById(@PathVariable Long idSte) {
        deliveryCompanyService.deleteSteById(idSte);
    }
}
