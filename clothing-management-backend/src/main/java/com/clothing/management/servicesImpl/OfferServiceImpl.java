package com.clothing.management.servicesImpl;

import com.clothing.management.dto.ModelQuantity;
import com.clothing.management.dto.OfferModelDTO;
import com.clothing.management.entities.Model;
import com.clothing.management.entities.Offer;
import com.clothing.management.entities.OfferModel;
import com.clothing.management.repository.IOfferModelRepository;
import com.clothing.management.repository.IOfferRepository;
import com.clothing.management.services.OfferService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;

@Service
public class OfferServiceImpl implements OfferService {

    @Autowired
    IOfferRepository offerRepository;

    @Autowired
    IOfferModelRepository offerModelRepository;

    @Override
    public List<OfferModelDTO> findAllOffers() {
        Map<Offer, List<OfferModel>> offerListMap = offerModelRepository.findAll()
                .stream()
                .collect(groupingBy(OfferModel::getOffer));

        List<OfferModelDTO> offerModelListDTO = new ArrayList<>();
        OfferModelDTO offerModelDTO = null;

        for (Offer offer : offerListMap.keySet()) {
            System.out.println("key: " + offer.getName());
            offerModelDTO = new OfferModelDTO(offer.getId(), offer.getName() , offer.getPrice() , offer.isEnabled());
            List<OfferModel> offerModels= offerListMap.get(offer);
            List<ModelQuantity> modelQuantities = new ArrayList<>();
            for(OfferModel offerModel : offerModels) {
                System.out.println("key: " + offer.getName());
                Model model = offerModel.getModel();
                System.out.println("getSize : " + model.getSize());
                int maxSizeIndex = model.sizes.indexOf(model.getSize());
                System.out.println("maxSizeIndex : " + maxSizeIndex);
                System.out.println("model : " + model.toString());
                List<String> sizes = new ArrayList<>();
                for(int i=0 ; i <= maxSizeIndex; i++)
                   sizes.add(model.sizes.get(i));
                System.out.println("sizes : " + sizes.toString());
                model.setSizes(sizes);
                ModelQuantity modelQuantity = new ModelQuantity(offerModel.getQuantity() , model);
                modelQuantities.add(modelQuantity);
            }
            offerModelDTO.setModelQuantities(modelQuantities);
            offerModelListDTO.add(offerModelDTO);
        }
        return offerModelListDTO;
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
    public OfferModelDTO addOffer(OfferModelDTO offerModelDTO) {
        Offer offer = new Offer(offerModelDTO.getName() , offerModelDTO.getPrice() , offerModelDTO.isEnabled());
        Offer offerResult = offerRepository.save(offer);
        for(ModelQuantity modelQuantity: offerModelDTO.getModelQuantities()){
            //OfferModel offerModel = new OfferModel(offer, modelQuantity.getModel(), modelQuantity.getQuantity());
            offerModelRepository.addOfferModel(offerResult.getId() , modelQuantity.getModel().getId() , modelQuantity.getQuantity());
        }
        offerModelDTO.setOfferId(offerResult.getId());
        return offerModelDTO;
    }

    @Override
    public OfferModelDTO updateOffer(OfferModelDTO offerModelDTO) {
        // Update the offer
        Optional<Offer> offer = offerRepository.findById(offerModelDTO.getOfferId());
        Offer offerResult = null;
        if(offer.isPresent()){
           Offer offerToUpdate =  offer.get();
           offerToUpdate.setName(offerModelDTO.getName());
           offerToUpdate.setPrice(offerModelDTO.getPrice());
           offerToUpdate.setEnabled(offerModelDTO.isEnabled());
           offerResult = offerRepository.save(offerToUpdate);
        }
        // Update the models of the offer and their quantities
        List<OfferModel> offerModels = offerModelRepository.findByOfferId(offerModelDTO.getOfferId());
        if(offerModels.size() > 0 )
            offerModelRepository.deleteAll(offerModels);
        for(ModelQuantity modelQuantity: offerModelDTO.getModelQuantities()){
            offerModelRepository.addOfferModel(offerResult.getId() , modelQuantity.getModel().getId() , modelQuantity.getQuantity());
        }
        offerModelDTO.setOfferId(offerResult.getId());
        return offerModelDTO;

    }

    @Override
    public void deleteOffer(Offer offer) {
        offerRepository.delete(offer);
    }

    /**
     * Delete selected offers by id
     * @param offersId
     */
    @Override
    public void deleteSelectedOffers(List<Long> offersId) {
        offerRepository.deleteAllById(offersId);
    }
}
