package com.clothing.management.controllers;

import com.clothing.management.dto.ModelQuantity;
import com.clothing.management.dto.OfferModelDTO;
import com.clothing.management.entities.Offer;
import com.clothing.management.entities.OfferModel;
import com.clothing.management.repository.IOfferModelRepository;
import com.clothing.management.services.OfferService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static java.util.stream.Collectors.groupingBy;

@RestController
@RequestMapping("offer")
@CrossOrigin
public class OfferController {

    @Autowired
    OfferService offerService;

    @GetMapping(path = "/findAll")
    public List<OfferModelDTO> findAllOffers() {
        return offerService.findAllOffers();
    }

    @GetMapping(path = "/findById/{id}")
    public Optional<Offer> findByIdOffer(@PathVariable Long idOffer) {
        return offerService.findOfferById(idOffer);
    }

    @PostMapping(value = "/add" , produces = "application/json")
    public OfferModelDTO addOffer(@RequestBody  OfferModelDTO offerModelDTO) {
        return offerService.addOffer(offerModelDTO);
    }

    @PutMapping(value = "/update" , produces = "application/json")
    public OfferModelDTO updateOffer(@RequestBody OfferModelDTO offerModelDTO) {
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
