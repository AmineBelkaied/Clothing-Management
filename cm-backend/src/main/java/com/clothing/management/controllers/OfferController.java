package com.clothing.management.controllers;

import com.clothing.management.dto.*;
import com.clothing.management.entities.FbPage;
import com.clothing.management.entities.Offer;
import com.clothing.management.entities.OfferModel;
import com.clothing.management.entities.Packet;
import com.clothing.management.services.OfferService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("offer")
@CrossOrigin
@Secured({"ROLE_ADMIN", "ROLE_USER"})
public class OfferController {

    @Autowired
    OfferService offerService;

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
    @GetMapping(path = "/offersDTO")
    public List<OfferDTO> getOffers() throws IOException {
        return offerService.getOffers();
    }


    @GetMapping(path = "/findById/{idOffer}")
    public Optional<Offer> findOfferById(@PathVariable Long idOffer) {
        return offerService.findOfferById(idOffer);
    }

    @GetMapping(path = "/findByFBPage/{fbPageId}")
    public List<OfferModel> findByFbPageId(@PathVariable Long fbPageId) throws IOException {
        return offerService.findOfferByFbPageId(fbPageId);
    }

    @PostMapping(value = "/add" , produces = "application/json")
    public OfferDTO addOffer(@RequestBody  OfferDTO offerDTO) {
        return offerService.addOffer(offerDTO);
    }
    @PostMapping(value = "/update" , produces = "application/json")
    public OfferDTO updateOffer(@RequestBody Offer offer) throws Exception {
        return offerService.updateOffer(offer);
    }
    @PutMapping(value = "/updateData" , produces = "application/json")
    public OfferDTO updateOffer(@RequestParam long id,@RequestParam String name,@RequestParam double price,@RequestParam boolean enabled) throws Exception {
        return offerService.updateOfferData(id, name, price, enabled);
    }
    @PutMapping(value = "/updateOfferFbPages" , produces = "application/json")
    public OfferDTO updateOfferFbPages(@RequestParam("offerId") long offerId,@RequestBody Set<FbPage> fbPages) throws Exception {
        return offerService.updateOfferFbPages(offerId,fbPages);
    }

    @PutMapping(value = "/updateOfferModels" , produces = "application/json")
    public OfferDTO updateOfferModels(@RequestParam("offerId") long offerId,@RequestBody Set<OfferModelsDTO> offerModelsDTO) throws Exception {
        return offerService.updateOfferModels(offerId,offerModelsDTO);
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
