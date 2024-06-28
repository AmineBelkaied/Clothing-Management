package com.clothing.management.controllers;

import com.clothing.management.entities.DeliveryCompany;
import com.clothing.management.services.DeliveryCompanyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("${api.prefix}/delivery-companies")
@CrossOrigin
public class DeliveryCompanyController {

    @Autowired
    DeliveryCompanyService deliveryCompanyService;

    @GetMapping
    public ResponseEntity<List<DeliveryCompany>> getAllDeliveryCompanies() {
        List<DeliveryCompany> deliveryCompanies = deliveryCompanyService.findAllDeliveryCompanies();
        return new ResponseEntity<>(deliveryCompanies, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<DeliveryCompany> getDeliveryCompanyById(@PathVariable Long id) {
        Optional<DeliveryCompany> deliveryCompany = deliveryCompanyService.findDeliveryCompanyById(id);
        return deliveryCompany.map(company -> new ResponseEntity<>(company, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PostMapping
    public ResponseEntity<DeliveryCompany> createDeliveryCompany(@RequestBody DeliveryCompany deliveryCompany) {
        DeliveryCompany createdCompany = deliveryCompanyService.addDeliveryCompany(deliveryCompany);
        return new ResponseEntity<>(createdCompany, HttpStatus.CREATED);
    }

    @PutMapping
    public ResponseEntity<DeliveryCompany> updateDeliveryCompany(@RequestBody DeliveryCompany deliveryCompany) {
        DeliveryCompany updatedCompany = deliveryCompanyService.updateDeliveryCompany(deliveryCompany);
        return new ResponseEntity<>(updatedCompany, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDeliveryCompanyById(@PathVariable Long id) {
        deliveryCompanyService.deleteDeliveryCompanyById(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteDeliveryCompany(@RequestBody DeliveryCompany deliveryCompany) {
        deliveryCompanyService.deleteDeliveryCompany(deliveryCompany);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
