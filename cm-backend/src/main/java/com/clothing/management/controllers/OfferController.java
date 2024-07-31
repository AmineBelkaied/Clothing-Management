package com.clothing.management.controllers;

import com.clothing.management.dto.OfferModelQuantitiesDTO;
import com.clothing.management.dto.OfferModelsDTO;
import com.clothing.management.entities.Offer;
import com.clothing.management.services.OfferService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("${api.prefix}/offers")
@CrossOrigin
@Secured({"ROLE_ADMIN", "ROLE_USER"})
public class OfferController {

    private final OfferService offerService;

    @Autowired
    public OfferController(OfferService offerService) {
        this.offerService = offerService;
    }

    @GetMapping
    public ResponseEntity<List<OfferModelsDTO>> getAllOffers() throws IOException {
        List<OfferModelsDTO> offers = offerService.findAllOffers();
        return ResponseEntity.ok(offers);
    }

    @GetMapping("/model-quantities")
    public ResponseEntity<List<OfferModelQuantitiesDTO>> getAllOffersModelQuantities() throws IOException {
        List<OfferModelQuantitiesDTO> offerQuantities = offerService.findAllOffersModelQuantities();
        return ResponseEntity.ok(offerQuantities);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Offer> getOfferById(@PathVariable Long id) {
        return offerService.findOfferById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @GetMapping("/fb-page/{fbPageId}")
    public ResponseEntity<List<OfferModelsDTO>> getOffersByFbPageId(@PathVariable("fbPageId") Long fbPageId) throws IOException {
        List<OfferModelsDTO> offers = offerService.findOfferByFbPageId(fbPageId);
        return ResponseEntity.ok(offers);
    }

    @PostMapping
    public ResponseEntity<OfferModelQuantitiesDTO> addOffer(@RequestBody OfferModelQuantitiesDTO offerModelDTO) {
        OfferModelQuantitiesDTO createdOffer = offerService.addOffer(offerModelDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdOffer);
    }

    @PutMapping
    public ResponseEntity<OfferModelQuantitiesDTO> updateOffer(@RequestBody OfferModelQuantitiesDTO offerModelDTO) {
        OfferModelQuantitiesDTO updatedOffer = offerService.updateOffer(offerModelDTO);
        return ResponseEntity.ok(updatedOffer);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOffer(@PathVariable Long id) {
        offerService.deleteOffer(new Offer(id));
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/batch-delete/{offerIds}")
    public ResponseEntity<Void> deleteSelectedOffers(@PathVariable List<Long> offerIds) {
        offerService.deleteSelectedOffers(offerIds);
        return ResponseEntity.noContent().build();
    }
}
