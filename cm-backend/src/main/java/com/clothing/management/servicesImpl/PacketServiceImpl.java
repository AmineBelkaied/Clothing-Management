package com.clothing.management.servicesImpl;
import com.clothing.management.dto.*;
import com.clothing.management.enums.DeliveryCompany;
import com.clothing.management.enums.DiggieStatus;
import com.clothing.management.enums.FirstStatus;
import com.clothing.management.repository.repositoryImpl.PacketRepositoryImpl;
import com.clothing.management.servicesImpl.api.FirstApiService;
import com.clothing.management.entities.*;
import com.clothing.management.repository.*;
import com.clothing.management.services.PacketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.ReflectionUtils;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.time.LocalDate;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;

@Service
public class PacketServiceImpl implements PacketService {

    private final IPacketRepository packetRepository;
    private final IProductRepository productRepository;
    private final IProductsPacketRepository productsPacketRepository;
    private final IOfferRepository offerRepository;
    private final IModelRepository modelRepository;
    private final IColorRepository colorRepository;
    private final ISizeRepository sizeRepository;
    private final IPacketStatusRepository packetStatusRepository;
    private final FirstApiService firstApiService;
    private final PacketRepositoryImpl packetRepositoryImpl;
    @Autowired
    public PacketServiceImpl(
            IPacketRepository packetRepository,
            IProductRepository productRepository,
            IProductsPacketRepository productsPacketRepository,
            IOfferRepository offerRepository,
            IModelRepository modelRepository,
            IColorRepository colorRepository,
            ISizeRepository sizeRepository,
            IPacketStatusRepository packetStatusRepository,
            FirstApiService firstApiService,
            PacketRepositoryImpl packetRepositoryImpl
    ) {
        this.packetRepository = packetRepository;
        this.productRepository = productRepository;
        this.productsPacketRepository = productsPacketRepository;
        this.offerRepository = offerRepository;
        this.modelRepository = modelRepository;
        this.colorRepository = colorRepository;
        this.sizeRepository = sizeRepository;
        this.packetStatusRepository = packetStatusRepository;
        this.firstApiService = firstApiService;
        this.packetRepositoryImpl = packetRepositoryImpl;
    }
    @Override
    public List<Packet> findAllPackets() {
        List<Packet> sortedPackets = packetRepository.findAll().stream()
                .sorted(Comparator.comparingLong(Packet::getId).reversed())
                .collect(Collectors.toList());
        return sortedPackets;
    }

    public Page<Packet> findAllPackets(String searchText, String startDate, String endDate, String status, Pageable pageable) {
        return packetRepositoryImpl.findAllPackets(searchText, startDate, endDate, status, pageable);
    }

    public List<Packet> findAllPacketsByDate(String startDate, String endDate) {
        return packetRepositoryImpl.findAllPacketsByDate(startDate, endDate);
    }
    @Override
    public Page<Packet> findAllTodaysPackets(Pageable pageable) {
        return packetRepository.findAllTodayPackets(pageable);
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
        System.out.println("save Packey:"+packet);
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
                Field fieldPacket = ReflectionUtils.findField(Packet.class, (String) firstKey);
                fieldPacket.setAccessible(true);
                System.out.println("firstKey:"+firstKey+"/field.get(firstKey:)"+field.get(firstKey));

                if (firstKey.equals("status")) {
                    if (field.get(firstKey).equals(DiggieStatus.CONFIRMEE.getStatus()) || field.get(firstKey).equals(DiggieStatus.RETOUR_RECU.getStatus()) || field.get(firstKey).equals(DiggieStatus.RETOUR_EXCHANGE.getStatus())) {
                        if (field.get(firstKey).equals(DiggieStatus.CONFIRMEE.getStatus()))
                            createBarCode(packet, DeliveryCompany.FIRST.toString());
                        return updateProductsQuantityByPacket(packet,String.valueOf(field.get(firstKey)));
                    }else updatePacketStatus(packet, String.valueOf(field.get(firstKey)));
                }else {
                    ReflectionUtils.setField(fieldPacket, packet, field.get(firstKey));
                    updatePacket(packet);
                }
            }
        }
        return packet;
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
                newProductsPacket.add(new ProductsPacket(new Product(productOfferDTO.getProductId()), packet, new Date(), new Offer(productOfferDTO.getOfferId()), productOfferDTO.getPacketOfferIndex(),0));
            });
            productsPacketRepository.saveAll(newProductsPacket);
            packet.setPacketDescription(selectedProductsDTO.getPacketDescription());
            updatePacket(packet);
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
                    OfferUpdateDTO offerUpdateDTO = new OfferUpdateDTO(firstOffer.getId(), firstOffer.getName(), firstOffer.getPrice(), firstOffer.isEnabled(), productsPacket.stream().map(productPacket -> {
                        try {
                            return mapToProduct(productPacket.getProduct());
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }).collect(Collectors.toList()));
                    offerUpdateDTOList.add(offerUpdateDTO);
                });
            }
            packetDTO.setOfferUpdateDTOList(offerUpdateDTOList);
        }
        return packetDTO;
    }

    private Product mapToProduct(Product product) throws IOException {
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

    private Model mapToModel(Model model) throws IOException {
        Model newModel = new Model();
        newModel.setId(model.getId());
        newModel.setColors(model.getColors());
        newModel.setSizes(model.getSizes());
        newModel.setDescription(model.getDescription());
        newModel.setName(model.getName());
        newModel.setReference(model.getReference());
        if(model.getImage() != null)
            newModel.setBytes(Files.readAllBytes(new File(model.getImage().getImagePath()).toPath()));
        return newModel;
    }

    @Override
    public void deletePacketById(Long idPacket) {
        Optional<Packet> optionalPacket = packetRepository.findById(idPacket);
        if (optionalPacket.isPresent()) {
            updatePacketStatusAndSaveToHistory(optionalPacket.get(),DiggieStatus.DELETED.getStatus());
        }
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
                if(packet.getCustomerPhoneNb() == null || packet.getCustomerPhoneNb()== "")
                    packetRepository.deleteById(packetId);
                else {
                    updatePacketStatusAndSaveToHistory(packet,DiggieStatus.DELETED.getStatus());
                }
            }
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
            if(deliveryResponse.getResponseCode() == 200 || deliveryResponse.getResponseCode() == 201 ) {
                if(!deliveryResponse.isError()){
                    packet.setPrintLink(deliveryResponse.getResult().getLink());
                    packet.setBarcode(deliveryResponse.getResult().getBarCode());
                    packet.setDate(new Date());
                    updatePacket(packet);
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

                if (deliveryResponse.getResponseCode() == 200 || deliveryResponse.getResponseCode() == 201 || deliveryResponse.getResponseCode() == 404) {
                    String diggieStatus = DiggieStatus.A_VERIFIER.getStatus();
                    if (deliveryResponse.getStatus()==404 || deliveryResponse.getResult().getState() == null || deliveryResponse.getResult().getState().equals("")) {
                        throw new Exception("Problem API First");
                    }else if (deliveryResponse.getStatus()>199) {
                        diggieStatus = mapFirstToDiggieStatus(deliveryResponse.getResult().getState());
                        packet.setLastDeliveryStatus(deliveryResponse.getResult().getState());
                        diggieStatus =
                                diggieStatus.equals(DiggieStatus.EN_COURS_1.getStatus())//First always return "en cours"
                                        || diggieStatus.equals(DiggieStatus.EN_COURS_2.getStatus())//not in First System
                                        || diggieStatus.equals(DiggieStatus.EN_COURS_3.getStatus())//not in First System
                                        ? upgradeInProgressStatus(packet) : diggieStatus;

                    }
                    return updatePacketStatus(packet, diggieStatus);
                }
            }catch (Exception e ){
                packet.setLastDeliveryStatus(DiggieStatus.INCORRECT_BARCODE.getStatus());
                return updatePacketStatusAndSaveToHistory(packet, DiggieStatus.A_VERIFIER.getStatus());
            }
        }
        return null;
    }

    private String mapFirstToDiggieStatus(String status) {
        if (status == null || status.equals(""))
            return DiggieStatus.A_VERIFIER.getStatus();
        FirstStatus firstStatus = FirstStatus.fromString(status);
        switch (firstStatus) {
            case LIVREE:
                return DiggieStatus.LIVREE.getStatus();
            case RETOUR_EXPEDITEUR:
                return DiggieStatus.RETOUR_EXPEDITEUR.getStatus();
            case RETOUR_DEFINITIF:
            case RETOUR_CLIENT_AGENCE:
                return DiggieStatus.RETOUR.getStatus();
            case EN_ATTENTE:
                return DiggieStatus.CONFIRMEE.getStatus();
            case A_VERIFIER:
                return DiggieStatus.A_VERIFIER.getStatus();
        }
        return DiggieStatus.EN_COURS_1.getStatus();
    }

    private String upgradeInProgressStatus(Packet packet) {
        DiggieStatus diggieStatus = DiggieStatus.fromString(packet.getStatus());
        if (checkSameDateStatus(packet))
            return packet.getStatus();
        switch (diggieStatus) {
            case EN_COURS_1:
                return DiggieStatus.EN_COURS_2.getStatus();
            case EN_COURS_2:
                return DiggieStatus.EN_COURS_3.getStatus();
            case EN_COURS_3:
                return DiggieStatus.A_VERIFIER.getStatus();
            default:
                return DiggieStatus.EN_COURS_1.getStatus();
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

    public Packet duplicatePacket(Long idPacket) {
        Packet packet = packetRepository.findById(idPacket).get();
        Packet newPacket = new Packet();
        if(packet != null) {
            newPacket.setCustomerName(packet.getCustomerName() + "   echange id: " + packet.getId());
            newPacket.setCustomerPhoneNb(packet.getCustomerPhoneNb());
            newPacket.setAddress(packet.getAddress());
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
                ProductsPacket newProductsPacket = new ProductsPacket(productsPacket.getProduct(), response,productsPacket.getPacketDate(), productsPacket.getOffer(), productsPacket.getPacketOfferId(),0);
                productsPacketRepository.save(newProductsPacket);
            });
        }
        return response;
    }

    public List<String> updatePacketsByBarCode(BarCodeStatusDTO barCodeStatusDTO) {
        List<String> errors = new ArrayList<>();
        System.out.println(barCodeStatusDTO);
        String newState = barCodeStatusDTO.getStatus();
        barCodeStatusDTO.getBarCodes().forEach(barCode -> {
            try {
                Optional<Packet> optionalPacket = packetRepository.findByBarCode(barCode);
                if(optionalPacket.isPresent() && !optionalPacket.get().getStatus().equals(DiggieStatus.RETOUR_RECU.getStatus())) {
                    if(newState.equals(DiggieStatus.PAYEE.getStatus())){
                        updatePacketStatus(optionalPacket.get(),DiggieStatus.PAYEE.getStatus());
                    }else
                        updateProductsQuantityByPacket(optionalPacket.get(),newState);
                    //updatePacketStatus(packet.get(),newState);
                } else {
                    errors.add(barCode);
                }
            } catch(Exception e){
                errors.add(barCode);
                e.printStackTrace();
            }
        });
        return errors;
    }
    public Long getExchangeId(Packet packet){
        Long id = packet.getId();
        int indexStartOfString = packet.getCustomerName().lastIndexOf("id: ");
        if (indexStartOfString != -1) {
            String idSubstring = packet.getCustomerName().substring(indexStartOfString + 4); // +4 to skip "id: "
            id = Long.valueOf(idSubstring.trim());
        }
        return id;
    }

    Packet updateProductsQuantityByPacket(Packet packet, String newState){
        //System.out.println("updateProductsQuantityByPacket packet"+packet);
        Long id= packet.getId();
        int quantity = 1;
        if (!newState.equals(DiggieStatus.CONFIRMEE.getStatus())){
            //System.out.println("else echange newState"+newState+" st:"+DiggieStatus.RETOUR_EXCHANGE.getStatus());

            if (newState.equals(DiggieStatus.RETOUR_EXCHANGE.getStatus())){
                if(!packet.getStatus().equals(DiggieStatus.PAYEE.getStatus()))
                    updatePacketStatusAndSaveToHistory(packet, DiggieStatus.LIVREE.getStatus());
                System.out.println("retour echange"+id);
                id = getExchangeId(packet);
                packet = packetRepository.findById(id).get();
                newState = DiggieStatus.RETOUR_RECU.getStatus();
            }
        }
        return updatePacketStatusAndSaveToHistory(packet, newState);
    }

    private void updateProductQuantity(Product product, int quantityChange) {
        product.setQuantity(product.getQuantity() + quantityChange);
        product.setDate(new Date());
        productRepository.save(product);
    }

    private Packet updatePacketStatus(Packet packet,String status){
        if (status.equals(DiggieStatus.PAYEE.getStatus())
                && packet.isExchange())
        {
            updatePacketStatusAndSaveToHistory(packet, status);
            updateExchangePacketStatusToPaid(packet);
        }
        if (status.equals(DiggieStatus.LIVREE.getStatus())
                && packet.isExchange())
        {
            updatePacketStatusAndSaveToHistory(packet, status);
            status = DiggieStatus.RETOUR.getStatus();
        }
        System.out.println("updatePacketStatus "+packet.getId());
        return updatePacketStatusAndSaveToHistory(packet, status);
    }

    private void updateExchangePacketStatusToPaid(Packet packet){
        Long id = getExchangeId(packet);
        Optional<Packet> optionalPacket = packetRepository.findById(id);
        packet.setPrice(optionalPacket.get().getPrice()+packet.getPrice());
        //optionalPacket.get().setStatus(DiggieStatus.RETOUR.getStatus());
        updatePacketStatusAndSaveToHistory(optionalPacket.get(), DiggieStatus.RETOUR.getStatus());

    }

    private Packet updatePacketStatusAndSaveToHistory(Packet packet, String status) {
        if (!packet.getStatus().equals(status) && !packet.getStatus().equals(DiggieStatus.RETOUR_RECU.getStatus())){
            savePacketStatusToHistory(packet,status);
            packet.setStatus(status);
            packet.setLastUpdateDate(new Date());
            return updatePacket(packet);
        }
        return packet;
    }

    private void savePacketStatusToHistory(Packet packet, String status) {
        if(status.equals(DiggieStatus.LIVREE.getStatus())||status.equals(DiggieStatus.PAYEE.getStatus())||status.equals(DiggieStatus.CONFIRMEE.getStatus())||status.equals(DiggieStatus.RETOUR_RECU.getStatus()))
            updateProductsStatusByPacket(packet.getId(),status);
        if (status.equals(DiggieStatus.RETOUR_RECU.getStatus())||status.equals(DiggieStatus.CONFIRMEE.getStatus()))
            updateProductsQuantity(packet.getId(),status);
        PacketStatus packetStatus = new PacketStatus();
        packetStatus.setPacket(packet);
        packetStatus.setStatus(status);
        packetStatus.setDate(new Date());
        packetStatusRepository.save(packetStatus);
    }

    public boolean updateProductsStatusByPacket(Long idPacket,String status) {
        int x = 0;
        if (status.equals(DiggieStatus.RETOUR_RECU.getStatus())) x = 0;
        if (status.equals(DiggieStatus.CONFIRMEE.getStatus())) x = 1;
        if (status.equals(DiggieStatus.LIVREE.getStatus())||status.equals(DiggieStatus.PAYEE.getStatus())) x = 2;

        List<ProductsPacket> productsPackets = productsPacketRepository.findByPacketId(idPacket);
        if(productsPackets.size() > 0) {
            for (ProductsPacket product : productsPackets) {
                product.setStatus(x);
                productsPacketRepository.save(product);
            };
        }else return false;
        return true;
    }


    public boolean updateProductsQuantity(Long idPacket,String status) {
        int quantity = 0;
        if (status.equals(DiggieStatus.RETOUR_RECU.getStatus())) quantity = 1;
        if (status.equals(DiggieStatus.CONFIRMEE.getStatus())) quantity = -1;
        List<ProductsPacket> productsPackets = productsPacketRepository.findByPacketId(idPacket);
        if(productsPackets.size() > 0) {
            for (ProductsPacket productsPacket : productsPackets) {
                Optional<Product> product = productRepository.findById(productsPacket.getProduct().getId());
                if (product.isPresent()) {
                    updateProductQuantity(product.get(), quantity);
                }
            }
        }else return false;
        return true;
    }


    @Override
    public void savePacketStatusToHistory(Long idPacket, String status) {//unused
        System.out.println("savePacketStatusToHistory "+idPacket);
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
    public List<ProductsDayCountDTO> productsCountByDate(Long state,String beginDate,String endDate){
        List<ProductsDayCountDTO> existingProductsPacket = productsPacketRepository.productsCountByDate(state, beginDate,endDate);
        return existingProductsPacket;
    }

}
