package com.clothing.management.controllers;

import com.clothing.management.dto.*;
import com.clothing.management.entities.FbPage;
import com.clothing.management.entities.Offer;
import com.clothing.management.entities.OfferModel;
import com.clothing.management.services.OfferService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private static final Logger LOGGER = LoggerFactory.getLogger(OfferController.class);

    public OfferController(OfferService offerService) {
        this.offerService = offerService;
    }

    @GetMapping
    public ResponseEntity<List<OfferDTO>> getOffers() {
        LOGGER.info("Fetching all offers.");
        try {
            List<OfferDTO> offers = offerService.getOffers();
            LOGGER.info("Successfully fetched {} offers.", offers.size());
            return ResponseEntity.ok(offers);
        } catch (Exception e) {
            LOGGER.error("Error fetching offers: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Offer> getOfferById(@PathVariable Long id) {
        LOGGER.info("Fetching offer with id: {}", id);
        return offerService.findOfferById(id)
                .map(offer -> {
                    LOGGER.info("Successfully fetched offer: {}", offer);
                    return ResponseEntity.ok(offer);
                })
                .orElseGet(() -> {
                    LOGGER.warn("Offer with id: {} not found.", id);
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
                });
    }

    @GetMapping("/fb-page/{fbPageId}")
    public ResponseEntity<List<OfferModel>> getOffersByFbPageId(@PathVariable("fbPageId") Long fbPageId) throws IOException {
        LOGGER.info("Fetching offers for fbPageId: {}", fbPageId);
        try {
            List<OfferModel> offers = offerService.findOfferByFbPageId(fbPageId);
            LOGGER.info("Successfully fetched {} offers for fbPageId: {}", offers.size(), fbPageId);
            return ResponseEntity.ok(offers);
        } catch (IOException e) {
            LOGGER.error("Error fetching offers for fbPageId: {}: ", fbPageId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping
    public ResponseEntity<OfferDTO> addOffer(@RequestBody OfferRequest offerDTO) {
        LOGGER.info("Creating new offer: {}", offerDTO);
        try {
            OfferDTO createdOffer = offerService.addOffer(offerDTO);
            LOGGER.info("Offer created successfully: {}", createdOffer);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdOffer);
        } catch (Exception e) {
            LOGGER.error("Error creating offer: ", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @PutMapping
    public ResponseEntity<OfferDTO> updateOffer(@RequestBody Offer offer) {
        LOGGER.info("Updating offer: {}", offer);
        try {
            OfferDTO updatedOffer = offerService.updateOffer(offer);
            LOGGER.info("Offer updated successfully: {}", updatedOffer);
            return ResponseEntity.ok(updatedOffer);
        } catch (Exception e) {
            LOGGER.error("Error updating offer: ", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @PutMapping(value = "/update-offer-models", produces = "application/json")
    public ResponseEntity<OfferDTO> updateOfferModels(@RequestParam("offerId") long offerId, @RequestBody Set<OfferModelsDTO> offerModelsDTO) {
        LOGGER.info("Updating offer models for offerId: {}", offerId);
        try {
            OfferDTO updatedOffer = offerService.updateOfferModels(offerId, offerModelsDTO);
            LOGGER.info("Offer models updated successfully for offerId: {}", offerId);
            return ResponseEntity.ok(updatedOffer);
        } catch (Exception e) {
            LOGGER.error("Error updating offer models for offerId: {}: ", offerId, e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @GetMapping(value = "/update-data", produces = "application/json")
    public ResponseEntity<OfferDTO> updateOffer(@RequestParam("id") Long id, @RequestParam("name") String name,
                                                @RequestParam("price") double price, @RequestParam("enabled") boolean isEnabled) {
        LOGGER.info("Updating offer data for id: {}", id);
        try {
            OfferDTO updatedOffer = offerService.updateOfferData(id, name, price, isEnabled);
            LOGGER.info("Offer data updated successfully for id: {}", id);
            return ResponseEntity.ok(updatedOffer);
        } catch (Exception e) {
            LOGGER.error("Error updating offer data for id: {}: ", id, e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @PutMapping(value = "/update-offer-fb-pages", produces = "application/json")
    public ResponseEntity<OfferDTO> updateOfferFbPages(@RequestParam("offerId") long offerId, @RequestBody Set<Long> fbPagesId) {
        LOGGER.info("Updating offer fb pages for offerId: {}", offerId);
        try {
            OfferDTO updatedOffer = offerService.updateOfferFbPages(offerId, fbPagesId);
            LOGGER.info("Offer fb pages updated successfully for offerId: {}", offerId);
            return ResponseEntity.ok(updatedOffer);
        } catch (Exception e) {
            LOGGER.error("Error updating offer fb pages for offerId: {}: ", offerId, e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @GetMapping("/check-offer-usage/{id}")
    public ResponseEntity<Long> checkOfferUsage(@PathVariable Long id) {
        LOGGER.info("Checking offer with id: {}", id);
        long offerUsageNumber = offerService.checkOfferUsage(id);
        LOGGER.info("Model with id used : {} .", offerUsageNumber);
        return new ResponseEntity<>(offerUsageNumber, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOffer(@PathVariable Long id, @RequestParam("soft-delete") boolean isSoftDelete) {
        try {
            offerService.deleteOfferById(id, isSoftDelete);
            LOGGER.info("Offer with id: {} deleted successfully.", id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            LOGGER.error("Error deleting offer with id: {}: ", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping(value = "/delete", produces = "application/json")
    public ResponseEntity<Void> deleteOffer(@RequestBody Offer offer) {
        LOGGER.info("Deleting offer: {}", offer);
        try {
            offerService.deleteOffer(offer);
            LOGGER.info("Offer deleted successfully: {}", offer);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            LOGGER.error("Error deleting offer: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/batch-delete/{offerIds}")
    public ResponseEntity<Void> deleteSelectedOffers(@PathVariable List<Long> offerIds) {
        LOGGER.info("Deleting offers with ids: {}", offerIds);
        try {
            offerService.deleteSelectedOffers(offerIds);
            LOGGER.info("Offers with ids: {} deleted successfully.", offerIds);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            LOGGER.error("Error deleting offers with ids: {}: ", offerIds, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/rollback/{id}")
    public ResponseEntity<Void> rollBackOffer(@PathVariable Long id) {
        LOGGER.info("Rollback model with id: {}", id);
        offerService.rollBackOffer(id);
        LOGGER.info("Offer with id: {} rolled back successfully.", id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
