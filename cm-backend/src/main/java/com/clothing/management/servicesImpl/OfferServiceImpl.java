package com.clothing.management.servicesImpl;

import com.clothing.management.dto.ModelQuantity;
import com.clothing.management.dto.OfferModelQuantitiesDTO;
import com.clothing.management.dto.OfferModelsDTO;
import com.clothing.management.entities.Model;
import com.clothing.management.entities.Offer;
import com.clothing.management.entities.OfferModel;
import com.clothing.management.entities.Product;
import com.clothing.management.repository.IOfferModelRepository;
import com.clothing.management.repository.IOfferRepository;
import com.clothing.management.services.OfferService;
import com.clothing.management.services.PacketService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;

@Service
public class OfferServiceImpl implements OfferService {
    private static final Logger LOGGER = LoggerFactory.getLogger(PacketService.class);
    @Autowired
    IOfferRepository offerRepository;

    @Autowired
    IOfferModelRepository offerModelRepository;

    @Autowired
    IOfferModelRepository fbPageRepository;

    @Override
    public List<OfferModelsDTO> findAllOffers() throws IOException {
        Map<Offer, List<OfferModel>> offerListMap = offerModelRepository.findAll()
                .stream()
                .collect(groupingBy(OfferModel::getOffer));

        List<OfferModelsDTO> offerModelsListDTO = new ArrayList<>();
        OfferModelsDTO offerModelsDTO = null;

        for (Offer offer : offerListMap.keySet()) {
            offerModelsDTO = new OfferModelsDTO(offer.getId(), offer.getName() , offer.getPrice() , offer.isEnabled());
            List<OfferModel> offerModels= offerListMap.get(offer);
            List<Model> models = new ArrayList<>();
            for(OfferModel offerModel : offerModels) {
                if(offerModel.getQuantity() >= 1) {
                    for (int i = 0; i < offerModel.getQuantity(); i++) {
                        models.add(mapToModel(offerModel.getModel()));
                    }
                }
            }
            offerModelsDTO.setModels(models);
            offerModelsListDTO.add(offerModelsDTO);
        }
        return offerModelsListDTO;
    }

    @Override
    public List<OfferModelQuantitiesDTO> findAllOffersModelQuantities() throws IOException {
        Map<Offer, List<OfferModel>> offerListMap = offerModelRepository.findAll()
                .stream()
                .collect(groupingBy(OfferModel::getOffer));

        List<OfferModelQuantitiesDTO> offerModelListDTO = new ArrayList<>();
        OfferModelQuantitiesDTO offerModelDTO = null;

        for (Offer offer : offerListMap.keySet()) {
            offerModelDTO = new OfferModelQuantitiesDTO(offer.getId(), offer.getName() , offer.getPrice() , offer.getFbPages(), offer.isEnabled());
            List<OfferModel> offerModels= offerListMap.get(offer);
            List<ModelQuantity> modelQuantities = new ArrayList<>();
            for(OfferModel offerModel : offerModels) {
                Model model = mapToModel(offerModel.getModel());
                ModelQuantity modelQuantity = new ModelQuantity(offerModel.getQuantity() , model);
                modelQuantities.add(modelQuantity);
            }
            offerModelDTO.setModelQuantities(modelQuantities);
            offerModelListDTO.add(offerModelDTO);
        }
        return offerModelListDTO;
    }

    private Model mapToModel(Model model) throws IOException {
        Model newModel = new Model();
        newModel.setId(model.getId());
        newModel.setColors(model.getColors());
        newModel.setSizes(model.getSizes());
        newModel.setDescription(model.getDescription());
        newModel.setName(model.getName());
        newModel.setReference(model.getReference());
        newModel.setPurchasePrice(model.getPurchasePrice());
        newModel.setEarningCoefficient(model.getEarningCoefficient());
        newModel.setProducts(model.getProducts().stream().map(this::mapToProduct).collect(Collectors.toList()));
        if(model.getImage() != null)
            newModel.setBytes(Files.readAllBytes(new File(model.getImage().getImagePath()).toPath()));
        return newModel;
    }

    private Product mapToProduct(Product product) {
        Product newProduct = new Product();
        newProduct.setId(product.getId());
        newProduct.setColor(product.getColor());
        newProduct.setSize(product.getSize());
        //newProduct.setReference(product.getReference());
        newProduct.setQuantity(product.getQuantity());
        newProduct.setDate(product.getDate());
        return newProduct;
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
    public OfferModelQuantitiesDTO addOffer(OfferModelQuantitiesDTO offerModelDTO) {

        Offer offer = new Offer(offerModelDTO.getName() ,offerModelDTO.getFbPages(), offerModelDTO.getPrice(), offerModelDTO.isEnabled());
        Offer offerResult = offerRepository.save(offer);

        for(ModelQuantity modelQuantity: offerModelDTO.getModelQuantities()){
            offerModelRepository.addOfferModel(offerResult.getId() , modelQuantity.getModel().getId() , modelQuantity.getQuantity());
        }
        offerModelDTO.setOfferId(offerResult.getId());
        return offerModelDTO;
    }

    @Override
    public OfferModelQuantitiesDTO updateOffer(OfferModelQuantitiesDTO offerModelDTO) {
        // Update the offer
        Optional<Offer> offer = offerRepository.findById(offerModelDTO.getOfferId());
        Offer offerResult = null;
        if(offer.isPresent()){
           Offer offerToUpdate =  offer.get();
           offerToUpdate.setName(offerModelDTO.getName());
           offerToUpdate.setPrice(offerModelDTO.getPrice());
           offerToUpdate.setEnabled(offerModelDTO.isEnabled());
           offerToUpdate.setFbPages(offerModelDTO.getFbPages());
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

    @Override
    public List<OfferModelsDTO> findOfferByFbPageId(Long fbPageId) throws IOException {
        Map<Offer, List<OfferModel>> offerListMap = offerModelRepository.findByOfferId(fbPageId)
                .stream()
                .collect(groupingBy(OfferModel::getOffer));

        List<OfferModelsDTO> offerModelsListDTO = new ArrayList<>();
        OfferModelsDTO offerModelsDTO = null;

        for (Offer offer : offerListMap.keySet()) {
            offerModelsDTO = new OfferModelsDTO(offer.getId(), offer.getName() , offer.getPrice() , offer.isEnabled());
            List<OfferModel> offerModels= offerListMap.get(offer);
            List<Model> models = new ArrayList<>();
            for(OfferModel offerModel : offerModels) {
                if(offerModel.getQuantity() >= 1) {
                    for (int i = 0; i < offerModel.getQuantity(); i++) {
                        models.add(mapToModel(offerModel.getModel()));
                    }
                }
            }
            offerModelsDTO.setModels(models);
            offerModelsListDTO.add(offerModelsDTO);
        }
        return offerModelsListDTO;
    }
}
