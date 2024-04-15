package com.clothing.management.servicesImpl;
import ch.qos.logback.core.net.SyslogOutputStream;
import com.clothing.management.dto.*;
//import com.clothing.management.repository.enums.DeliveryCompany;
import com.clothing.management.repository.enums.SystemStatus;
import com.clothing.management.repository.enums.FirstStatus;
import com.clothing.management.models.DashboardCard;
import com.clothing.management.repository.repositoryImpl.PacketRepositoryImpl;
import com.clothing.management.repository.repositoryImpl.PacketRepositoryOldImpl;
import com.clothing.management.services.GlobalConfService;
import com.clothing.management.servicesImpl.api.FirstApiService;
import com.clothing.management.entities.*;
import com.clothing.management.repository.*;
import com.clothing.management.services.PacketService;
import com.clothing.management.servicesImpl.api.NavexApiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ReflectionUtils;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.sql.ResultSet;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
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
    private final NavexApiService navexApiService;
    private final PacketRepositoryImpl packetRepositoryImpl;
    private final PacketRepositoryOldImpl packetRepositoryOld;
    private final UserRepository userRepository;
    private com.clothing.management.entities.DeliveryCompany defaultDeliveryCompany;
    @Autowired
    public IGlobalConfRepository globalConfRepository;
    private DeliveryCompany defaultDeliveryCompany;



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
            NavexApiService navexApiService,
            PacketRepositoryImpl packetRepositoryImpl,
            GlobalConfService globalConfService,
            PacketRepositoryOldImpl packetRepositoryOld,
            UserRepository userRepository,
            IGlobalConfRepository globalConfRepository
    ) {
        this.packetRepository = packetRepository;
        this.productRepository = productRepository;
        this.productsPacketRepository = productsPacketRepository;
        this.offerRepository = offerRepository;
        this.modelRepository = modelRepository;
        this.colorRepository = colorRepository;
        this.sizeRepository = sizeRepository;
        this.packetStatusRepository = packetStatusRepository;
        this.packetRepositoryImpl = packetRepositoryImpl;
        this.globalConfRepository = globalConfRepository;
        this.firstApiService = firstApiService;
        this.navexApiService = navexApiService;
       // this.defaultDeliveryCompany = globalConfServiceImpl.getGlobalConf().getDeliveryCompany();
        this.packetRepositoryOld = packetRepositoryOld;
        this.userRepository = userRepository;
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
        if (searchText != null)
            return packetRepository.findAllPacketsByField(searchText, pageable);
        if (startDate != null) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            if (status != null)
                return packetRepository.findAllPacketsByDateAndStatus(dateFormat.parse(startDate), dateFormat.parse(endDate), convertStatusToList(status), pageable);
            return packetRepository.findAllPacketsByDate(dateFormat.parse(startDate), dateFormat.parse(endDate), pageable);
        }
        if (status != null)
            return packetRepository.findAllPacketsByStatus(convertStatusToList(status), pageable);
        return packetRepository.findAll(pageable);
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
        savePacketStatusToHistory(packet,SystemStatus.CREATION.getStatus());
        return packet;
    }
    @Override
    public Packet updatePacket(Packet packet) {
        return packetRepository.save(packet);
    }
    @Override
    public Packet updatePacketValid(String barCode,String type) {
        Optional<Packet> optionalPacket = packetRepository.findByBarCode(barCode);
        if (type.equals(SystemStatus.CONFIRMEE.getStatus())){
            optionalPacket.get().setValid(true);
            return packetRepository.save(optionalPacket.get());
        }
        return updatePacketStatus(optionalPacket.get(), SystemStatus.RETOUR_RECU.getStatus());
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
                //System.out.println("firstKey:"+firstKey+"/field.get(firstKey):"+field.get(firstKey));

                if (firstKey.equals("status")) {
                        if (field.get(firstKey).equals(SystemStatus.CONFIRMEE.getStatus()))
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
            if (noStockStatus!= null && noStockStatus.equals(SystemStatus.ENDED.getStatus())
                    &&(packet.getStatus().equals(SystemStatus.NON_CONFIRMEE.getStatus())
                    ||packet.getStatus().equals(SystemStatus.NOTSERIOUS.getStatus())
                    ||packet.getStatus().equals(SystemStatus.CANCELED.getStatus())
                    ||packet.getStatus().equals(SystemStatus.INJOIGNABLE.getStatus())))
                packet.setStatus(SystemStatus.ENDED.getStatus());
            if (noStockStatus!= null && noStockStatus.equals(SystemStatus.NON_CONFIRMEE.getStatus()))
                packet.setStatus(SystemStatus.NON_CONFIRMEE.getStatus());

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
                        if(qte!=oldQte)lowestProductQte = product.get().getId();
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
        optionalPacket.ifPresent(packet -> updatePacketStatusAndSaveToHistory(packet, SystemStatus.DELETED.getStatus()));
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
                    updatePacketStatusAndSaveToHistory(packet, SystemStatus.DELETED.getStatus());
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
        System.out.println("deliveryResponse");
        System.out.println(deliveryResponse);
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
            //System.out.println("packet"+packet);
            DeliveryResponse deliveryResponse;
            if(packet.getDeliveryCompany().getName().equals("FIRST"))
                deliveryResponse = new DeliveryResponse(this.firstApiService.getLastStatus(packet.getBarcode(),packet.getDeliveryCompany()));
            else
                deliveryResponse = new DeliveryResponse(this.navexApiService.getLastStatus(packet.getBarcode(),packet.getDeliveryCompany()));
            System.out.println("deliveryResponse");
            System.out.println(deliveryResponse.toString());
            if (deliveryResponse.getResponseCode() == 200 || deliveryResponse.getResponseCode() == 201 || deliveryResponse.getResponseCode() == 404) {
                String systemNewStatus = SystemStatus.A_VERIFIER.getStatus();
                if (deliveryResponse.getStatus()==404 || deliveryResponse.getState() == null || deliveryResponse.getState().equals("")) {
                    throw new Exception("Problem API");
                }else if (deliveryResponse.getStatus()>199) {
                    //Convert input from first to System Status
                    //System.out.println(deliveryResponse);
                    savePacketStatusToHistory(packet,"First:"+deliveryResponse.getState());
                    systemNewStatus = mapFirstToSystemStatus(deliveryResponse.getState());
                    packet.setLastDeliveryStatus(deliveryResponse.getState());
                    systemNewStatus =
                            (systemNewStatus.equals(SystemStatus.EN_COURS_1.getStatus())//First always return "en cours"
                                    || systemNewStatus.equals(SystemStatus.EN_COURS_2.getStatus())//not in First System
                                    || systemNewStatus.equals(SystemStatus.EN_COURS_3.getStatus()))
                                    && !packet.getStatus().equals(SystemStatus.PROBLEM.getStatus())//not in First System
                                    ? upgradeInProgressStatus(packet) : systemNewStatus;
                }
                return updatePacketStatus(packet, systemNewStatus);
            }
        }catch (Exception e ){
            packet.setLastDeliveryStatus(SystemStatus.INCORRECT_BARCODE.getStatus());
            return updatePacketStatusAndSaveToHistory(packet, SystemStatus.A_VERIFIER.getStatus());
        }

        return null;
    }

    @Override
    public Packet addAttempt(Packet packet,String note) throws ParseException {
        packet.setAttempt(packet.getAttempt()+1);
        Date noteDate = new Date();
        String noteWithDate = noteDate.toString() +" "+ note;
        packet.setNote(packet.getNote()+" "+noteWithDate);
        savePacketStatusToHistory(packet,"tentative: "+packet.getAttempt()+" "+note);
        return updatePacket(packet);
    }

    private String mapFirstToSystemStatus(String status) {
        if (status == null || status.equals(""))
            return SystemStatus.A_VERIFIER.getStatus();
        FirstStatus firstStatus = FirstStatus.fromString(status);
        switch (firstStatus) {
            case LIVREE:
            case LIVRER:
            case EXCHANGE:
                return SystemStatus.LIVREE.getStatus();
            case RETOUR_EXPEDITEUR:
            case RETOUR_DEFINITIF:
            case RETOUR_CLIENT_AGENCE:
                return SystemStatus.RETOUR.getStatus();
            case EN_ATTENTE:
                return SystemStatus.CONFIRMEE.getStatus();
            case A_VERIFIER:
                return SystemStatus.A_VERIFIER.getStatus();
        }
        return SystemStatus.EN_COURS_1.getStatus();
    }
    private String upgradeInProgressStatus(Packet packet) {
        SystemStatus systemStatus = SystemStatus.fromString(packet.getStatus());
        if ((checkSameDateStatus(packet)
                || packet.getStatus().equals(SystemStatus.A_VERIFIER.getStatus()))
                && !packet.getStatus().equals(SystemStatus.CANCELED.getStatus()))
            return packet.getStatus();
        switch (systemStatus) {
            case EN_COURS_1:
                return SystemStatus.EN_COURS_2.getStatus();
            case EN_COURS_2:
                return SystemStatus.EN_COURS_3.getStatus();
            case EN_COURS_3:
                return SystemStatus.A_VERIFIER.getStatus();
            default:
                return SystemStatus.EN_COURS_1.getStatus();
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
            newPacket.setDeliveryPrice(packet.getDeliveryPrice());
            newPacket.setValid(false);
            newPacket.setExchange(true);
            newPacket.setStock(packet.getStock());
            newPacket.setDeliveryCompany(globalConf.getDeliveryCompany());
        }
        Packet response = packetRepository.save(newPacket);
        List<ProductsPacket> productsPackets = productsPacketRepository.findByPacketId(packet.getId());
        if(productsPackets.size()>0) {
            productsPackets.stream().forEach(productsPacket -> {
                ProductsPacket newProductsPacket = new ProductsPacket(productsPacket.getProduct(), response,productsPacket.getPacketDate(), productsPacket.getOffer(), productsPacket.getPacketOfferId());
                productsPacketRepository.save(newProductsPacket);
            });
        }
        savePacketStatusToHistory(newPacket,SystemStatus.CREATION.getStatus());
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
                    if (!optionalPacket.get().getStatus().equals(SystemStatus.RETOUR_RECU.getStatus())) {
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
        if(packet.isExchange()){
            if (status.equals(SystemStatus.PAYEE.getStatus())||status.equals(SystemStatus.LIVREE.getStatus()))
                return updateExchangePacketStatusToPaid(packet,status);
            if (status.equals(SystemStatus.RETOUR_RECU.getStatus())){
                return updateExchangePacketStatusToReturnReceived(packet);
            }
        }
        return updatePacketStatusAndSaveToHistory(packet, status);
    }
    private Packet updateExchangePacketStatusToReturnReceived(Packet packet){
        Long id = getExchangeId(packet);
        Optional<Packet> optionalPacket = packetRepository.findById(id);
        if(packet.getStatus().equals(SystemStatus.LIVREE.getStatus())
                ||packet.getStatus().equals(SystemStatus.PAYEE.getStatus())
                ||optionalPacket.get().getStatus().equals(SystemStatus.RETOUR.getStatus()))
        return updatePacketStatusAndSaveToHistory(optionalPacket.get(), SystemStatus.RETOUR_RECU.getStatus());
        else return updatePacketStatusAndSaveToHistory(packet, SystemStatus.RETOUR_RECU.getStatus());
    }
    private Packet updateExchangePacketStatusToPaid(Packet packet,String status){
        Long id = getExchangeId(packet);
        Optional<Packet> optionalPacket = packetRepository.findById(id);
        packet.setPrice(optionalPacket.get().getPrice()-optionalPacket.get().getDiscount()+packet.getPrice()-packet.getDiscount());
        if(!optionalPacket.get().getStatus().equals(SystemStatus.RETOUR_RECU.getStatus()))
            updatePacketStatusAndSaveToHistory(optionalPacket.get(), SystemStatus.RETOUR.getStatus());
        return updatePacketStatusAndSaveToHistory(packet, status);
    }
    private Packet updatePacketStatusAndSaveToHistory(Packet packet, String status) {
        if (
            !packet.getStatus().equals(status)&&
            !(
                packet.getStatus().equals(SystemStatus.RETOUR.getStatus())&&
                !(
                    status.equals(SystemStatus.RETOUR_RECU.getStatus())
                    || status.equals(SystemStatus.PROBLEM.getStatus())
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
        System.out.println("SecurityContextHolder.getContext().getAuthentication().getPrincipal();");
        if(SecurityContextHolder.getContext().getAuthentication() != null) {
            UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            if(userDetails != null) {
                User user = userRepository.findByUserName(userDetails.getUsername());
                packetStatus.setUser(user);
            }
        }
        packetStatusRepository.save(packetStatus);
    }
    private void updateProducts_Status(Packet packet,String status){
        if(status.equals(SystemStatus.LIVREE.getStatus())
                ||status.equals(SystemStatus.PAYEE.getStatus())
                ||status.equals(SystemStatus.CONFIRMEE.getStatus())
                ||status.equals(SystemStatus.RETOUR_RECU.getStatus())
                ||status.equals(SystemStatus.CANCELED.getStatus()))
            updateProductsPacket_Status_ByPacketId(packet,status);

    }
    private void updateProducts_Quantity(Packet packet,String status){
        if (status.equals(SystemStatus.RETOUR_RECU.getStatus())
                ||status.equals(SystemStatus.CONFIRMEE.getStatus())
                ||status.equals(SystemStatus.CANCELED.getStatus()))
            updateProductsQuantity(packet,status);
    }
    public void updateProductsPacket_Status_ByPacketId(Packet packet,String status) {
        int x = -1;
        if (status.equals(SystemStatus.RETOUR_RECU.getStatus()) || status.equals(SystemStatus.CANCELED.getStatus())) x = 0;
        if (status.equals(SystemStatus.CONFIRMEE.getStatus())) x = 1;
        if (status.equals(SystemStatus.LIVREE.getStatus())||status.equals(SystemStatus.PAYEE.getStatus())) x = 2;

        List<ProductsPacket> productsPackets = productsPacketRepository.findByPacketId(packet.getId());
        if(productsPackets.size() > 0) {
            for (ProductsPacket product : productsPackets) {
                product.setStatus(x);
                productsPacketRepository.save(product);
            };
        }
    }
    public void updateProductsQuantity(Packet packet,String status) {
        int quantity = 0;
        if (status.equals(SystemStatus.RETOUR_RECU.getStatus()) || status.equals(SystemStatus.CANCELED.getStatus())) quantity = 1;
        if (status.equals(SystemStatus.CONFIRMEE.getStatus())) quantity = -1;
        List<ProductsPacket> productsPackets = productsPacketRepository.findByPacketId(packet.getId());
        if(productsPackets.size() > 0) {
            for (ProductsPacket productsPacket : productsPackets) {
                Optional<Product> product = productRepository.findById(productsPacket.getProduct().getId());
                if (product.isPresent()) {
                    updateProductQuantity(product.get(), quantity);
                }
            }
        }else return ;
        //checkPacketProductsValidity(packet.getId());
    }
    private void updateProductQuantity(Product product, int quantityChange) {

        product.setQuantity(product.getQuantity() + quantityChange);
        product.setDate(new Date());
        productRepository.save(product);
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

}
