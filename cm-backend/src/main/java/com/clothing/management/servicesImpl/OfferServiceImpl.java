package com.clothing.management.servicesImpl;
import com.clothing.management.dto.OfferDTO;
import com.clothing.management.dto.OfferModelsDTO;
import com.clothing.management.entities.*;
import com.clothing.management.repository.IOfferModelRepository;
import com.clothing.management.repository.IOfferRepository;
import com.clothing.management.services.OfferService;
import com.clothing.management.services.PacketService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;

@Service
@Transactional("tenantTransactionManager")
public class OfferServiceImpl implements OfferService {
    private static final Logger LOGGER = LoggerFactory.getLogger(PacketService.class);
    @Autowired
    IOfferRepository offerRepository;

    @Autowired
    IOfferModelRepository offerModelRepository;

    @Autowired
    IOfferModelRepository fbPageRepository;

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public void setDeletedByIds(List<Long> ids) {
        offerRepository.setDeletedByIds(ids);
    }

    @Override
    public List<OfferDTO> getOffers(){
        List<OfferDTO> offerListMap = offerRepository.findAll()
                .stream()
                .map(OfferDTO::new)
                .collect(Collectors.toList());
        return offerListMap;
    }

    public List<OfferDTO> getOffersByPacketId(Long id){
        List<OfferDTO> offerListMap = offerRepository.findById(id)
                .stream()
                .map(OfferDTO::new)
                .collect(Collectors.toList());
        return offerListMap;
    }

    @Transactional("tenantTransactionManager")
    @Override
    public List<OfferModel> findOfferByFbPageId(Long fbPageId) {
        return offerModelRepository.findOffersByFbPageId(fbPageId);
    }


    private Model mapToModel(Model model) throws IOException {
        Model newModel = new Model();
        newModel.setId(model.getId());
        newModel.setColors(model.getColors());
        newModel.setSizes(model.getSizes());
        newModel.setDescription(model.getDescription());
        newModel.setName(model.getName());
        newModel.setPurchasePrice(model.getPurchasePrice());
        newModel.setEarningCoefficient(model.getEarningCoefficient());
        newModel.setProducts(model.getProducts().stream().map(this::mapToProduct).collect(Collectors.toList()));
        /*if(model.getImage() != null)
            newModel.setBytes(Files.readAllBytes(new File(model.getImage().getImagePath()).toPath()));*/
        return newModel;
    }

    private Product mapToProduct(Product product) {
        Product newProduct = new Product();
        newProduct.setId(product.getId());
        newProduct.setColor(product.getColor());
        newProduct.setSize(product.getSize());
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
    public OfferDTO addOffer(OfferDTO offerDTO) {

        Offer offer = new Offer(offerDTO.getName() ,offerDTO.getFbPages(), offerDTO.getPrice(), offerDTO.isEnabled(), false);
        Offer offerResult = offerRepository.save(offer);

        offerDTO.getOfferModels().stream()
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
    public OfferDTO updateOffer(Offer offer) throws Exception {
        offerRepository.save(offer);
        return new OfferDTO(offer);
    }

    @Transactional("tenantTransactionManager")
    @Override
    public OfferDTO updateOfferData(long id, String name, double price,boolean enabled) throws Exception {
        // Update the offer
        Offer offerX = offerRepository.findById(id)
                .orElseThrow( () -> new Exception("Offer not found with ID: " + id));

        offerX.setName(name);
        offerX.setPrice(price);
        offerX.setEnabled(enabled);
        offerRepository.save(offerX);
        return new OfferDTO(offerX);
    }
    @Override
    @Transactional("tenantTransactionManager")
    public OfferDTO updateOfferModels(long offerId, Set<OfferModelsDTO> modelQuantityList) throws Exception {
        Set<OfferModel> oldOfferModels = offerModelRepository.findByOfferId(offerId);
        if(oldOfferModels.size() > 0 )
            offerModelRepository.deleteByOfferId(offerId);
        for(OfferModelsDTO offerModelsDTO: modelQuantityList){
            offerModelRepository.addOfferModel(offerId , offerModelsDTO.getModel().getId() , offerModelsDTO.getQuantity());}

        Offer offer = offerRepository.findById(offerId)
                .orElseThrow(() -> new Exception("Offer not found with ID: " + offerId));
        return new OfferDTO(offer);
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
