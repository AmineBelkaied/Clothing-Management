package com.clothing.management.services;

import com.clothing.management.dto.OfferModelDTO;
import com.clothing.management.entities.Offer;

import java.util.List;
import java.util.Optional;

public interface OfferService {
    
    public List<OfferModelDTO> findAllOffers();
    public Optional<Offer> findOfferById(Long idOffer);
    public Offer findOfferByName(String name);
    public OfferModelDTO addOffer(OfferModelDTO offerModelDTO);
    public OfferModelDTO updateOffer(OfferModelDTO offerModelDTO);
    public void deleteOffer(Offer offer);
    void deleteSelectedOffers(List<Long> offersId);
}
