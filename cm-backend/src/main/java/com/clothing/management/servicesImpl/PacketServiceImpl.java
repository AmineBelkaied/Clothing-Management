package com.clothing.management.servicesImpl;
import com.clothing.management.dto.*;
import com.clothing.management.enums.DeliveryCompany;
import com.clothing.management.enums.DiggieStatus;
import com.clothing.management.enums.FirstStatus;
import com.clothing.management.servicesImpl.api.FirstApiService;
import com.clothing.management.entities.*;
import com.clothing.management.repository.*;
import com.clothing.management.services.PacketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.bind.annotation.PathVariable;

import java.awt.*;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.*;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.mapping;

@Service
public class PacketServiceImpl implements PacketService {

    @Autowired
    IPacketRepository packetRepository;

    @Autowired
    IProductRepository productRepository;

    @Autowired
    IProductsPacketRepository productsPacketRepository;

    @Autowired
    IOfferRepository offerRepository;

    @Autowired
    IModelRepository modelRepository;

    @Autowired
    IColorRepository colorRepository;

    @Autowired
    ISizeRepository sizeRepository;
    @Autowired
    IPacketStatusRepository packetStatusRepository;

    @Autowired
    FirstApiService firstApiService;

    @Override
    public List<Packet> findAllPackets() {
        List<Packet> sortedPackets = packetRepository.findAll().stream()
                .sorted(Comparator.comparingLong(Packet::getId).reversed())
                .collect(Collectors.toList());
        return sortedPackets;
    }

    @Override
    public List<Packet> findAllTodaysPackets() {
        return packetRepository.findAllTodayPackets();
    }

    @Override
    public List<Packet> findAllDiggiePackets() {
        return packetRepository.findAllDiggiePackets();
    }

    @Override
    public List<Packet> findAllPacketsByDate(Date date) {
        return packetRepository.findAllByDate(date);
    }

    @Override
    public Optional<Packet> findPacketById(Long idPacket) {
        return packetRepository.findById(idPacket);
    }

    @Override
    public Packet addPacket(Packet packet) {
        return packetRepository.save(packet);
    }

    @Override
    public Packet updatePacket(Packet packet) {
        return packetRepository.save(packet);
    }
    @Override
    public Packet patchPacket(Long idPacket, Map<String, Object> field) throws IOException {
        Optional<Packet> optionalPacket = packetRepository.findById(idPacket);
        Packet packet = null;
        if (optionalPacket.isPresent()) {
            packet = optionalPacket.get();
            Optional<String> firstKeyOptional = field.keySet().stream().findFirst();
            if (firstKeyOptional.isPresent()) {
                String firstKey = firstKeyOptional.get();
                System.out.println("firstKey : " + firstKey);
                Field fieldPacket = ReflectionUtils.findField(Packet.class, (String) firstKey);
                fieldPacket.setAccessible(true);
                System.out.println("fieldPacket : " + fieldPacket.toString());
                System.out.println("field.get(firstKey) : " + field.get(firstKey));
                ReflectionUtils.setField(fieldPacket, packet, field.get(firstKey));
                if (firstKey.equals("status")) {
                    if (field.get(firstKey).equals("Confirmée") || field.get(firstKey).equals("Retour reçu") || field.get(firstKey).equals("Echange")) {
                        if(field.get(firstKey).equals("Echange"))packet.setExchange(true);
                        if (!field.get(firstKey).equals("Retour reçu"))
                            createBarCode(packet, DeliveryCompany.FIRST.toString());
                        System.out.println("packet confirmé ou echange: " + packet);
                        updateProductQuantity(packet, field, firstKey);
                    }
                    // updatePacketStatus
                    updatePacketStatus(idPacket, String.valueOf(field.get(firstKey)));
                    packet.setLastUpdateDate(new Date());
                }
                updatePacket(packet);
            }
        }
        return packet;
    }

    private void updateProductQuantity(Packet packet,  Map<String, Object> field,String firstKey) {
        List<ProductsPacket> productsPackets = productsPacketRepository.findByPacketId(packet.getId());
        for (ProductsPacket productsPacket : productsPackets) {
            Optional<Product> product = productRepository.findById(productsPacket.getProduct().getId());
            if (product.isPresent()) {
                if (field.get(firstKey).equals("Confirmée") || field.get(firstKey).equals("Echange")) {
                    product.get().setQuantity(product.get().getQuantity() - 1);
                } else {
                    product.get().setQuantity(product.get().getQuantity() + 1);
                }
                product.get().setDate(new Date());
                productRepository.save(product.get());
            }
            // add the products of the packet inside historic table
            // productsPacketRepository.save(new ProductsPacket(product, packet, new Date()));
        }
    }


    @Override
    public void addProductsToPacket(SelectedProductsDTO selectedProductsDTO) {
        List<ProductOfferDTO> productsOffers = selectedProductsDTO.getProductsOffers();
        Optional<Packet> optionalPacket = packetRepository.findById(selectedProductsDTO.getIdPacket());
        if (optionalPacket.isPresent()) {
            Packet packet = optionalPacket.get();
            packet.setPrice(selectedProductsDTO.getTotalPrice());
            packet.setDeliveryPrice(selectedProductsDTO.getDeliveryPrice());
            packet.setDiscount(selectedProductsDTO.getDiscount());
            List<ProductsPacket> existingProductsPacket = productsPacketRepository.findByPacketId(packet.getId());
            if(existingProductsPacket.size() > 0)
                productsPacketRepository.deleteAll(existingProductsPacket);
            List<ProductsPacket> newProductsPacket = new ArrayList<>();
            productsOffers.forEach(productOfferDTO -> {
                newProductsPacket.add(new ProductsPacket(new Product(productOfferDTO.getProductId()), packet, new Date(), new Offer(productOfferDTO.getOfferId()), productOfferDTO.getPacketOfferIndex()));
            });
            productsPacketRepository.saveAll(newProductsPacket);
            packet.setPacketDescription(selectedProductsDTO.getPacketDescription());
            packetRepository.save(packet);
        }
    }

    public PacketDTO findPacketRelatedProducts(Long idPacket) {
        PacketDTO packetDTO = new PacketDTO();
        List<OfferUpdateDTO> offerUpdateDTOList = new ArrayList<>();
        Optional<Packet> optionalPacket = packetRepository.findById(idPacket);
        if(optionalPacket.isPresent()) {
            packetDTO.setTotalPrice(optionalPacket.get().getPrice());
            packetDTO.setDeliveryPrice(optionalPacket.get().getDeliveryPrice());
            packetDTO.setDiscount(optionalPacket.get().getDiscount());
            List<ProductsPacket> productsPackets = productsPacketRepository.findByPacketId(optionalPacket.get().getId());
            if(productsPackets.size() > 0) {
                Map<Integer, List<ProductsPacket>> offerListMap = productsPackets.stream()
                        .collect(groupingBy(ProductsPacket::getPacketOfferId));
                offerListMap.forEach((offer, productsPacket) -> {
                    Offer firstOffer = productsPacket.get(0).getOffer();
                    OfferUpdateDTO offerUpdateDTO = new OfferUpdateDTO(firstOffer.getId(), firstOffer.getName(), firstOffer.getPrice(), firstOffer.isEnabled(), productsPacket.stream().map(productPacket -> mapToProduct(productPacket.getProduct())).collect(Collectors.toList()));
                    offerUpdateDTOList.add(offerUpdateDTO);
                });
            }
            packetDTO.setOfferUpdateDTOList(offerUpdateDTOList);
        }
        return packetDTO;
    }

    private Product mapToProduct(Product product) {
        Product newProduct = new Product();
        newProduct.setId(product.getId());
        newProduct.setColor(product.getColor());
        newProduct.setSize(product.getSize());
        newProduct.setReference(product.getReference());
        newProduct.setModel(mapToModel(product.getModel()));
        newProduct.setQuantity(product.getQuantity());
        newProduct.setDate(product.getDate());
        return newProduct;
    }

    private Model mapToModel(Model model) {
        Model newModel = new Model();
        newModel.setId(model.getId());
        newModel.setColors(model.getColors());
        newModel.setSizes(model.getSizes());
        newModel.setDescription(model.getDescription());
        newModel.setName(model.getName());
        newModel.setReference(model.getReference());
        return newModel;
    }

    @Override
    public void deletePacketById(Long idPacket) {
        Optional<Packet> optionalPacket = packetRepository.findById(idPacket);
        Packet packet = null;
        if (optionalPacket.isPresent()) {
            packet = optionalPacket.get();
            packet.setStatus("Supprimé");
            updatePacket(packet);
            updatePacketStatus(idPacket, "Supprimé");
        }
        //packetRepository.deleteById(idPacket);
    }

    /**
     * Delete selected packets by id
     *
     * @param packetsId
     */
    @Override
    public void deleteSelectedPackets(List<Long> packetsId) {
        for (Long packetId : packetsId) {

            // Retrieve the packet with the given ID (Assuming you have a method to fetch the packet by ID)
            Optional<Packet> optionalPacket = packetRepository.findById(packetId);
            Packet packet = null;
            if (optionalPacket.isPresent()) {
                packet= optionalPacket.get();
                if((packet.getCustomerPhoneNb() == null || packet.getCustomerPhoneNb()== "") &&
                        (packet.getPacketReference() == null || packet.getPacketReference() ==""))
                    packetRepository.deleteById(packetId);
                else {
                    packet.setStatus("Supprimé");
                    updatePacket(packet);
                }
            }
        }
    }

    @Override
    public void updatePacketStatus(Long idPacket, String status) {
        Optional<Packet> optionalPacket = packetRepository.findById(idPacket);
        Packet packet = null;
        if (optionalPacket.isPresent()) {
            packet = optionalPacket.get();
            PacketStatus packetStatus = new PacketStatus();
            packetStatus.setPacket(packet);
            packetStatus.setStatus(status);
            packetStatus.setDate(new Date());
            packetStatusRepository.save(packetStatus);
        }
    }

    @Override
    public List<PacketStatus> findPacketStatusById(Long idPacket) {
        Optional<Packet> optionalPacket = packetRepository.findById(idPacket);
        if (optionalPacket.isPresent()) {
            Packet packet = optionalPacket.get();
            return packet.getPacketStatus();
        }
        return null;
    }

    @Override
    public DeliveryResponseFirst createBarCode(Packet packet, String deliveryCompany) throws IOException {
        if(deliveryCompany.equals(DeliveryCompany.FIRST.toString())) {
            DeliveryResponseFirst deliveryResponse = this.firstApiService.createBarCode(packet);
            //System.out.println("PSIdeliveryResponse.toString()"+deliveryResponse.toString());
            if(deliveryResponse.getResponseCode() == 200 || deliveryResponse.getResponseCode() == 201 ) {
                if(!deliveryResponse.isError()){
                    packet.setPrintLink(deliveryResponse.getResult().getLink());
                    packet.setBarcode(deliveryResponse.getResult().getBarCode());
                    packet.setDate(new Date());
                    //System.out.println("PSIpacket barcode"+packet);
                    packetRepository.save(packet);
                }
                return deliveryResponse;
            }
        }
        return null;
    }

    @Override
    public Packet getLastStatus(Packet packet, String deliveryCompany) throws Exception {
        if(deliveryCompany.equals(DeliveryCompany.FIRST.toString())) {
            try {
                DeliveryResponseFirst deliveryResponse = this.firstApiService.getLastStatus(packet.getBarcode());
                System.out.println("PSIdeliveryResponse"+deliveryResponse.toString());

                if (deliveryResponse.getResponseCode() == 200 || deliveryResponse.getResponseCode() == 201 || deliveryResponse.getResponseCode() == 404) {
                    String diggieStatus = DiggieStatus.A_VERIFIER.getStatus();
                    System.out.println("PSIdeliveryResponse.getStatus()"+deliveryResponse.getStatus());
                    if (deliveryResponse.getStatus()==404 || deliveryResponse.getResult().getState() == null || deliveryResponse.getResult().getState().equals("")) {
                        packet.setStatus(DiggieStatus.A_VERIFIER.getStatus());
                        packet.setLastDeliveryStatus("Code à barre incorrect");
                        diggieStatus = DiggieStatus.A_VERIFIER.getStatus();
                        System.out.println("PSI404");
                    }else if (deliveryResponse.getStatus()>199) {
                        System.out.println("PSI>199");
                        diggieStatus = mapFirstToDiggieStatus(deliveryResponse.getResult().getState());
                        packet.setLastDeliveryStatus(deliveryResponse.getResult().getState());
                        System.out.println("deliveryResponse.getEtat() : " + deliveryResponse.getResult().getState());
                        System.out.println("packet.getStatus() : " + packet.getStatus());
                        /*if(packet.getStatus().equals(DiggieStatus.EXCHANGE.getStatus()))
                            packet.setStatus(
                                    !diggieStatus.equals(DiggieStatus.RETOUR_RECU.getStatus()) && !diggieStatus.equals(DiggieStatus.RETOUR_EXPEDITEUR.getStatus())
                                            ? DiggieStatus.EXCHANGE.getStatus() : diggieStatus
                            );
                        else */

                        if(
                                !packet.getStatus().equals(DiggieStatus.RETOUR_RECU.getStatus())
                                        || !packet.getStatus().equals(DiggieStatus.PAYEE.getStatus())
                                        || !packet.getStatus().equals(DiggieStatus.RETOUR_EXPEDITEUR.getStatus())
                        ) {
                            packet.setStatus(
                                    diggieStatus.equals(DiggieStatus.EN_COURS_1.getStatus())
                                            ? upgradeInProgressStatus(packet) : diggieStatus
                            );
                            System.out.println("packet.getStatus() : " + packet.getStatus());
                        }
                    }
                    saveLastStatusToHistory(packet, diggieStatus);
                    packet.setLastUpdateDate(new Date());
                    return packetRepository.save(packet);
                }
            }catch (Exception e ){

                System.out.println("exeption code a barre incorrect" + packet + "ex: "+ e);
                packet.setStatus(DiggieStatus.A_VERIFIER.getStatus());
                packet.setLastDeliveryStatus("Code à barre incorrect");
                packet.setLastUpdateDate(new Date());
                return packetRepository.save(packet);

                //throw new Exception("Problem lors de l'excution first "+ e.getMessage());
            }
        }
        return null;
    }

    private void saveLastStatusToHistory(Packet packet, String status) {
        PacketStatus packetStatus = new PacketStatus();
        packetStatus.setPacket(packet);
        packetStatus.setStatus(status);
        packetStatus.setDate(new Date());
        packetStatusRepository.save(packetStatus);
    }


    private String mapFirstToDiggieStatus(String status) {
        if (status == null || status.equals(""))
            return DiggieStatus.A_VERIFIER.getStatus();
        FirstStatus firstStatus = FirstStatus.fromString(status);
        switch (firstStatus) {
            case LIVREE:
                return DiggieStatus.LIVREE.getStatus();
            case EXCHANGE:
                return DiggieStatus.EXCHANGE.getStatus();
            case RETOUR_EXPEDITEUR:
                return DiggieStatus.RETOUR_EXPEDITEUR.getStatus();
            case RETOUR_DEFINITIF:
            case RETOUR_CLIENT_AGENCE:
                return DiggieStatus.RETOUR.getStatus();
            case EN_ATTENTE:
                return DiggieStatus.CONFIRMEE.getStatus();
            case A_VERIFIER:
                return DiggieStatus.A_VERIFIER.getStatus();
            case AU_MAGASIN:
                return DiggieStatus.EN_COURS_1.getStatus();
        }
        return DiggieStatus.EN_COURS_1.getStatus();
    }

    private String upgradeInProgressStatus(Packet packet) {
        DiggieStatus diggieStatus = DiggieStatus.fromString(packet.getStatus());
        System.out.println("upgradeInProgressStatus packet.getStatus(): " + packet.getStatus());
        //if (packet.getStatus().equals(DiggieStatus.AU_MAGASIN.getStatus()))return DiggieStatus.EN_COURS_1.getStatus();
        if (checkSameDateStatus(packet) && !packet.getStatus().equals(DiggieStatus.CONFIRMEE.getStatus()))
            return packet.getStatus();
        switch (diggieStatus) {
            case CONFIRMEE:
            case EXCHANGE:
                return DiggieStatus.EN_COURS_1.getStatus();
            case EN_COURS_1:
                return DiggieStatus.EN_COURS_2.getStatus();
            case EN_COURS_2:
                return DiggieStatus.EN_COURS_3.getStatus();
            default:
                return packet.getStatus();
        }
    }

    private boolean checkSameDateStatus(Packet packet) {
        Date date = new Date();
        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(packet.getLastUpdateDate());
        int day1 = cal1.get(Calendar.DAY_OF_YEAR);
        int year1 = cal1.get(Calendar.YEAR);
        Calendar cal2 = Calendar.getInstance();
        cal2.setTime(date);
        int day2 = cal2.get(Calendar.DAY_OF_YEAR);
        int year2 = cal2.get(Calendar.YEAR);
        return (day1 == day2) && (year1 == year2);
    }

    public void addProductsPackets() {
        List<Packet> packets = packetRepository.findAll();
        packets.stream().forEach(packet -> {
            if(packet.getPacketReference() != null) {
                String[] offers = packet.getPacketReference().split("-");
                for(int i=0 ; i<offers.length ; i++) {
                    String[] offerProducts = offers[i].split(":");
                    if(offerProducts.length > 1) {
                        String[] products = offerProducts[1].split(",");
                        for(int j=0 ; j < products.length ;j++) {
                            Optional<Offer> offer = offerRepository.findById(Long.parseLong(offerProducts[0]));
                            if(offer.isPresent()) {
                                ProductsPacket productsPacket = new ProductsPacket();
                                productsPacket.setOffer(offer.get());
                                productsPacket.setProduct(productRepository.findByReference(products[j]));
                                productsPacket.setPacket(packet);
                                productsPacket.setPacketDate(packet.getDate());
                                productsPacket.setPacketOfferId(i);
                                productsPacketRepository.save(productsPacket);
                            }
                        }
                    }
                }
            }
        });
    }
    public Packet duplicatePacket(Long idPacket) {
        Packet packet = packetRepository.findById(idPacket).get();
        Packet newPacket = new Packet();
        if(packet != null) {
            newPacket.setCustomerName(packet.getCustomerName() + "   echange id: " + packet.getId());
            newPacket.setCustomerPhoneNb(packet.getCustomerPhoneNb());
            newPacket.setAddress(packet.getAddress());
            newPacket.setRelatedProducts(packet.getRelatedProducts());
            newPacket.setPacketDescription(packet.getPacketDescription());
            newPacket.setPrice(packet.getPrice());
            newPacket.setDate(new Date());
            newPacket.setStatus("Non confirmée");
            newPacket.setFbPage(packet.getFbPage());
            newPacket.setCity(packet.getCity());
            newPacket.setExchange(true);
        }
        Packet response = packetRepository.save(newPacket);
        List<ProductsPacket> productsPackets = productsPacketRepository.findByPacketId(packet.getId());
        if(productsPackets.size()>0) {
            productsPackets.stream().forEach(productsPacket -> {
                ProductsPacket newProductsPacket = new ProductsPacket(productsPacket.getProduct(), response,productsPacket.getPacketDate(), productsPacket.getOffer(), productsPacket.getPacketOfferId());
                productsPacketRepository.save(newProductsPacket);
            });
        }
        return response;
    }
}
