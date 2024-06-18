package com.clothing.management.servicesImpl;
import com.clothing.management.auth.util.SessionUtils;
import com.clothing.management.dto.*;
import com.clothing.management.enums.DeliveryCompanyStatus;
import com.clothing.management.enums.SystemStatus;
import com.clothing.management.models.DashboardCard;
import com.clothing.management.servicesImpl.api.FirstApiService;
import com.clothing.management.entities.*;
import com.clothing.management.repository.*;
import com.clothing.management.services.PacketService;
import com.clothing.management.servicesImpl.api.NavexApiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ReflectionUtils;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

import static com.clothing.management.enums.DeliveryCompanyStatus.RETOUR_DEPOT;
import static com.clothing.management.enums.DeliveryCompanyStatus.RETOUR_DEPOT_NAVEX;
import static com.clothing.management.enums.SystemStatus.*;
import static java.util.stream.Collectors.groupingBy;

@Service
public class PacketServiceImpl implements PacketService {

    private final IPacketRepository packetRepository;
    private final IProductRepository productRepository;
    private final IProductsPacketRepository productsPacketRepository;
    private final IPacketStatusRepository packetStatusRepository;
    private final FirstApiService firstApiService;
    private final NavexApiService navexApiService;

    private final SessionUtils sessionUtils;
    private final IGlobalConfRepository globalConfRepository;
    private final static List<String> ignoredDateStatusList = List.of(new String[]{ RETURN.getStatus(), NOT_CONFIRMED.getStatus(), UNREACHABLE.getStatus(), PROBLEM.getStatus(), TO_VERIFY.getStatus(), OOS.getStatus(), IN_PROGRESS_1.getStatus(), IN_PROGRESS_2.getStatus(), IN_PROGRESS_3.getStatus()});

    @Autowired
    public PacketServiceImpl(
            IPacketRepository packetRepository,
            IProductRepository productRepository,
            IProductsPacketRepository productsPacketRepository,
            IPacketStatusRepository packetStatusRepository,
            FirstApiService firstApiService,
            NavexApiService navexApiService,
            SessionUtils sessionUtils,
            IGlobalConfRepository globalConfRepository
    ) {
        this.packetRepository = packetRepository;
        this.productRepository = productRepository;
        this.productsPacketRepository = productsPacketRepository;
        this.packetStatusRepository = packetStatusRepository;
        this.globalConfRepository = globalConfRepository;
        this.firstApiService = firstApiService;
        this.navexApiService = navexApiService;
        this.sessionUtils = sessionUtils;
    }

    @Override
    public List<Packet> findAllPackets() {
        List<Packet> sortedPackets = packetRepository.findAll().stream()
                .sorted(Comparator.comparing(Packet::getDate).reversed())
                .collect(Collectors.toList());
        return sortedPackets;
    }

    @Override
    public Page<Packet> findAllPackets(Pageable pageable, String searchText, String startDate, String endDate, String status, boolean mandatoryDate) throws ParseException {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        if(status.equals("validation"))
            return packetRepository.findAllNotValidatedPackets(pageable);
        if(mandatoryDate) {
            if (searchText != null)
                return packetRepository.findAllPacketsByFieldAndDate(searchText,dateFormat.parse(startDate), dateFormat.parse(endDate), pageable);

            if (status != null)
            {
                if (status.equals("Tous"))return packetRepository.findAllPacketsByDate(dateFormat.parse(startDate), dateFormat.parse(endDate), pageable);
                return packetRepository.findAllPacketsByDateAndStatus(dateFormat.parse(startDate), dateFormat.parse(endDate), convertStatusToList(status), pageable);
            }

        } else {
            if (searchText != null)
                return packetRepository.findAllPacketsByField(searchText, pageable);
            if (startDate != null && status != null)
            {
                if (status.equals("Tous"))return packetRepository.findAllPacketsByDate(dateFormat.parse(startDate), dateFormat.parse(endDate), pageable);
                return packetRepository.findAllPacketsByStatus(ignoredDateStatusList, convertStatusToList(status),dateFormat.parse(startDate), dateFormat.parse(endDate), pageable);
            }
            if (status != null)
            {

                return packetRepository.findAllPacketsByStatus(convertStatusToList(status), pageable);
            }
        }

        return packetRepository.findAllPacketsByDate(dateFormat.parse(startDate), dateFormat.parse(endDate), pageable);
    }

    private List<String> convertStatusToList(String status) {
        return Arrays.asList(status.split(",", -1));
    }

    /*@Override
    public Page<Packet> findAllPackets(String searchText, String    startDate, String endDate, String status, Pageable pageable,mandatoryDate) {
        return null;
    }*/

    public List<Packet> findAllPacketsByDate(String startDate, String endDate) throws ParseException {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        return packetRepository.findAllPacketsByDate(dateFormat.parse(startDate), dateFormat.parse(endDate));
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

    @Transactional("tenantTransactionManager")
    @Override
    public int deleteEmptyPacket() {
        return packetRepository.deleteEmptyPacket();
    }


    @Override
    public Packet addPacket() {
        GlobalConf globalConf = globalConfRepository.findAll().stream().findFirst().orElse(null);
        System.out.println("global:"+globalConf);
        Packet packet =new Packet(globalConf.getDeliveryCompany());

        packetRepository.save(packet);
        savePacketStatusToHistory(packet,CREATION.getStatus());
        return packet;
    }
    @Override
    public Packet updatePacket(Packet packet) {
        return packetRepository.save(packet);
    }
    @Override
    public Packet updatePacketValid(String barCode,String type) {
        Optional<Packet> optionalPacket = packetRepository.findByBarCode(barCode);
        if (type.equals(CONFIRMED.getStatus())){
            optionalPacket.get().setValid(true);
            return packetRepository.save(optionalPacket.get());
        }
        return updatePacketStatus(optionalPacket.get(), RETURN_RECEIVED.getStatus());
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
                Field fieldPacket = ReflectionUtils.findField(Packet.class, firstKey);
                fieldPacket.setAccessible(true);
                //System.out.println("firstKey:"+firstKey+"/field.get(firstKey):"+field.get(firstKey));

                if (firstKey.equals("status")) {
                        if (field.get(firstKey).equals(CONFIRMED.getStatus()))
                            createBarCode(packet);
                    updatePacketStatus(packet, String.valueOf(field.get(firstKey)));
                }else {
                    if (firstKey.equals("customerPhoneNb")) {
                        int existCount =0;
                        if(field.get(firstKey) != "" && field.get(firstKey) != null)
                            existCount = checkPhone(field.get(firstKey)+"");
                        packet.setOldClient(existCount);
                        //System.out.println("nombre de reccurance phone: "+existCount);
                    }
                    ReflectionUtils.setField(fieldPacket, packet, field.get(firstKey));
                    updatePacket(packet);
                }
            }
        }
        return packet;
    }
    @Override
    public Packet addProductsToPacket(SelectedProductsDTO selectedProductsDTO,Integer stock) {
        String noStockStatus = selectedProductsDTO.getStatus();
        List<ProductOfferDTO> productsOffers = selectedProductsDTO.getProductsOffers();
        Optional<Packet> optionalPacket = packetRepository.findById(selectedProductsDTO.getIdPacket());
        if (optionalPacket.isPresent()) {
            Packet packet = optionalPacket.get();
            packet.setPrice(selectedProductsDTO.getTotalPrice());
            packet.setDeliveryPrice(selectedProductsDTO.getDeliveryPrice());
            packet.setDiscount(selectedProductsDTO.getDiscount());
            if (noStockStatus!= null && noStockStatus.equals(OOS.getStatus())
                    &&(packet.getStatus().equals(NOT_CONFIRMED.getStatus())
                    ||packet.getStatus().equals(NOTSERIOUS.getStatus())
                    ||packet.getStatus().equals(CANCELED.getStatus())
                    ||packet.getStatus().equals(UNREACHABLE.getStatus())))
                packet.setStatus(OOS.getStatus());
            if (noStockStatus!= null && (noStockStatus.equals(NOT_CONFIRMED.getStatus())||noStockStatus.equals(CANCELED.getStatus())))
                {
                    packet.setStatus(noStockStatus);
                }

            List<ProductsPacket> existingProductsPacket = productsPacketRepository.findByPacketId(packet.getId());
            if(existingProductsPacket.size() > 0)
                productsPacketRepository.deleteAll(existingProductsPacket);
            List<ProductsPacket> newProductsPacket = new ArrayList<>();
            productsOffers.forEach(productOfferDTO -> {
                newProductsPacket.add(new ProductsPacket(new Product(productOfferDTO.getProductId()), packet, new Date(), new Offer(productOfferDTO.getOfferId()), productOfferDTO.getPacketOfferIndex(),0));
            });
            productsPacketRepository.saveAll(newProductsPacket);
            packet.setPacketDescription(selectedProductsDTO.getPacketDescription());
            packet.setStock(stock);
            System.out.println("updateProduct"+packet);
            return updatePacket(packet);
        }
        return optionalPacket.get();
    }

    @Override
    public List<Packet> checkPacketProductsValidity(Long packetId) {
        System.out.println("checkPacketProductsValidity");
        List<ProductsPacket> existingProductsPacket = productsPacketRepository.findByPacketId(packetId);
        Integer qte = 50;
        boolean colorSizeFalse = false;
        Long lowestProductQte =0L;
        for (ProductsPacket productsPacket : existingProductsPacket) {
            if (productsPacket.getProduct() != null) {
                if(!colorSizeFalse) {
                    Optional<Product> product = productRepository.findById(productsPacket.getProduct().getId());
                    colorSizeFalse = product.get().getSize().getReference().equals("?") || product.get().getColor().getName().equals("?");
                    if (colorSizeFalse) qte = -1;
                    else {
                        Integer oldQte = qte;
                        qte = Math.min(qte, product.get().getQuantity());
                        if(!qte.equals(oldQte))lowestProductQte = product.get().getId();
                    }
                }
            }
        }
        if (qte < 0 && !colorSizeFalse)qte = 0;
        List<Long> updatedPacketList = new ArrayList<>();
        if (qte < 10) updatedPacketList = updateUnConfirmedStock(lowestProductQte, qte);
        return packetRepository.findAllById(updatedPacketList);
    }

    public List<Long> updateUnConfirmedStock(Long productId, Integer stock) {
        List<Long> productIds = productsPacketRepository.getUnconfirmedPacketStock_By_ProductId(productId);
        int count = 0;
        if(productIds.size()>0){
            count = productsPacketRepository.updateUnconfirmedPacketStock_By_ProductId(productIds,stock);
        }
        System.out.println(count+"updateUnConfirmedStock:"+productIds+" /id:"+productId+" /stock:"+stock);
        return productIds;
    }

    @Transactional("tenantTransactionManager")
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
        optionalPacket.ifPresent(packet -> updatePacketStatusAndSaveToHistory(packet, DELETED.getStatus()));
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
                if(packet.getCustomerPhoneNb() == null || packet.getCustomerPhoneNb().equals(""))
                    packetRepository.deleteById(packetId);
                else {
                    updatePacketStatusAndSaveToHistory(packet, DELETED.getStatus());
                }
            }
        }
    }

    @Override
    public int checkPhone(String phoneNumber) {
        return packetRepository.findAllPacketsByPhone_number(phoneNumber);
    }

    @Override
    public List<DashboardCard> createDashboard() {
        return packetRepository.createDashboard();
    }

    @Override
    public List<DashboardCard> syncNotification() {
        return packetRepository.createNotification();
    }
    @Override
    public List<PacketStatus> findPacketTimeLineById(Long idPacket) throws Exception {
        Packet packet = packetRepository.findById(idPacket)
                .orElseThrow(() -> new Exception("Packet not found!"));
        return packet.getPacketStatus();
    }
    @Override
    public DeliveryResponse createBarCode(Packet packet) throws IOException {
        DeliveryResponse deliveryResponse = new DeliveryResponse();
        if(packet.getDeliveryCompany().getName().equals("FIRST"))
            deliveryResponse = new DeliveryResponse(this.firstApiService.createBarCode(packet));
        else if(packet.getDeliveryCompany().getName().equals("NAVEX"))
            deliveryResponse = new DeliveryResponse(this.navexApiService.createBarCode(packet));
        System.out.println("deliveryResponse"+deliveryResponse);
            if(deliveryResponse.getResponseCode() == 200 || deliveryResponse.getResponseCode() == 201 ) {
                /*if(!deliveryResponse.isError()){*/
                    packet.setPrintLink(deliveryResponse.getLink());
                    packet.setBarcode(deliveryResponse.getBarCode());
                    packet.setDate(new Date());
                    updatePacket(packet);
                //}
                return deliveryResponse;
            }

        return null;
    }
    @Override
    public Packet getLastStatus(Packet packet) throws Exception {

        try {
            DeliveryResponse deliveryResponse;
            if(packet.getDeliveryCompany().getName().equals("FIRST"))
                deliveryResponse = new DeliveryResponse(this.firstApiService.getLastStatus(packet.getBarcode(),packet.getDeliveryCompany()));
            else
                deliveryResponse = new DeliveryResponse(this.navexApiService.getLastStatus(packet.getBarcode(),packet.getDeliveryCompany()));

            //System.out.println(deliveryResponse);
            if (deliveryResponse.getResponseCode() == 200 || deliveryResponse.getResponseCode() == 201 || deliveryResponse.getResponseCode() == 404) {
                String systemNewStatus = TO_VERIFY.getStatus();
                if (deliveryResponse.getStatus()==404  || deliveryResponse.getState().equals("")) {
                    throw new Exception("Problem API");
                }else if (deliveryResponse.getStatus()>199 && deliveryResponse.getState() != null) {
                    //Convert input from first to System Status
                    savePacketStatusToHistory(packet,packet.getDeliveryCompany().getName()+":"+deliveryResponse.getState());
                    systemNewStatus = mapDeliveryToSystemStatus(deliveryResponse.getState());
                    packet.setLastDeliveryStatus(deliveryResponse.getState());
                    systemNewStatus =
                            (systemNewStatus.equals(IN_PROGRESS_1.getStatus())//First always return "en cours"
                                    || systemNewStatus.equals(IN_PROGRESS_2.getStatus())//not in First System
                                    || systemNewStatus.equals(IN_PROGRESS_3.getStatus()))
                                    && !packet.getStatus().equals(PROBLEM.getStatus())//not in First System
                                    ? upgradeInProgressStatus(packet) : systemNewStatus;
                }
                return updatePacketStatus(packet, systemNewStatus);
            }
        } catch (Exception e ){
            //packet.setLastDeliveryStatus(INCORRECT_BARCODE.getStatus());
            //return updatePacketStatusAndSaveToHistory(packet, A_VERIFIER.getStatus());
            System.out.println("errorahmed"+e);
        }

        return null;
    }

    @Override
    public Packet addAttempt(Long packetId, String note) throws ParseException {
        Packet packet = packetRepository.findById(packetId).get();
        packet.setAttempt(packet.getAttempt() + 1);
        Date noteDate = new Date();

        // Formatting the note date to "dd hh:mm" format
        SimpleDateFormat sdf = new SimpleDateFormat("dd-HH:mm");
        String noteWithDate = "-Le "+sdf.format(noteDate) + " " + note;
        if(packet.getNote().equals("")){packet.setNote(noteWithDate);}
        else packet.setNote(String.format("%s\n%s", packet.getNote(), noteWithDate));
        //if(!packet.getStatus().equals(INJOIGNABLE) || packet.getStatus().equals(CANCELED))

        savePacketStatusToHistory(packet, "tentative: " + packet.getAttempt() + " " + note);
        return updatePacket(packet);
    }

    private String mapDeliveryToSystemStatus(String status) {
        if (status == null || status.equals(""))
            return TO_VERIFY.getStatus();
        DeliveryCompanyStatus deliveryCompanyStatus = DeliveryCompanyStatus.fromString(status);
        return switch (deliveryCompanyStatus) {
            case LIVREE, LIVRER, EXCHANGE -> LIVREE.getStatus();
            case RETOUR_EXPEDITEUR, RETOUR_EXPEDITEUR_NAVEX ,
                    RETOUR_DEFINITIF,RETOUR_DEFINITIF_NAVEX, RETOUR_CLIENT_AGENCE,
                    RETOUR_RECU, RETOUR_RECU_NAVEX -> RETURN.getStatus();
            case EN_ATTENTE -> CONFIRMED.getStatus();
            case A_VERIFIER, A_VERIFIER_NAVEX -> TO_VERIFY.getStatus();
            //case RETOUR_DEPOT,RETOUR_DEPOT_NAVEX -> status;
            default -> IN_PROGRESS_1.getStatus();
        };
    }
    private String upgradeInProgressStatus(Packet packet) {
        SystemStatus systemStatus = fromString(packet.getStatus());
        DeliveryCompanyStatus deliveryCompanyStatus = DeliveryCompanyStatus.fromString(packet.getLastDeliveryStatus());
        if ((checkSameDateStatus(packet)
                && !packet.getStatus().equals(CANCELED.getStatus()))||deliveryCompanyStatus.equals(RETOUR_DEPOT)||deliveryCompanyStatus.equals(RETOUR_DEPOT_NAVEX))
            return packet.getStatus();
        switch (systemStatus) {
            case IN_PROGRESS_1:
                return IN_PROGRESS_2.getStatus();
            case IN_PROGRESS_2:
                return IN_PROGRESS_3.getStatus();
            case IN_PROGRESS_3:
                return TO_VERIFY.getStatus();
            case TO_VERIFY:
                return mapDeliveryToSystemStatus(packet.getLastDeliveryStatus());
            default:
                return IN_PROGRESS_1.getStatus();
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
        GlobalConf globalConf = globalConfRepository.findAll().stream().findFirst().orElse(null);
        Packet packet = packetRepository.findById(idPacket).get();
        Packet newPacket = new Packet();

            newPacket.setCustomerName(packet.getCustomerName() + "   echange id: " + packet.getId());
            newPacket.setCustomerPhoneNb(packet.getCustomerPhoneNb());
            newPacket.setAddress(packet.getAddress());
            newPacket.setPacketDescription(packet.getPacketDescription());
            newPacket.setPrice(packet.getPrice());
            newPacket.setDate(new Date());
            newPacket.setStatus("Non confirmée");
            newPacket.setFbPage(packet.getFbPage());
            newPacket.setCity(packet.getCity());
            newPacket.setDeliveryPrice(packet.getDeliveryPrice());
            newPacket.setValid(false);
            newPacket.setStock(packet.getStock());
            newPacket.setDeliveryCompany(globalConf.getDeliveryCompany());
            newPacket.setExchangeId(packet.getId());

        Packet response = packetRepository.save(newPacket);
        List<ProductsPacket> productsPackets = productsPacketRepository.findByPacketId(packet.getId());
        if(productsPackets.size()>0) {
            productsPackets.stream().forEach(productsPacket -> {
                ProductsPacket newProductsPacket = new ProductsPacket(productsPacket.getProduct(), response,productsPacket.getPacketDate(), productsPacket.getOffer(), productsPacket.getPacketOfferId());
                productsPacketRepository.save(newProductsPacket);
            });
        }
        savePacketStatusToHistory(newPacket,CREATION.getStatus());
        return response;
    }
    public List<String> updatePacketsByBarCodes(BarCodeStatusDTO barCodeStatusDTO) {
        List<String> errors = new ArrayList<>();
        //System.out.println(barCodeStatusDTO);
        String newState = barCodeStatusDTO.getStatus();
        barCodeStatusDTO.getBarCodes().forEach(barCode -> {
            try {
                Optional<Packet> optionalPacket = packetRepository.findByBarCode(barCode);
                if(optionalPacket.isPresent()) {
                    if (!optionalPacket.get().getStatus().equals(RETURN_RECEIVED.getStatus())) {
                            updatePacketStatus(optionalPacket.get(), newState);
                    } else {
                        errors.add(barCode + " déja récu");
                    }
                }else { errors.add(barCode + " n'existe pas"); }
            } catch(Exception e){
                errors.add(barCode+ " erreur");
                e.printStackTrace();
            }
        });
        return errors;
    }
    private Packet updatePacketStatus(Packet packet,String status){
        if(packet.getExchangeId() != null){
            if (status.equals(PAID.getStatus())||status.equals(LIVREE.getStatus()))
                return updateExchangePacketStatusToPaid(packet,status);
            if (status.equals(RETURN_RECEIVED.getStatus())){
                return updateExchangePacketStatusToReturnReceived(packet);
            }
        }
        return updatePacketStatusAndSaveToHistory(packet, status);
    }
    private Packet updateExchangePacketStatusToReturnReceived(Packet packet){
        //Long id = getExchangeId(packet);
        Long id = packet.getExchangeId();
        Optional<Packet> optionalPacket = packetRepository.findById(id);
        if(packet.getStatus().equals(LIVREE.getStatus())
                ||packet.getStatus().equals(PAID.getStatus())
                ||optionalPacket.get().getStatus().equals(RETURN.getStatus()))
        return updatePacketStatusAndSaveToHistory(optionalPacket.get(), RETURN_RECEIVED.getStatus());
        else return updatePacketStatusAndSaveToHistory(packet, RETURN_RECEIVED.getStatus());
    }
    private Packet updateExchangePacketStatusToPaid(Packet packet,String status){
        //Long id = getExchangeId(packet);
        Long id = packet.getExchangeId();
        Optional<Packet> optionalPacket = packetRepository.findById(id);
        packet.setPrice(optionalPacket.get().getPrice()-optionalPacket.get().getDiscount()+packet.getPrice()-packet.getDiscount());
        if(!optionalPacket.get().getStatus().equals(RETURN_RECEIVED.getStatus()))
            updatePacketStatusAndSaveToHistory(optionalPacket.get(), RETURN.getStatus());
        return updatePacketStatusAndSaveToHistory(packet, status);
    }

    private Packet updatePacketStatusAndSaveToHistory(Packet packet, String status) {
        if (packet.getStatus() == null) {
            packet.setStatus(NOT_CONFIRMED.getStatus());
        }
        if (
            !packet.getStatus().equals(status)&&
            !(
                packet.getStatus().equals(RETURN.getStatus())&&
                !(
                    status.equals(RETURN_RECEIVED.getStatus())
                    || status.equals(PROBLEM.getStatus())
                )
            )
        ){
            updateProducts_Status(packet, status);
            updateProducts_Quantity(packet, status);
            savePacketStatusToHistory(packet,status);
            return  savePacketStatus(packet, status);
        }
        return updatePacket(packet);
    }

    private Packet savePacketStatus(Packet packet, String status) {
        packet.setStatus(status);
        packet.setLastUpdateDate(new Date());
        return updatePacket(packet);
    }

    private void savePacketStatusToHistory(Packet packet, String status) {
        PacketStatus packetStatus = new PacketStatus();
        packetStatus.setPacket(packet);
        packetStatus.setStatus(status);
        packetStatus.setDate(new Date());
        packetStatus.setUser(sessionUtils.getCurrentUser());
        packetStatusRepository.save(packetStatus);
    }
    private void updateProducts_Status(Packet packet,String status){
        if(status.equals(LIVREE.getStatus())
                ||status.equals(PAID.getStatus())
                ||status.equals(CONFIRMED.getStatus())
                ||status.equals(RETURN_RECEIVED.getStatus())
                ||status.equals(CANCELED.getStatus()))
            updateProductsPacket_Status_ByPacketId(packet,status);

    }
    private void updateProducts_Quantity(Packet packet,String status){
        if (status.equals(RETURN_RECEIVED.getStatus())
                ||status.equals(CONFIRMED.getStatus())
                ||status.equals(CANCELED.getStatus()))
            updateProductsQuantity(packet,status);
    }
    public void updateProductsPacket_Status_ByPacketId(Packet packet,String status) {
        int x = -1;
        if (status.equals(RETURN_RECEIVED.getStatus()) || status.equals(CANCELED.getStatus())) x = 0;
        if (status.equals(CONFIRMED.getStatus())) x = 1;
        if (status.equals(LIVREE.getStatus())||status.equals(PAID.getStatus())) x = 2;

        List<ProductsPacket> productsPackets = productsPacketRepository.findByPacketId(packet.getId());
        if(productsPackets.size() > 0) {
            for (ProductsPacket product : productsPackets) {
                product.setStatus(x);
                productsPacketRepository.save(product);
            }
        }
    }

    public void updateProductsQuantity(Packet packet,String status) {
        int quantity = 0;
        if (status.equals(RETURN_RECEIVED.getStatus()) || status.equals(CANCELED.getStatus())) quantity = 1;
        if (status.equals(CONFIRMED.getStatus())) quantity = -1;
        List<ProductsPacket> productsPackets = productsPacketRepository.findByPacketId(packet.getId());
        if(productsPackets.size() > 0) {
            for (ProductsPacket productsPacket : productsPackets) {
                Optional<Product> product = productRepository.findById(productsPacket.getProduct().getId());
                if (product.isPresent()) {
                    updateProductQuantity(product.get(), quantity);
                }
            }
        }
    }

    private void updateProductQuantity(Product product, int quantityChange) {

        product.setQuantity(product.getQuantity() + quantityChange);
        product.setDate(new Date());
        productRepository.save(product);
    }
}
