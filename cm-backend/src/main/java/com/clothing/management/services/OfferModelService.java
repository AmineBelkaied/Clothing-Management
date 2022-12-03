package com.clothing.management.services;

import com.clothing.management.entities.OfferModel;

import java.util.List;
import java.util.Optional;

public interface OfferModelService {

    public List<OfferModel> findAllOfferModel();
    public Optional<OfferModel> findOfferModelById(Long idOfferModel);
    public OfferModel addOfferModel(OfferModel offerModel);
    public OfferModel updateOfferModel(OfferModel offerModel);
    public void deleteOfferModelById(Long idOfferModel);
}
