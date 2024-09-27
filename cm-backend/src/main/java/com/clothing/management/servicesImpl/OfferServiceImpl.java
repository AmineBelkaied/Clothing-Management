package com.clothing.management.servicesImpl;
import com.clothing.management.dto.OfferDTO;
import com.clothing.management.dto.OfferModelsDTO;
import com.clothing.management.entities.*;
import com.clothing.management.exceptions.custom.notfound.OfferNotFoundException;
import com.clothing.management.mappers.OfferMapper;
import com.clothing.management.repository.IOfferModelRepository;
import com.clothing.management.repository.IOfferRepository;
import com.clothing.management.services.OfferService;
import com.clothing.management.utils.EntityBuilderHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional("tenantTransactionManager")
public class OfferServiceImpl implements OfferService {

    private final IOfferRepository offerRepository;
    private final IOfferModelRepository offerModelRepository;
    private final OfferMapper offerMapper;
    private final EntityBuilderHelper entityBuilderHelper;

    public OfferServiceImpl(IOfferRepository offerRepository, IOfferModelRepository offerModelRepository, OfferMapper offerMapper, EntityBuilderHelper entityBuilderHelper) {
        this.offerRepository = offerRepository;
        this.offerModelRepository = offerModelRepository;
        this.offerMapper = offerMapper;
        this.entityBuilderHelper = entityBuilderHelper;
    }

    @Override
    public void setDeletedByIds(List<Long> ids) {
        offerRepository.setDeletedByIds(ids);
    }

    @Override
    public List<OfferDTO> getOffers(){
        return offerRepository.findAll()
                .stream()
                .map(offerMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional("tenantTransactionManager")
    @Override
    public List<OfferModel> findOfferByFbPageId(Long fbPageId) {
        return offerModelRepository.findOffersByFbPageId(fbPageId);
    }

    @Override
    public Optional<Offer> findOfferById(Long idOffer) {
        return offerRepository.findById(idOffer);
    }

    @Override
    public Offer findOfferByName(String name) {
        return offerRepository.findByName(name);
    }

    @Override
    public OfferDTO addOffer(OfferDTO offerDTO) {
        Offer offer = entityBuilderHelper
                .createOfferBuilder(offerDTO.getName(), offerDTO.getFbPages(), offerDTO.getPrice(), offerDTO.isEnabled(), false)
                .build();
        Offer offerResult = offerRepository.save(offer);

        offerDTO.getOfferModels()
                .forEach(offerModel ->
                        offerModelRepository.addOfferModel(
                                offerResult.getId(),
                                offerModel.getModel().getId(),
                                offerModel.getQuantity()
                        )
                );
        return offerMapper.toDto(offer);
    }

    @Transactional("tenantTransactionManager")
    @Override
    public OfferDTO updateOfferFbPages(long offerId,Set<FbPage> fbPages) throws Exception {
        Offer offer = offerRepository.findById(offerId)
                .orElseThrow( () -> new OfferNotFoundException(offerId));
        offer.setFbPages(fbPages);
        offerRepository.save(offer);
        return offerMapper.toDto(offer);
    }

    @Transactional("tenantTransactionManager")
    @Override
    public OfferDTO updateOffer(Offer offer) {
        offerRepository.save(offer);
        return offerMapper.toDto(offer);
    }

    @Transactional("tenantTransactionManager")
    @Override
    public OfferDTO updateOfferData(long id, String name, double price,boolean enabled) throws Exception {
        Offer offer = offerRepository.findById(id)
                .orElseThrow( () -> new OfferNotFoundException(id, name));

        offer.setName(name);
        offer.setPrice(price);
        offer.setEnabled(enabled);
        offerRepository.save(offer);
        return offerMapper.toDto(offer);
    }
    @Override
    @Transactional("tenantTransactionManager")
    public OfferDTO updateOfferModels(long offerId, Set<OfferModelsDTO> modelQuantityList) {
        Set<OfferModel> oldOfferModels = offerModelRepository.findByOfferId(offerId);
        if(!oldOfferModels.isEmpty())
            offerModelRepository.deleteByOfferId(offerId);
        for(OfferModelsDTO offerModelsDTO: modelQuantityList){
            offerModelRepository.addOfferModel(offerId , offerModelsDTO.getModel().getId() , offerModelsDTO.getQuantity());}

        return offerMapper.toDto(offerRepository.findById(offerId)
                .orElseThrow(() -> new OfferNotFoundException(offerId)));
    }

    @Override
    public void deleteOffer(Offer offer) {
        offerRepository.delete(offer);
    }

    /**
     * Delete selected offers by id.
     *
     * @param offersId a list of offer IDs to be deleted
     */
    @Override
    public void deleteSelectedOffers(List<Long> offersId) {
        offerRepository.deleteAllById(offersId);
    }
}
