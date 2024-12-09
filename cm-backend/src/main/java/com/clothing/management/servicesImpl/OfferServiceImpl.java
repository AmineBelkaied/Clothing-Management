package com.clothing.management.servicesImpl;
import com.clothing.management.dto.ModelDeleteDTO;
import com.clothing.management.dto.OfferDTO;
import com.clothing.management.dto.OfferModelsDTO;
import com.clothing.management.dto.OfferRequest;
import com.clothing.management.entities.*;
import com.clothing.management.exceptions.custom.notfound.OfferNotFoundException;
import com.clothing.management.mappers.OfferMapper;
import com.clothing.management.repository.*;
import com.clothing.management.services.FbPageService;
import com.clothing.management.services.OfferService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.clothing.management.utils.EntityBuilderHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.DecimalFormat;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional("tenantTransactionManager")
public class OfferServiceImpl implements OfferService {

    private final IOfferRepository offerRepository;
    private final IOfferModelRepository offerModelRepository;
    private final OfferMapper offerMapper;
    private final EntityBuilderHelper entityBuilderHelper;
    private static final Logger LOGGER = LoggerFactory.getLogger(OfferServiceImpl.class);
    private final IProductsPacketRepository productsPacketRepository;
    private final IFbPageRepository fbPageRepository;

    public OfferServiceImpl(IOfferRepository offerRepository,
                            IOfferModelRepository offerModelRepository,
                            IModelRepository modelRepository,
                            OfferMapper offerMapper,
                            EntityBuilderHelper entityBuilderHelper,
                            IProductsPacketRepository productsPacketRepository,
                            IFbPageRepository fbPageRepository) {
        this.offerRepository = offerRepository;
        this.offerModelRepository = offerModelRepository;
        this.productsPacketRepository = productsPacketRepository;
        this.offerMapper = offerMapper;
        this.entityBuilderHelper = entityBuilderHelper;
        this.fbPageRepository = fbPageRepository;
    }

    @Override
    public void setDeletedByIds(List<Long> ids) {
        offerRepository.softDeletedByIds(ids);
        LOGGER.info("Deleted status set for offers with ids: {}", ids);
    }

    @Override
    public List<OfferDTO> getOffers() {
        List<OfferDTO> offers = offerRepository.findAll()
                .stream()
                .map(offerMapper::toDto)
                .collect(Collectors.toList());
        LOGGER.info("Retrieved {} offers.", offers.size());
        return offers;
    }

    @Transactional("tenantTransactionManager")
    @Override
    public List<OfferModel> findOfferByFbPageId(Long fbPageId) {
        List<OfferModel> offerModels = offerModelRepository.findOffersByFbPageId(fbPageId);
        LOGGER.info("Retrieved {} offer models for fbPageId: {}", offerModels.size(), fbPageId);
        return offerModels;
    }

    @Override
    public Optional<Offer> findOfferById(Long idOffer) {
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
        List<FbPage> fbPages = fbPageRepository.findAllById(offerRequest.getFbPages());
        Offer offer = entityBuilderHelper
                .createOfferBuilder(offerRequest.getName(), fbPages, offerRequest.getPrice(), offerRequest.isEnabled(), false)
                .build();
        Offer savedOffer = offerRepository.save(offer);
        Set<OfferModel> offerModels = new HashSet<>();
        offerRequest.getOfferModels().forEach(offerModel -> {
                    offerModelRepository.addOfferModel(
                            savedOffer.getId(),
                            offerModel.getModel().getId(),
                            offerModel.getQuantity()
                    );
                    OfferModel addedOfferModel = offerModelRepository.findOfferModelByModelIdAndOfferId(offerModel.getModel().getId(), savedOffer.getId());
                    offerModels.add(addedOfferModel);
                }
        );
        savedOffer.setOfferModels(offerModels);
        LOGGER.info("Offer added with id: {}", savedOffer.getId());
        return offerMapper.toDto(offer);
    }


    @Transactional("tenantTransactionManager")
    @Override
    public OfferDTO updateOfferFbPages(long offerId, Set<Long> fbPagesId) throws Exception {
        Offer offer = offerRepository.findById(offerId)
                .orElseThrow(() -> {
                    LOGGER.error("Offer with id: {} not found for update.", offerId);
                    return new OfferNotFoundException(offerId);
                });
        List<FbPage> fbPages  = fbPagesId.stream().map(fbPageId ->
            FbPage.builder().id(fbPageId).build()
        ).collect(Collectors.toList());
        offer.setFbPages(fbPages);
        Offer updatedOffer = offerRepository.save(offer);
        LOGGER.info("Updated fbPages for offerId: {}.", offerId);
        return offerMapper.toDto(updatedOffer);
    }

    @Transactional("tenantTransactionManager")
    @Override
    public OfferDTO updateOffer(Offer offer) {
        Offer updatedOffer = offerRepository.save(offer);
        LOGGER.info("Offer updated with id: {}", updatedOffer.getId());
        return offerMapper.toDto(updatedOffer);
    }

    @Transactional("tenantTransactionManager")
    @Override
    public OfferDTO updateOfferData(long id, String name, double price, boolean isEnabled) throws Exception {
        Offer offer = offerRepository.findById(id)
                .orElseThrow(() -> {
                    LOGGER.error("Offer with id: {} not found for update.", id);
                    return new OfferNotFoundException(id, name);
                });

        offer.setName(name);
        if(price != offer.getPrice()){
            offer.setPrice(price);
            correctPacketPrice(id);
        }

        offer.setEnabled(isEnabled);
        Offer updatedOffer = offerRepository.save(offer);
        LOGGER.info("Offer data updated for id: {}.", id);
        return offerMapper.toDto(updatedOffer);
    }
    private int correctPacketPrice(long offerId){
        return productsPacketRepository.updatePacketsByOfferId(offerId);
    }

    @Override
    @Transactional("tenantTransactionManager")
    public OfferDTO updateOfferModels(long offerId, Set<OfferModelsDTO> modelQuantityList) {
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
        return offerMapper.toDto(updatedOffer);
    }

    @Override
    public void deleteOffer(Offer offer) {
        offerRepository.delete(offer);
        LOGGER.info("Deleted offer with id: {}", offer.getId());
    }

    @Override
    public void deleteOfferById(Long offerId, boolean isSoftDelete) {
        if(isSoftDelete) {
            offerRepository.softDeletedByIds(List.of(offerId));
        } else {
            offerRepository.deleteById(offerId);
        }
    }

    @Override
    public long checkOfferUsage(Long offerId) {
        return productsPacketRepository.countProductsPacketByOfferId(offerId);
    }

    @Override
    public void deleteSelectedOffers(List<Long> offersId) {
        offerRepository.deleteAllById(offersId);
        LOGGER.info("Deleted selected offers with ids: {}", offersId);
    }

    @Override
    public void rollBackOffer(Long id) {
        offerRepository.rollBackOffer(id);
        LOGGER.info("Offer with ID: {} rolled back.", id);
    }
}
