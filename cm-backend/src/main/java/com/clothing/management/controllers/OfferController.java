package com.clothing.management.controllers;

import com.clothing.management.dto.OfferModelQuantitiesDTO;
import com.clothing.management.dto.OfferModelsDTO;
import com.clothing.management.entities.Offer;
import com.clothing.management.services.OfferService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("offer")
@CrossOrigin
@Secured({"ROLE_ADMIN", "ROLE_USER"})
public class OfferController {

    @Autowired
    OfferService offerService;

    @GetMapping(path = "/findAll")
    public List<OfferModelsDTO> findAllOffers() throws IOException {
        return offerService.findAllOffers();
    }

    @GetMapping(path = "/findAllOffersModelQuantities")
    public List<OfferModelQuantitiesDTO> findAllOffersModelQuantities() throws IOException {
        return offerService.findAllOffersModelQuantities();
    }

    @GetMapping(path = "/findById/{id}")
    public Optional<Offer> findOfferById(@PathVariable Long idOffer) {
        return offerService.findOfferById(idOffer);
    }

    @GetMapping(path = "/findByFBPage/{id}")
    public List<OfferModelsDTO> findByFbPageId(@PathVariable Long fbPageId) throws IOException {
        return offerService.findOfferByFbPageId(fbPageId);
    }

    @PostMapping(value = "/add" , produces = "application/json")
    public OfferModelQuantitiesDTO addOffer(@RequestBody  OfferModelQuantitiesDTO offerModelDTO) {
        return offerService.addOffer(offerModelDTO);
    }

    @PutMapping(value = "/update" , produces = "application/json")
    public OfferModelQuantitiesDTO updateOffer(@RequestBody OfferModelQuantitiesDTO offerModelDTO) {
        return offerService.updateOffer(offerModelDTO);
    }

    @DeleteMapping(value = "/delete" , produces = "application/json")
    public void deleteOffer(@RequestBody Offer offer) {
        offerService.deleteOffer(offer);
    }

    @DeleteMapping(value = "/deleteSelectedOffers/{offersId}" , produces = "application/json")
    public void deleteSelectedOffers(@PathVariable List<Long> offersId) {
        offerService.deleteSelectedOffers(offersId);
    }
}
