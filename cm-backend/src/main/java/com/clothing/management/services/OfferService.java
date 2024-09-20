package com.clothing.management.services;
import com.clothing.management.dto.OfferDTO;
import com.clothing.management.dto.OfferModelsDTO;
import com.clothing.management.dto.OfferRequest;
import com.clothing.management.entities.FbPage;
import com.clothing.management.entities.Offer;
import com.clothing.management.entities.OfferModel;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface OfferService {
    List<OfferDTO> getOffers();
    List<OfferModel> findOfferByFbPageId(Long fbPageId) throws IOException;
    Optional<Offer> findOfferById(Long idOffer);
    Offer findOfferByName(String name);
    //OfferDTO addOffer(OfferDTO offerDTO);
    OfferDTO updateOffer(Offer offer);
    OfferDTO updateOfferData(long id, String name, double price,boolean enabled) throws Exception;

    OfferDTO addOffer(OfferRequest offerDTO);

    OfferDTO updateOfferFbPages(long offerId, Set<FbPage> fbPages) throws Exception;
    OfferDTO updateOfferModels(long offerId, Set<OfferModelsDTO> modelQuantityList) throws Exception;
    void deleteOffer(Offer offer);
    void deleteSelectedOffers(List<Long> offersId);
    void setDeletedByIds(List<Long> ids);

}
