package com.clothing.management.controllers;

import com.clothing.management.entities.DeliveryCompany;
import com.clothing.management.services.DeliveryCompanyService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("${api.prefix}/delivery-companies")
@CrossOrigin
public class DeliveryCompanyController {

    private final DeliveryCompanyService deliveryCompanyService;
    private static final Logger LOGGER = LoggerFactory.getLogger(DeliveryCompanyController.class);

    public DeliveryCompanyController(DeliveryCompanyService deliveryCompanyService) {
        this.deliveryCompanyService = deliveryCompanyService;
    }

    @GetMapping
    public ResponseEntity<List<DeliveryCompany>> getAllDeliveryCompanies() {
        LOGGER.info("Fetching all delivery companies.");
        List<DeliveryCompany> deliveryCompanies = deliveryCompanyService.findAllDeliveryCompanies();
        return new ResponseEntity<>(deliveryCompanies, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<DeliveryCompany> getDeliveryCompanyById(@PathVariable Long id) {
        LOGGER.info("Fetching delivery company with ID: {}", id);
        Optional<DeliveryCompany> deliveryCompany = deliveryCompanyService.findDeliveryCompanyById(id);
        return deliveryCompany.map(company -> new ResponseEntity<>(company, HttpStatus.OK))
                .orElseGet(() -> {
                    LOGGER.warn("Delivery company with ID {} not found.", id);
                    return new ResponseEntity<>(HttpStatus.NOT_FOUND);
                });
    }

    @PostMapping
    public ResponseEntity<DeliveryCompany> createDeliveryCompany(@RequestBody DeliveryCompany deliveryCompany) {
        LOGGER.info("Creating a new delivery company: {}", deliveryCompany.getName());
        DeliveryCompany createdCompany = deliveryCompanyService.addDeliveryCompany(deliveryCompany);
        return new ResponseEntity<>(createdCompany, HttpStatus.CREATED);
    }

    @PutMapping
    public ResponseEntity<DeliveryCompany> updateDeliveryCompany(@RequestBody DeliveryCompany deliveryCompany) {
        LOGGER.info("Updating delivery company with ID: {}", deliveryCompany.getId());
        DeliveryCompany updatedCompany = deliveryCompanyService.updateDeliveryCompany(deliveryCompany);
        return new ResponseEntity<>(updatedCompany, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDeliveryCompanyById(@PathVariable Long id) {
        LOGGER.info("Deleting delivery company with ID: {}", id);
        deliveryCompanyService.deleteDeliveryCompanyById(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteDeliveryCompany(@RequestBody DeliveryCompany deliveryCompany) {
        LOGGER.info("Deleting delivery company with ID: {}", deliveryCompany.getId());
        deliveryCompanyService.deleteDeliveryCompany(deliveryCompany);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
