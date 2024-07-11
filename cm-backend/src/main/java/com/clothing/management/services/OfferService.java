package com.clothing.management.services;

import com.clothing.management.dto.OfferModelQuantitiesDTO;
import com.clothing.management.dto.OfferModelsDTO;
import com.clothing.management.entities.Offer;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public interface OfferService {
    
    public List<OfferModelsDTO> findAllOffers() throws IOException;
    public List<OfferModelQuantitiesDTO> findAllOffersModelQuantities() throws IOException;
    public Optional<Offer> findOfferById(Long idOffer);
    public Offer findOfferByName(String name);
    public OfferModelQuantitiesDTO addOffer(OfferModelQuantitiesDTO offerModelDTO);
    public OfferModelQuantitiesDTO updateOffer(OfferModelQuantitiesDTO offerModelDTO);
    public void deleteOffer(Offer offer);
    void deleteSelectedOffers(List<Long> offersId);
    List<OfferModelsDTO> findOfferByFbPageId(Long fbPageId) throws IOException;
}
