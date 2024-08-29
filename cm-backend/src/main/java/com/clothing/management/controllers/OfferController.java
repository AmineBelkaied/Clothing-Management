package com.clothing.management.controllers;

import com.clothing.management.dto.*;
import com.clothing.management.entities.FbPage;
import com.clothing.management.entities.Offer;
import com.clothing.management.entities.OfferModel;
import com.clothing.management.services.OfferService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("${api.prefix}/offers")
@CrossOrigin
@Secured({"ROLE_ADMIN", "ROLE_USER"})
public class OfferController {

    private final OfferService offerService;

    public OfferController(OfferService offerService){
        this.offerService = offerService;
    }

    @GetMapping
    public List<OfferDTO> getOffers(){
        return offerService.getOffers();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Offer> getOfferById(@PathVariable Long id) {
        return offerService.findOfferById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @GetMapping("/fb-page/{fbPageId}")
    public ResponseEntity<List<OfferModel>> getOffersByFbPageId(@PathVariable("fbPageId") Long fbPageId) throws IOException {
        List<OfferModel> offers = offerService.findOfferByFbPageId(fbPageId);
        return ResponseEntity.ok(offers);
    }

    @PostMapping
    public ResponseEntity<OfferDTO> addOffer(@RequestBody OfferDTO offerDTO) {
        OfferDTO createdOffer = offerService.addOffer(offerDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdOffer);
    }

    @PutMapping
    public ResponseEntity<OfferDTO> updateOffer(@RequestBody Offer offer) throws Exception {
        OfferDTO updatedOffer = offerService.updateOffer(offer);
        return ResponseEntity.ok(updatedOffer);
    }

    @PutMapping(value = "/update-offer-models" , produces = "application/json")
    public OfferDTO updateOfferModels(@RequestParam("offerId") long offerId, @RequestBody Set<OfferModelsDTO> offerModelsDTO) throws Exception {
        return offerService.updateOfferModels(offerId,offerModelsDTO);
    }

    @GetMapping(value = "/update-data" , produces = "application/json")
    public OfferDTO updateOffer(@RequestParam("id") Long id,@RequestParam("name") String name,@RequestParam("price") double price,@RequestParam("enabled") boolean enabled) throws Exception {
        return offerService.updateOfferData(id, name, price, enabled);
    }

    @PutMapping(value = "/update-offer-fb-pages" , produces = "application/json")
    public OfferDTO updateOfferFbPages(@RequestParam("offerId") long offerId,@RequestBody Set<FbPage> fbPages) throws Exception {
        return offerService.updateOfferFbPages(offerId,fbPages);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOffer(@PathVariable Long id) {
        offerService.deleteOffer(new Offer(id));
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping(value = "/delete" , produces = "application/json")
    public void deleteOffer(@RequestBody Offer offer) {
        offerService.deleteOffer(offer);
    }

    @DeleteMapping("/batch-delete/{offerIds}")
    public ResponseEntity<Void> deleteSelectedOffers(@PathVariable List<Long> offerIds) {
        offerService.deleteSelectedOffers(offerIds);
        return ResponseEntity.noContent().build();
    }
}

    /*@GetMapping(path = "/findAll")
    public List<OfferModelsDTO> findAllOffers() throws IOException {
        return offerService.findAllOffers();
    }

    /*@GetMapping(path = "/findAllOffersModelQuantities")
    public List<OfferModelQuantitiesDTO> findAllOffersModelQuantities() throws IOException {
        return offerService.findAllOffersModelQuantities();
    }

    @GetMapping(path = "/findOffersModelQuantitiesById/{idOffer}")
    public OfferModelQuantitiesDTO findOffersModelQuantitiesById(@PathVariable Long idOffer) throws IOException {
        return offerService.findOffersModelQuantitiesById(idOffer);
    }*/