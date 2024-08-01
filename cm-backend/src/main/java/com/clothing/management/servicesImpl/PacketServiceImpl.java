package com.clothing.management.servicesImpl;
import com.clothing.management.auth.util.SessionUtils;
import com.clothing.management.dto.*;
import com.clothing.management.dto.DeliveryCompanyDTOs.BarCodeStatusDTO;
import com.clothing.management.dto.DeliveryCompanyDTOs.DeliveryResponse;
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

import java.io.IOException;
import java.lang.reflect.Field;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

import static com.clothing.management.enums.SystemStatus.*;
import static com.clothing.management.enums.SystemStatus.LIVREE;
import static java.util.stream.Collectors.groupingBy;
@Transactional("tenantTransactionManager")
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

    @Override
    public Packet getPacketById(Long packetId) throws Exception {
        return packetRepository.findById(packetId)
                .orElseThrow(() -> new Exception("Packet not found!"));
    }

    @Override
    public Packet getPacketByBarcode(String barCode) throws Exception {
        return packetRepository.findByBarCode(barCode)
                .orElseThrow(() -> new Exception("Packet not found!"));
    }

    @Autowired
    public PacketServiceImpl(
            IPacketRepository packetRepository,
            IProductRepository productRepository,
            IProductsPacketRepository productsPacketRepository,
            IPacketStatusRepository packetStatusRepository,
            DeliveryCompanyServiceFactory deliveryCompanyServiceFactory,
            SessionUtils sessionUtils,
            IGlobalConfRepository globalConfRepository,
            IFbPageRepository fbPageRepository
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
    @Transactional(readOnly = true, transactionManager = "tenantTransactionManager")
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
                if (status.equals("Tous"))
                    return packetRepository.findAllPacketsByDate(dateFormat.parse(startDate), dateFormat.parse(endDate), pageable);
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


    public List<PacketDTO> findAllPacketsByDate(String startDate, String endDate) throws ParseException {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        List<Packet> packets = packetRepository.findAllPacketsByDate(dateFormat.parse(startDate), dateFormat.parse(endDate));
        return packets.stream().map(PacketDTO::new).collect(Collectors.toList());
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

    private int getStock(List<ProductsPacket> productsPackets, String barcode){
        return (barcode == null || barcode.equals(""))?productsPackets.stream()
                .mapToInt(productsPacket -> productsPacket.getProduct().getQuantity()) // Assuming getQte() returns the quantity
                .min()
                .orElse(100):100;
    }

    @Override
    public List<Packet> findAllPacketsByDate(Date date) {
        return packetRepository.findAllByDate(date);
    }

    /*@Override
    public Optional<Packet> findPacketById(Long idPacket) {
        return packetRepository.findById(idPacket);
    }*/

    @Override
    public int deleteEmptyPacket() {
        return packetRepository.deleteEmptyPacket();
    }

    @Override
    public Packet addPacket() throws Exception {
        GlobalConf globalConf = globalConfRepository.findAll().stream().findFirst().orElseThrow(() -> new Exception("globalConf not found"));;
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
    public PacketValidationDTO updatePacketValid(String barCode, String type) throws Exception {
        Packet nonValidPacket = getPacketByBarcode(barCode);
        Packet packet;
        try{
            if (type.equals(CONFIRMED.getStatus())){
                nonValidPacket.setValid(true);
                packet = packetRepository.save(nonValidPacket);
            }
            else packet = updatePacketStatus(nonValidPacket, RETURN_RECEIVED.getStatus());
        } catch (Exception e ){
            LOGGER.error("Error " + e);
            return null;
        }

        return new PacketValidationDTO(packet);
    }

    @Override
    public Packet patchPacket(Long idPacket, Map<String, Object> field) throws Exception {
        Packet packet = null;
            Optional<String> firstKeyOptional = field.keySet().stream().findFirst();
            if (firstKeyOptional.isPresent()) {
                String firstKey = firstKeyOptional.get();
                Field fieldPacket = ReflectionUtils.findField(Packet.class, firstKey);
                String value = String.valueOf(field.get(firstKey));
                fieldPacket.setAccessible(true);
                if (firstKey.equals("status")) {
                        packet = getPacketById(idPacket);
                        if (value.equals(CONFIRMED.getStatus()))
                            createBarCode(packet);
                    updatePacketStatus(packet, value);
                }else if (firstKey.equals("customerPhoneNb")) {
                        int existCount =0;
                        if(field.get(firstKey) != "" && field.get(firstKey) != null)
                            existCount = checkPhone(field.get(firstKey)+"");
                        packetRepository.savePhoneNumber(idPacket, value,existCount);
                }else if(firstKey.equals("fbPage")){
                        packetRepository.saveFbPage(idPacket, Long.valueOf(value));
                } else if(firstKey.equals("city")){
                        packetRepository.saveCity(idPacket, Long.valueOf(value));
                }else if (firstKey.equals("customerName")){
                        packetRepository.saveCustomerName(idPacket, value);
                }else if (firstKey.equals("address")){
                    packetRepository.saveAddress(idPacket, value);
                }else if (firstKey.equals("date")){
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");
                    packetRepository.saveDate(idPacket, dateFormat.parse(value));
                }else if (firstKey.equals("barcode")){
                    packetRepository.saveBarcode(idPacket, value);
                }
                else{
                        ReflectionUtils.setField(fieldPacket, packet, field.get(firstKey));
                        System.out.println("no"+firstKey);
                        packetRepository.save(packet);
                }
                packet = packetRepository.findById(idPacket).get();
            }
        return packet;
    }




    /*@Override
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
    }*/

    public List<Long> updateUnConfirmedStock(Long productId, Integer stock) {
        List<Long> productIds = productsPacketRepository.getUnconfirmedPacketStock_By_ProductId(productId);
        int count = 0;
        if(productIds.size()>0){
            count = productsPacketRepository.updateUnconfirmedPacketStock_By_ProductId(productIds,stock);
        }
        //System.out.println(count+"updateUnConfirmedStock:"+productIds+" /id:"+productId+" /stock:"+stock);
        return productIds;
    }

    @Transactional("tenantTransactionManager")
    public List<ProductsPacketDTO> findPacketRelatedProducts(Long packetId) {
        List<ProductsPacket> productsPackets = productsPacketRepository.findByPacketId(packetId);
        return groupProductsPackets(productsPackets);
        /*return productsPackets.stream()
                .map(productsPacket -> new ProductsPacketDTO(productsPacket))
                .collect(Collectors.toList());*/
    }

    private List<ProductsPacketDTO> groupProductsPackets(List<ProductsPacket> productsPackets) {
        Map<Long, List<ProductsPacket>> groupedByOfferId = productsPackets.stream()
                .collect(Collectors.groupingBy(ProductsPacket::getPacketOfferId));

        List<ProductsPacketDTO> productsPacketDTOs = new ArrayList<>();

        for (Map.Entry<Long, List<ProductsPacket>> entry : groupedByOfferId.entrySet()) {
            Long packetOfferId = entry.getKey();
            List<ProductsPacket> groupedPackets = entry.getValue();
            Long offerId = groupedPackets.get(0).getOffer().getId();

            List<ProductDTO> products = groupedPackets.stream()
                    .map(pp-> new ProductDTO(pp.getProduct()))
                    .collect(Collectors.toList());

            ProductsPacketDTO dto = new ProductsPacketDTO(
                    offerId,
                    packetOfferId,
                    products
            );
            productsPacketDTOs.add(dto);
        }

        return productsPacketDTOs;
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
        newModel.setPurchasePrice(model.getPurchasePrice());
        newModel.setEarningCoefficient(model.getEarningCoefficient());
        newModel.setDeleted(model.isDeleted());
        /*if(model.getImage() != null)
            newModel.setBytes(Files.readAllBytes(new File(model.getImage().getImagePath()).toPath()));*/
        return newModel;
    }


    @Override
    public int checkPhone(String phoneNumber) {
        return packetRepository.findAllPacketsByPhone_number(phoneNumber);
    }

    @Override
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
                    packetRepository.save(packet);
                //}
                return deliveryResponse;
            }

        return null;
    }



    @Override
    public PacketDTO getLastStatus(long packetId) throws Exception {
        Packet packet = packetRepository.findById(packetId).orElseThrow( () -> new Exception("Packet not found with ID: " + packetId));
        return new PacketDTO(getLastStatus(packet));
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
                    DeliveryCompanyStatus dcStatus = DeliveryCompanyStatus.fromString(deliveryState, deliveryCompanyName);

                    systemNewStatus = mapDeliveryToSystemStatus(dcStatus);
                    packet.setLastDeliveryStatus(deliveryState);
                    if (dcStatus == DeliveryCompanyStatus.WAITING
                            || dcStatus == DeliveryCompanyStatus.RETOUR_DEPOT
                            || (dcStatus == DeliveryCompanyStatus.AU_MAGASIN
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

    private String mapDeliveryToSystemStatus(DeliveryCompanyStatus status) {
        if (status == null || status.equals(""))
            return TO_VERIFY.getStatus();
        return switch (status) {
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
                return mapDeliveryToSystemStatus(DeliveryCompanyStatus.fromString(packet.getLastDeliveryStatus(), deliveryCompanyName));
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

    @Transactional("tenantTransactionManager")
    public PacketDTO duplicatePacket(Long idPacket) throws Exception {
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
            newPacket.setDeliveryCompany(globalConf.getDeliveryCompany());
            newPacket.setExchangeId(packet.getId());
            newPacket.setHaveExchange(false);
        Packet response = packetRepository.save(newPacket);

        List<ProductsPacket> productsPackets = packet.getProductsPackets();//productsPacketRepository.findByPacketId(packet.getId());
        List<ProductsPacket> newProductsPackets = new ArrayList<>();
        if(productsPackets.size()>0) {
            productsPackets.stream().forEach(productsPacket -> {
                productsPacket.setPacket(response);
                newProductsPackets.add(productsPacket);
                //ProductsPacket newProductsPacket = new ProductsPacket(productsPacket.getProduct(), response, productsPacket.getOffer(), productsPacket.getPacketOfferId(),productsPacket.getProfits());

            });
            productsPacketRepository.saveAll(productsPackets);
        }
        savePacketStatusToHistory(newPacket,CREATION.getStatus());
        packet.setHaveExchange(true);
        packetRepository.save(packet);
        Packet fullPacket = packetRepository.findById(response.getId()).orElseThrow(() -> new Exception("Packet not found with ID: " + response.getId()));
        return new PacketDTO(fullPacket);
    }

    public List<String> updatePacketsByBarCodes(BarCodeStatusDTO barCodeStatusDTO) {
        List<String> errors = new ArrayList<>();
        String newState = barCodeStatusDTO.getStatus();
        barCodeStatusDTO.getBarCodes().forEach(barCode -> {
            try {
                Packet packet = getPacketByBarcode(barCode);
                    if (!packet.getStatus().equals(RETURN_RECEIVED.getStatus())) {
                            updatePacketStatus(packet, newState);
                    } else {
                        errors.add(barCode + " déja récu");
                    }
            } catch(Exception e){
                errors.add(barCode+ " erreur");
                e.printStackTrace();
            }
        });
        return errors;
    }

    public Packet updatePacketStatus(Packet packet,String status) throws Exception {
        if(packet.getExchangeId() != null){
            if (status.equals(PAID.getStatus())||status.equals(LIVREE.getStatus()))
                return updateExchangePacketStatusToPaid(packet,status);
            if (status.equals(RETURN_RECEIVED.getStatus())){
                return updateExchangePacketStatusToReturnReceived(packet);
            }
        }
        return updatePacketStatusAndSaveToHistory(packet, status);
    }

    public Packet updateExchangePacketStatusToReturnReceived(Packet packet) throws Exception {
        Long id = packet.getExchangeId();
        Packet exchangePacket = getPacketById(id);
        if(packet.getStatus().equals(LIVREE.getStatus())
                ||packet.getStatus().equals(PAID.getStatus())
                ||exchangePacket.getStatus().equals(RETURN.getStatus()))
        return updatePacketStatusAndSaveToHistory(exchangePacket, RETURN_RECEIVED.getStatus());
        else return updatePacketStatusAndSaveToHistory(packet, RETURN_RECEIVED.getStatus());
    }

    public Packet updateExchangePacketStatusToPaid(Packet packet,String status) throws Exception {
        Long id = packet.getExchangeId();
        Packet exchangePacket = getPacketById(id);
        packet.setPrice(exchangePacket.getPrice()-exchangePacket.getDiscount()+packet.getPrice()-packet.getDiscount());
        if(!exchangePacket.getStatus().equals(RETURN_RECEIVED.getStatus()))
            updatePacketStatusAndSaveToHistory(exchangePacket, RETURN.getStatus());
        return updatePacketStatusAndSaveToHistory(packet, status);
    }

    public Packet updatePacketStatusAndSaveToHistory(Packet packet, String status) {
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
        return packetRepository.save(packet);
    }

    public void updateProducts_Status(Packet packet,String status){
        if(status.equals(LIVREE.getStatus())
                ||status.equals(PAID.getStatus())
                ||status.equals(CONFIRMED.getStatus())
                ||status.equals(RETURN_RECEIVED.getStatus())
                ||status.equals(CANCELED.getStatus()))
            updateProductsPacket_Status_ByPacketId(packet,status);
    }

    public void updateProducts_Quantity(Packet packet,String status){
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

    public void updateProductQuantity(Product product, int quantityChange) {
        product.setQuantity(product.getQuantity() + quantityChange);
        product.setDate(new Date());
        productRepository.save(product);
    }

    public Packet savePacketStatus(Packet packet, String status) {
        packet.setStatus(status);
        packet.setLastUpdateDate(new Date());

        //packetRepository.savePacketStatus(packet.getId(),status,packet.getLastDeliveryStatus());
        return packetRepository.save(packet);
    }

    public void savePacketStatusToHistory(Packet packet, String status) {
        PacketStatus packetStatus = new PacketStatus();
        packetStatus.setPacket(packet);
        packetStatus.setStatus(status);
        packetStatus.setDate(new Date());
        packetStatus.setUser(sessionUtils.getCurrentUser());
        packetStatusRepository.save(packetStatus);
    }

    @Override
    public PacketDTO addProductsToPacket(SelectedProductsDTO selectedProductsDTO) throws Exception {
        String noStockStatus = selectedProductsDTO.getStatus();
        List<ProductOfferDTO> productsOffers = selectedProductsDTO.getProductsOffers();
        Packet packet = getPacketById(selectedProductsDTO.getIdPacket());

        if (noStockStatus != null && noStockStatus.equals(OOS.getStatus()) &&
                (packet.getStatus().equals(NOT_CONFIRMED.getStatus()) ||
                        packet.getStatus().equals(NOTSERIOUS.getStatus()) ||
                        packet.getStatus().equals(CANCELED.getStatus()) ||
                        packet.getStatus().equals(UNREACHABLE.getStatus()))) {
            packet.setStatus(OOS.getStatus());
        }

        if (noStockStatus != null && (noStockStatus.equals(NOT_CONFIRMED.getStatus()) || noStockStatus.equals(CANCELED.getStatus()))) {
            packet.setStatus(noStockStatus);
        }

        List<ProductsPacket> newProductsPacket = productsOffers.stream()
                .map(productOffer -> addProductPacket(packet, productOffer))
                .toList();

        packet.addProductsToPacket(newProductsPacket);
        packet.setPacketDescription(selectedProductsDTO.getPacketDescription());
        packet.setPrice(selectedProductsDTO.getTotalPrice());
        packet.setDeliveryPrice(selectedProductsDTO.getDeliveryPrice());
        packet.setDiscount(selectedProductsDTO.getDiscount());
        packet.setProductCount(selectedProductsDTO.getProductCount());
        return new PacketDTO(packetRepository.save(packet));
    }

    private ProductsPacket addProductPacket(Packet packet,ProductOfferDTO productsPacket){
        return new ProductsPacket(
                new Product(productsPacket.getProductId()),
                packet,
                new Offer(productsPacket.getOfferId()),
                productsPacket.getPacketOfferIndex(),
                productsPacket.getProfits()
        );
    }

    @Override
    public Packet addAttempt(Long packetId, String note) {
        Packet packet = packetRepository.findById(packetId).get();
        packet.setAttempt(packet.getAttempt() + 1);
        Date noteDate = new Date();
        // Formatting the note date to "dd hh:mm" format
        SimpleDateFormat sdf = new SimpleDateFormat("dd-HH:mm");
        String noteWithDate = "-Le "+sdf.format(noteDate) + " " + note;
        if(packet.getNote().equals("")){packet.setNote(noteWithDate);}
        else packet.setNote(String.format("%s\n%s", packet.getNote(), noteWithDate));
        savePacketStatusToHistory(packet, "tentative: " + packet.getAttempt() + " " + note);
        return packetRepository.save(packet);
    }
    @Override
    public void deletePacketById(Long idPacket) throws Exception {
        Packet packet = getPacketById(idPacket);
        updatePacketStatusAndSaveToHistory(packet, DELETED.getStatus());
    }

    /**
     * Delete selected packets by id
     *
     * @param packetsId
     */
    @Override
    public void deleteSelectedPackets(List<Long> packetsId) throws Exception {
        for (Long packetId : packetsId) {
            Packet packet = getPacketById(packetId);
            if(packet.getCustomerPhoneNb() == null || packet.getCustomerPhoneNb().equals(""))
                packetRepository.deleteById(packetId);
            else {
                updatePacketStatusAndSaveToHistory(packet, DELETED.getStatus());
            }
        }
    }
}
