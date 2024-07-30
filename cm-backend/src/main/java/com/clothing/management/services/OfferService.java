package com.clothing.management.services;
import com.clothing.management.dto.OfferDTO;
import com.clothing.management.dto.OfferModelsDTO;
import com.clothing.management.entities.FbPage;
import com.clothing.management.entities.Offer;
import com.clothing.management.entities.OfferModel;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface OfferService {

    //public List<OfferModelsDTO> findAllOffers() throws IOException;
    //public List<OfferModelQuantitiesDTO> findAllOffersModelQuantities() throws IOException;
    //public OfferModelQuantitiesDTO findOffersModelQuantitiesById(Long id) throws IOException;
    public List<OfferDTO> getOffers() throws IOException;
    public List<OfferModel> findOfferByFbPageId(Long fbPageId) throws IOException;

    public Optional<Offer> findOfferById(Long idOffer);
    public Offer findOfferByName(String name);
    public OfferDTO addOffer(OfferDTO offerDTO);
    public OfferDTO updateOffer(Offer offer) throws Exception;
    public OfferDTO updateOfferData(long id, String name, double price,boolean enabled) throws Exception;
    public OfferDTO updateOfferFbPages(long offerId,Set<FbPage> fbPages) throws Exception;
    public OfferDTO updateOfferModels(long offerId, Set<OfferModelsDTO> modelQuantityList) throws Exception;

    public void deleteOffer(Offer offer);
    void deleteSelectedOffers(List<Long> offersId);

    public void setDeletedByIds(List<Long> ids);

}
