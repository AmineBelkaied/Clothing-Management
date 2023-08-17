package com.clothing.management.controllers;

import com.clothing.management.dto.OfferModelQuantitiesDTO;
import com.clothing.management.dto.OfferModelsDTO;
import com.clothing.management.entities.Offer;
import com.clothing.management.services.OfferService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("offer")
@CrossOrigin
public class OfferController {

    @Autowired
    OfferService offerService;

    @GetMapping(path = "/findAll")
    public List<OfferModelsDTO> findAllOffers() {
        return offerService.findAllOffers();
    }

    @GetMapping(path = "/findAllOffersModelQuantities")
    public List<OfferModelQuantitiesDTO> findAllOffersModelQuantities() {
        return offerService.findAllOffersModelQuantities();
    }

    @GetMapping(path = "/findById/{id}")
    public Optional<Offer> findByIdOffer(@PathVariable Long idOffer) {
        return offerService.findOfferById(idOffer);
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
