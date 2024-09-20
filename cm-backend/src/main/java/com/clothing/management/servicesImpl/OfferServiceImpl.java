package com.clothing.management.servicesImpl;

import com.clothing.management.dto.OfferDTO;
import com.clothing.management.dto.OfferModelsDTO;
import com.clothing.management.dto.OfferRequest;
import com.clothing.management.entities.*;
import com.clothing.management.exceptions.custom.notfound.OfferNotFoundException;
import com.clothing.management.repository.IOfferModelRepository;
import com.clothing.management.repository.IOfferRepository;
import com.clothing.management.services.OfferService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional("tenantTransactionManager")
public class OfferServiceImpl implements OfferService {

    private static final Logger LOGGER = LoggerFactory.getLogger(OfferServiceImpl.class);

    private final IOfferRepository offerRepository;
    private final IOfferModelRepository offerModelRepository;

    @Autowired
    public OfferServiceImpl(IOfferRepository offerRepository, IOfferModelRepository offerModelRepository) {
        this.offerRepository = offerRepository;
        this.offerModelRepository = offerModelRepository;
    }

    @Override
    public void setDeletedByIds(List<Long> ids) {
        LOGGER.info("Setting deleted status for offers with ids: {}", ids);
        offerRepository.setDeletedByIds(ids);
        LOGGER.info("Deleted status set for offers with ids: {}", ids);
    }

    @Override
    public List<OfferDTO> getOffers() {
        LOGGER.info("Fetching all offers.");
        List<OfferDTO> offers = offerRepository.findAll()
                .stream()
                .map(OfferDTO::new)
                .collect(Collectors.toList());
        LOGGER.info("Retrieved {} offers.", offers.size());
        return offers;
    }

    @Transactional("tenantTransactionManager")
    @Override
    public List<OfferModel> findOfferByFbPageId(Long fbPageId) {
        LOGGER.info("Fetching offers for fbPageId: {}", fbPageId);
        List<OfferModel> offerModels = offerModelRepository.findOffersByFbPageId(fbPageId);
        LOGGER.info("Retrieved {} offer models for fbPageId: {}", offerModels.size(), fbPageId);
        return offerModels;
    }

    @Override
    public Optional<Offer> findOfferById(Long idOffer) {
        LOGGER.info("Fetching offer with id: {}", idOffer);
        Optional<Offer> offer = offerRepository.findById(idOffer);
        if (offer.isPresent()) {
            LOGGER.info("Offer with id: {} found.", idOffer);
        } else {
            LOGGER.warn("Offer with id: {} not found.", idOffer);
        }
        return offer;
    }

    @Override
    public Offer findOfferByName(String name) {
        LOGGER.info("Fetching offer with name: {}", name);
        Offer offer = offerRepository.findByName(name);
        if (offer != null) {
            LOGGER.info("Offer with name: {} found.", name);
        } else {
            LOGGER.warn("Offer with name: {} not found.", name);
        }
        return offer;
    }

    @Override
    public OfferDTO addOffer(OfferRequest offerRequest) {
        LOGGER.info("Adding new offer: {}", offerRequest);
        Offer offer = new Offer(offerRequest.getName(), offerRequest.getFbPages(), offerRequest.getPrice(), offerRequest.isEnabled(), false);
        Offer savedOffer = offerRepository.save(offer);
        offerRequest.getOfferModels().forEach(offerModel ->
                offerModelRepository.addOfferModel(
                        savedOffer.getId(),
                        offerModel.getModel().getId(),
                        offerModel.getQuantity()
                )
        );
        offer = offerRepository.findById(savedOffer.getId())
                .orElseThrow(() -> {
                    LOGGER.error("Offer with id: {} not found for update.", savedOffer.getId());
                    return new OfferNotFoundException(savedOffer.getId());
                });
        LOGGER.info("Offer added with id: {}", savedOffer.getId());
        return new OfferDTO(offer);
    }

    @Transactional("tenantTransactionManager")
    @Override
    public OfferDTO updateOfferFbPages(long offerId, Set<FbPage> fbPages) throws Exception {
        LOGGER.info("Updating fbPages for offerId: {}", offerId);
        Offer offer = offerRepository.findById(offerId)
                .orElseThrow(() -> {
                    LOGGER.error("Offer with id: {} not found for update.", offerId);
                    return new OfferNotFoundException(offerId);
                });
        offer.setFbPages(fbPages);
        Offer updatedOffer = offerRepository.save(offer);
        LOGGER.info("Updated fbPages for offerId: {}.", offerId);
        return new OfferDTO(updatedOffer);
    }

    @Transactional("tenantTransactionManager")
    @Override
    public OfferDTO updateOffer(Offer offer) {
        LOGGER.info("Updating offer: {}", offer);
        Offer updatedOffer = offerRepository.save(offer);
        LOGGER.info("Offer updated with id: {}", updatedOffer.getId());
        return new OfferDTO(updatedOffer);
    }

    @Transactional("tenantTransactionManager")
    @Override
    public OfferDTO updateOfferData(long id, String name, double price, boolean enabled) throws Exception {
        LOGGER.info("Updating offer data for id: {}. New values - name: {}, price: {}, enabled: {}", id, name, price, enabled);
        Offer offer = offerRepository.findById(id)
                .orElseThrow(() -> {
                    LOGGER.error("Offer with id: {} not found for update.", id);
                    return new OfferNotFoundException(id, name);
                });

        offer.setName(name);
        offer.setPrice(price);
        offer.setEnabled(enabled);
        Offer updatedOffer = offerRepository.save(offer);
        LOGGER.info("Offer data updated for id: {}.", id);
        return new OfferDTO(updatedOffer);
    }

    @Override
    @Transactional("tenantTransactionManager")
    public OfferDTO updateOfferModels(long offerId, Set<OfferModelsDTO> modelQuantityList) {
        LOGGER.info("Updating models for offerId: {}", offerId);
        Set<OfferModel> oldOfferModels = offerModelRepository.findByOfferId(offerId);
        if (!oldOfferModels.isEmpty()) {
            offerModelRepository.deleteByOfferId(offerId);
            LOGGER.info("Deleted old offer models for offerId: {}", offerId);
        }
        for (OfferModelsDTO offerModelsDTO : modelQuantityList) {
            offerModelRepository.addOfferModel(offerId, offerModelsDTO.getModel().getId(), offerModelsDTO.getQuantity());
        }
        Offer updatedOffer = offerRepository.findById(offerId)
                .orElseThrow(() -> {
                    LOGGER.error("Offer with id: {} not found for update.", offerId);
                    return new OfferNotFoundException(offerId);
                });
        LOGGER.info("Updated offer models for offerId: {}", offerId);
        return new OfferDTO(updatedOffer);
    }

    @Override
    public void deleteOffer(Offer offer) {
        LOGGER.info("Deleting offer: {}", offer);
        offerRepository.delete(offer);
        LOGGER.info("Deleted offer with id: {}", offer.getId());
    }

    @Override
    public void deleteSelectedOffers(List<Long> offersId) {
        LOGGER.info("Deleting selected offers with ids: {}", offersId);
        offerRepository.deleteAllById(offersId);
        LOGGER.info("Deleted selected offers with ids: {}", offersId);
    }
}
