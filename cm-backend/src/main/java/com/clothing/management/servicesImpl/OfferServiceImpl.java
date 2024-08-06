package com.clothing.management.servicesImpl;
import com.clothing.management.dto.OfferDTO;
import com.clothing.management.dto.OfferModelsDTO;
import com.clothing.management.entities.*;
import com.clothing.management.repository.IOfferModelRepository;
import com.clothing.management.repository.IOfferRepository;
import com.clothing.management.services.OfferService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional("tenantTransactionManager")
public class OfferServiceImpl implements OfferService {

    IOfferRepository offerRepository;
    IOfferModelRepository offerModelRepository;

    @Autowired
    public OfferServiceImpl(IOfferRepository offerRepository,IOfferModelRepository offerModelRepository) {
        this.offerRepository = offerRepository;
        this.offerModelRepository = offerModelRepository;
    }

    @Override
    public void setDeletedByIds(List<Long> ids) {
        offerRepository.setDeletedByIds(ids);
    }

    @Override
    public List<OfferDTO> getOffers(){
        return offerRepository.findAll()
                .stream()
                .map(OfferDTO::new)
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
        Offer offer = new Offer(offerDTO.getName() ,offerDTO.getFbPages(), offerDTO.getPrice(), offerDTO.isEnabled(), false);
        Offer offerResult = offerRepository.save(offer);

        offerDTO.getOfferModels()
                .forEach(offerModel ->
                        offerModelRepository.addOfferModel(
                                offerResult.getId(),
                                offerModel.getModel().getId(),
                                offerModel.getQuantity()
                        )
                );
        return new OfferDTO(offerResult);
    }

    @Transactional("tenantTransactionManager")
    @Override
    public OfferDTO updateOfferFbPages(long offerId,Set<FbPage> fbPages) throws Exception {
        Offer offer = offerRepository.findById(offerId)
                .orElseThrow( () -> new Exception("Offer not found with ID: " + offerId));
        offer.setFbPages(fbPages);
        offerRepository.save(offer);
        return new OfferDTO(offer);
    }

    @Transactional("tenantTransactionManager")
    @Override
    public OfferDTO updateOffer(Offer offer) {
        offerRepository.save(offer);
        return new OfferDTO(offer);
    }

    @Transactional("tenantTransactionManager")
    @Override
    public OfferDTO updateOfferData(long id, String name, double price,boolean enabled) throws Exception {
        // Update the offer
        Offer offer = offerRepository.findById(id)
                .orElseThrow( () -> new Exception("Offer not found with ID: " + id));

        offer.setName(name);
        offer.setPrice(price);
        offer.setEnabled(enabled);
        offerRepository.save(offer);
        return new OfferDTO(offer);
    }
    @Override
    @Transactional("tenantTransactionManager")
    public OfferDTO updateOfferModels(long offerId, Set<OfferModelsDTO> modelQuantityList) throws Exception {
        Set<OfferModel> oldOfferModels = offerModelRepository.findByOfferId(offerId);
        if(oldOfferModels.size() > 0 )
            offerModelRepository.deleteByOfferId(offerId);
        for(OfferModelsDTO offerModelsDTO: modelQuantityList){
            offerModelRepository.addOfferModel(offerId , offerModelsDTO.getModel().getId() , offerModelsDTO.getQuantity());}

        return new OfferDTO(offerRepository.findById(offerId)
                .orElseThrow(() -> new Exception("Offer not found with ID: " + offerId)));
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
