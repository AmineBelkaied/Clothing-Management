package com.clothing.management.servicesImpl;
import com.clothing.management.auth.util.SessionUtils;
import com.clothing.management.dto.*;
import com.clothing.management.enums.DeliveryCompanyName;
import com.clothing.management.enums.DeliveryCompanyStatus;
import com.clothing.management.enums.SystemStatus;
import com.clothing.management.models.DashboardCard;
import com.clothing.management.servicesImpl.api.DeliveryCompanyService;
import com.clothing.management.servicesImpl.api.DeliveryCompanyServiceFactory;
import com.clothing.management.entities.*;
import com.clothing.management.repository.*;
import com.clothing.management.services.PacketService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

import static com.clothing.management.enums.SystemStatus.*;
import static com.clothing.management.enums.SystemStatus.LIVREE;
import static java.util.stream.Collectors.groupingBy;

@Service
public class PacketServiceImpl implements PacketService {

    private static final Logger LOGGER = LoggerFactory.getLogger(PacketService.class);
    private final IPacketRepository packetRepository;
    private final IProductRepository productRepository;
    private final IProductsPacketRepository productsPacketRepository;
    private final IPacketStatusRepository packetStatusRepository;
    private final DeliveryCompanyServiceFactory deliveryCompanyServiceFactory;
    private final SessionUtils sessionUtils;
    private final IGlobalConfRepository globalConfRepository;
    private final static List<String> ignoredDateStatusList = List.of(new String[]{ RETURN.getStatus(), NOT_CONFIRMED.getStatus(), UNREACHABLE.getStatus(), PROBLEM.getStatus(), TO_VERIFY.getStatus(), OOS.getStatus(), IN_PROGRESS_1.getStatus(), IN_PROGRESS_2.getStatus(), IN_PROGRESS_3.getStatus()});

    @Autowired
    public PacketServiceImpl(
            IPacketRepository packetRepository,
            IProductRepository productRepository,
            IProductsPacketRepository productsPacketRepository,
            IPacketStatusRepository packetStatusRepository,
            DeliveryCompanyServiceFactory deliveryCompanyServiceFactory,
            SessionUtils sessionUtils,
            IGlobalConfRepository globalConfRepository
    ) {
        this.packetRepository = packetRepository;
        this.productRepository = productRepository;
        this.productsPacketRepository = productsPacketRepository;
        this.packetStatusRepository = packetStatusRepository;
        this.deliveryCompanyServiceFactory = deliveryCompanyServiceFactory;
        this.globalConfRepository = globalConfRepository;
        this.sessionUtils = sessionUtils;
    }

    @Override
    public List<Packet> findAllPackets() {
        List<Packet> sortedPackets = packetRepository.findAll().stream()
                .sorted(Comparator.comparingLong(Packet::getId).reversed())
                .collect(Collectors.toList());
        return sortedPackets;
    }

    @Override
    public Page<Packet> findAllPackets(Pageable pageable, String searchText, String startDate, String endDate, String status, boolean mandatoryDate) throws ParseException {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        if(mandatoryDate) {
            if (searchText != null)
                return packetRepository.findAllPacketsByFieldAndDate(searchText,dateFormat.parse(startDate), dateFormat.parse(endDate), pageable);

            if (status != null)
            {
                if(status.equals("validation"))
                    return packetRepository.findAllNotValidatedPackets(pageable);
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
                if(status.equals("validation"))
                    return packetRepository.findAllNotValidatedPackets(pageable);
                return packetRepository.findAllPacketsByStatus(convertStatusToList(status), pageable);
            }
        }

        return packetRepository.findAllPacketsByDate(dateFormat.parse(startDate), dateFormat.parse(endDate), pageable);
    }

    private List<String> convertStatusToList(String status) {
        return Arrays.asList(status.split(",", -1));
    }


    public List<Packet> findAllPacketsByDate(String startDate, String endDate) throws ParseException {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        return packetRepository.findAllPacketsByDate(dateFormat.parse(startDate), dateFormat.parse(endDate));
    }

    @Override
    public List<Packet> findAllDiggiePackets() {
        List<String> status = Arrays.asList(
                SystemStatus.PAID.getStatus(),
                SystemStatus.CANCELED.getStatus(),
                SystemStatus.LIVREE.getStatus(),
                SystemStatus.DELETED.getStatus(),
                SystemStatus.RETURN.getStatus(),
                SystemStatus.NOTSERIOUS.getStatus(),
                SystemStatus.RETURN_RECEIVED.getStatus(),
                SystemStatus.PROBLEM.getStatus()
        );
        return packetRepository.findAllDiggiePackets(status);
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
    @Transactional("tenantTransactionManager")
    @Override
    public void updatePacketStockForRupture(){
        packetRepository.updatePacketStockForRuptureStatus();
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
    @Transactional("tenantTransactionManager")
    public Packet updatePacket(Packet packet) {
        System.out.println("start update save"+packet);

        return packetRepository.save(packet);
    }
    @Override
    @Transactional("tenantTransactionManager")
    public Packet updatePacketValid(String barCode,String type) {
        Optional<Packet> optionalPacket = packetRepository.findByBarCode(barCode);
        if (type.equals(CONFIRMED.getStatus())){
            optionalPacket.get().setValid(true);
            return packetRepository.save(optionalPacket.get());
        }
        return updatePacketStatus(optionalPacket.get(), RETURN_RECEIVED.getStatus());
    }

    @Override
    @Transactional("tenantTransactionManager")
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
    @Transactional("tenantTransactionManager")
    public Packet addProductsToPacket(SelectedProductsDTO selectedProductsDTO, Integer stock) {
        String noStockStatus = selectedProductsDTO.getStatus();
        List<ProductOfferDTO> productsOffers = selectedProductsDTO.getProductsOffers();
        Optional<Packet> optionalPacket = packetRepository.findById(selectedProductsDTO.getIdPacket());

        if (optionalPacket.isPresent()) {
            Packet existingPacket = optionalPacket.get();
            existingPacket.setPrice(selectedProductsDTO.getTotalPrice());
            existingPacket.setDeliveryPrice(selectedProductsDTO.getDeliveryPrice());
            existingPacket.setDiscount(selectedProductsDTO.getDiscount());
            existingPacket.setProductCount(selectedProductsDTO.getProductCount());

            if (noStockStatus != null && noStockStatus.equals(OOS.getStatus()) &&
                    (existingPacket.getStatus().equals(NOT_CONFIRMED.getStatus()) ||
                            existingPacket.getStatus().equals(NOTSERIOUS.getStatus()) ||
                            existingPacket.getStatus().equals(CANCELED.getStatus()) ||
                            existingPacket.getStatus().equals(UNREACHABLE.getStatus()))) {
                existingPacket.setStatus(OOS.getStatus());
            }

            if (noStockStatus != null && (noStockStatus.equals(NOT_CONFIRMED.getStatus()) || noStockStatus.equals(CANCELED.getStatus()))) {
                existingPacket.setStatus(noStockStatus);
            }

            List<ProductsPacket> existingProductsPacket = productsPacketRepository.findByPacketId(existingPacket.getId());
            if (!existingProductsPacket.isEmpty()) {
                productsPacketRepository.deleteAllInBatch(existingProductsPacket);
            }

            List<ProductsPacket> newProductsPacket = new ArrayList<>();
            for (ProductOfferDTO productOfferDTO : productsOffers) {
                newProductsPacket.add(new ProductsPacket(new Product(productOfferDTO.getProductId()), existingPacket,
                        new Offer(productOfferDTO.getOfferId()), productOfferDTO.getPacketOfferIndex(),
                        productOfferDTO.getProfits()));
            }

            productsPacketRepository.saveAll(newProductsPacket);
            existingPacket.setPacketDescription(selectedProductsDTO.getPacketDescription());
            existingPacket.setStock(stock);

            return updatePacket(existingPacket);
        }
        return optionalPacket.orElse(null);
    }


    @Override
    @Transactional("tenantTransactionManager")
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

    @Transactional("tenantTransactionManager")
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
                Map<Long, List<ProductsPacket>> offerListMap = productsPackets.stream()
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
        newModel.setPurchasePrice(model.getPurchasePrice());
        newModel.setEarningCoefficient(model.getEarningCoefficient());
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
    @Transactional("tenantTransactionManager")
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

    /*@Override
    public List<DashboardCard> createDashboard() {
        return packetRepository.createDashboard();
    }*/

    @Override
    @Transactional("tenantTransactionManager")
    public List<DashboardCard> syncNotification(String startDate, String endDate) {
        return packetRepository.createNotification(startDate,endDate);
    }
    @Override
    public List<PacketStatus> findPacketTimeLineById(Long idPacket) throws Exception {
        Packet packet = packetRepository.findById(idPacket)
                .orElseThrow(() -> new Exception("Packet not found!"));
        return packet.getPacketStatus();
    }
    @Override
    public DeliveryResponse createBarCode(Packet packet) throws IOException {
        DeliveryCompanyName deliveryCompanyName = DeliveryCompanyName.fromString(packet.getDeliveryCompany().getName());
        DeliveryCompanyService deliveryCompanyService = deliveryCompanyServiceFactory.getDeliveryCompanyService(deliveryCompanyName);
        DeliveryResponse deliveryResponse = deliveryCompanyService.createBarCode(packet);

        LOGGER.debug("deliveryResponse : " + deliveryResponse);
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
    public Packet getLastStatus(Packet packet) {
        try {
            DeliveryCompanyName deliveryCompanyName = DeliveryCompanyName.fromString(packet.getDeliveryCompany().getName());
            DeliveryCompanyService deliveryCompanyService = deliveryCompanyServiceFactory.getDeliveryCompanyService(deliveryCompanyName);
            DeliveryResponse deliveryResponse = deliveryCompanyService.getLastStatus(packet.getBarcode(), packet.getDeliveryCompany());
            String deliveryState = deliveryResponse.getState();
            String packetStatus = packet.getStatus();
            LOGGER.debug("deliveryResponse : " + deliveryResponse);
            if (deliveryResponse.getResponseCode() == 200 || deliveryResponse.getResponseCode() == 201 || deliveryResponse.getResponseCode() == 404) {
                String systemNewStatus = TO_VERIFY.getStatus();

                if (deliveryResponse.getStatus()==404  || deliveryState.equals("")) {
                    throw new Exception("Problem API");
                }else if (deliveryResponse.getStatus()>199 && deliveryState != null) {
                    //Convert input from first to System Status
                    savePacketStatusToHistory(packet,deliveryCompanyName+":"+deliveryState);
                    systemNewStatus = mapDeliveryToSystemStatus(deliveryState,deliveryCompanyName);
                    packet.setLastDeliveryStatus(deliveryState);
                    //System.out.println("deliveryResponse.getState():"+deliveryState);

                    if (DeliveryCompanyStatus.fromString(deliveryState, deliveryCompanyName) == DeliveryCompanyStatus.WAITING
                            || DeliveryCompanyStatus.fromString(deliveryState, deliveryCompanyName) == DeliveryCompanyStatus.RETOUR_DEPOT
                            || (DeliveryCompanyStatus.fromString(deliveryState, deliveryCompanyName) == DeliveryCompanyStatus.AU_MAGASIN
                            && packetStatus.equals(IN_PROGRESS_1.getStatus()))) {
                        return packet;
                    }

                    systemNewStatus =
                            (systemNewStatus.equals(IN_PROGRESS_1.getStatus())//First always return "en cours"
                                    || systemNewStatus.equals(IN_PROGRESS_2.getStatus())//not in First System
                                    || systemNewStatus.equals(IN_PROGRESS_3.getStatus()))
                                    && !packetStatus.equals(PROBLEM.getStatus())//not in First System
                                    ? upgradeInProgressStatus(packet) : systemNewStatus;
                }
                return updatePacketStatus(packet, systemNewStatus);
            }
        } catch (Exception e ){
            LOGGER.error("Error " + e);
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

    private String mapDeliveryToSystemStatus(String status,DeliveryCompanyName deliveryCompanyName) {
        if (status == null || status.equals(""))
            return TO_VERIFY.getStatus();
        DeliveryCompanyStatus deliveryCompanyStatus = DeliveryCompanyStatus.fromString(status,deliveryCompanyName);
        return switch (deliveryCompanyStatus) {
            case LIVREE, EXCHANGE -> LIVREE.getStatus();
            case RETOUR_EXPEDITEUR ,
                    RETOUR_DEFINITIF, RETOUR_CLIENT_AGENCE,
                    RETOUR_RECU -> RETURN.getStatus();
            case ANNULER, WAITING, A_VERIFIER -> TO_VERIFY.getStatus();
            default -> IN_PROGRESS_1.getStatus();
        };
    }
    private String upgradeInProgressStatus(Packet packet) {
        DeliveryCompanyName deliveryCompanyName = DeliveryCompanyName.fromString(packet.getDeliveryCompany().getName());
        SystemStatus systemStatus = SystemStatus.fromString(packet.getStatus());
        if (checkSameDateStatus(packet)&& !packet.getStatus().equals(CANCELED.getStatus()))
            return packet.getStatus();

        switch (systemStatus) {
            case IN_PROGRESS_1:
                return IN_PROGRESS_2.getStatus();
            case IN_PROGRESS_2:
                return IN_PROGRESS_3.getStatus();
            case IN_PROGRESS_3:
                return TO_VERIFY.getStatus();
            case TO_VERIFY:
                return mapDeliveryToSystemStatus(packet.getLastDeliveryStatus(),deliveryCompanyName);
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
            newPacket.setHaveExchange(false);

        Packet response = packetRepository.save(newPacket);
        List<ProductsPacket> productsPackets = productsPacketRepository.findByPacketId(packet.getId());
        if(productsPackets.size()>0) {
            productsPackets.stream().forEach(productsPacket -> {
                ProductsPacket newProductsPacket = new ProductsPacket(productsPacket.getProduct(), response, productsPacket.getOffer(), productsPacket.getPacketOfferId(),productsPacket.getProfits());
                productsPacketRepository.save(newProductsPacket);
            });
        }
        savePacketStatusToHistory(newPacket,CREATION.getStatus());
        packet.setHaveExchange(true);
        packetRepository.save(packet);
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
        if(!productsPackets.isEmpty()) {
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
        if(!productsPackets.isEmpty()) {
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
