package com.clothing.management.servicesImpl;
import com.clothing.management.auth.util.SessionUtils;
import com.clothing.management.dto.*;
import com.clothing.management.dto.DeliveryCompanyDTOs.BarCodeStatusDTO;
import com.clothing.management.dto.DeliveryCompanyDTOs.DeliveryResponse;
import com.clothing.management.enums.DeliveryCompanyName;
import com.clothing.management.enums.DeliveryCompanyStatus;
import com.clothing.management.enums.SystemStatus;
import com.clothing.management.exceptions.custom.notfound.PacketNotFoundException;
import com.clothing.management.exceptions.generic.EntityNotFoundException;
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
@Transactional("tenantTransactionManager")
@Service
public class PacketServiceImpl implements PacketService {

    private static final Logger LOGGER = LoggerFactory.getLogger(PacketService.class);
    private final IPacketRepository packetRepository;
    private final IProductRepository productRepository;
    private final IProductsPacketRepository productsPacketRepository;
    private final DeliveryCompanyServiceFactory deliveryCompanyServiceFactory;
    private final SessionUtils sessionUtils;
    private final IGlobalConfRepository globalConfRepository;
    private final static List<String> ignoredDateStatusList = List.of(new String[]{RETURN.getStatus(), NOT_CONFIRMED.getStatus(), UNREACHABLE.getStatus(), PROBLEM.getStatus(), TO_VERIFY.getStatus(), OOS.getStatus(), IN_PROGRESS_1.getStatus(), IN_PROGRESS_2.getStatus(), IN_PROGRESS_3.getStatus()});

    @Override
    public Packet getPacketById(Long packetId) throws PacketNotFoundException {
        return packetRepository.findById(packetId)
                .orElseThrow(() -> new PacketNotFoundException(packetId,"Packet not found!"));
    }

    @Override
    public Packet getPacketByBarcode(String barCode) throws EntityNotFoundException {
        return packetRepository.findByBarCode(barCode)
                .orElseThrow(() -> new EntityNotFoundException("BarCode",0L,barCode));
    }

    @Autowired
    public PacketServiceImpl(
            IPacketRepository packetRepository,
            IProductRepository productRepository,
            IProductsPacketRepository productsPacketRepository,
            DeliveryCompanyServiceFactory deliveryCompanyServiceFactory,
            SessionUtils sessionUtils,
            IGlobalConfRepository globalConfRepository
    ) {
        this.packetRepository = packetRepository;
        this.productRepository = productRepository;
        this.productsPacketRepository = productsPacketRepository;
        this.deliveryCompanyServiceFactory = deliveryCompanyServiceFactory;
        this.globalConfRepository = globalConfRepository;
        this.sessionUtils = sessionUtils;
    }

    @Override
    public List<Packet> findAllPackets() {
        return packetRepository.findAll().stream()
                .sorted(Comparator.comparingLong(Packet::getId).reversed())
                .collect(Collectors.toList());

    }

    @Override
    @Transactional(readOnly = true, transactionManager = "tenantTransactionManager")
    public Page<PacketDTO> findAllPackets(Pageable pageable, String searchText, String beginDate, String endDate, String status, boolean mandatoryDate) throws ParseException {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        if (mandatoryDate) {
            if (searchText != null)
                return packetRepository.findAllPacketsByFieldAndDate(searchText, dateFormat.parse(beginDate), dateFormat.parse(endDate), pageable);

            if (status != null) {
                if (status.equals("Tous"))
                    return packetRepository.findAllPacketsByDate(dateFormat.parse(beginDate), dateFormat.parse(endDate), pageable);
                return packetRepository.findAllPacketsByDateAndStatus(dateFormat.parse(beginDate), dateFormat.parse(endDate), convertStatusToList(status), pageable);
            }

        } else {
            if (searchText != null)
                return packetRepository.findAllPacketsByField(searchText, pageable);
            if (beginDate != null && status != null) {
                if (status.equals("Tous"))
                    return packetRepository.findAllPacketsByDate(dateFormat.parse(beginDate), dateFormat.parse(endDate), pageable);
                return packetRepository.findAllPacketsByStatus(ignoredDateStatusList, convertStatusToList(status), dateFormat.parse(beginDate), dateFormat.parse(endDate), pageable);
            }
            if (status != null) {
                return packetRepository.findAllPacketsByStatus(convertStatusToList(status), pageable);
            }
        }

        return packetRepository.findAllPacketsByDate(dateFormat.parse(beginDate), dateFormat.parse(endDate), pageable);
    }


    @Override
    @Transactional(readOnly = true, transactionManager = "tenantTransactionManager")
    public List<PacketValidationDTO> findValidationPackets() {
        return packetRepository.findValidationPackets().stream()
                .map(PacketValidationDTO::new)
                .collect(Collectors.toList());
    }

    private List<String> convertStatusToList(String status) {

        return Arrays.asList(status.split(",", -1));
    }


    public List<PacketDTO> findAllPacketsByDate(String beginDate, String endDate) throws ParseException {
        //System.out.println("beginDate:"+beginDate+"/endDate"+endDate);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        return packetRepository.findAllPacketsByDate(dateFormat.parse(beginDate), dateFormat.parse(endDate))
                .stream().map(PacketDTO::new).collect(Collectors.toList());
    }

    @Override
    public List<Packet> findSyncPackets() {
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
    public int deleteEmptyPacket() {

        return packetRepository.deleteEmptyPacket();
    }

    @Override
    public Packet addPacket() throws Exception {
        GlobalConf globalConf = globalConfRepository.findAll().stream().findFirst().orElseThrow(() -> new Exception("globalConf not found"));
        Packet packet = new Packet(globalConf.getDeliveryCompany());
        //savePacketStatusToHistory(packet,CREATION.getStatus());
        User currentUser = sessionUtils.getCurrentUser();
        packet.getPacketStatus().add(new PacketStatus(CREATION.getStatus(), packet, currentUser));

        return packetRepository.save(packet);
    }

    @Override
    public Packet updatePacket(Packet packet) {
        return packetRepository.save(packet);
    }

    @Override
    public PacketValidationDTO updatePacketValid(String barCode, String type) {
        Packet nonValidPacket = getPacketByBarcode(barCode);
        Packet packet;
        try {
            if (type.equals(CONFIRMED.getStatus())) {
                nonValidPacket.setValid(true);
                packet = packetRepository.save(nonValidPacket);
            } else packet = updatePacketStatus(nonValidPacket, RETURN_RECEIVED.getStatus());
        } catch (Exception e) {
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
            switch (firstKey) {
                case "status" -> {
                    packet = getPacketById(idPacket);
                    if (value.equals(CONFIRMED.getStatus()))
                        createBarCode(packet);
                    updatePacketStatus(packet, value);
                }
                case "customerPhoneNb" -> {
                    int existCount = 0;
                    if (field.get(firstKey) != "" && field.get(firstKey) != null)
                        existCount = checkPhone(field.get(firstKey) + "");
                    packetRepository.savePhoneNumber(idPacket, value, existCount);
                }
                case "fbPage" -> packetRepository.saveFbPage(idPacket, Long.valueOf(value));
                case "city" -> packetRepository.saveCity(idPacket, Long.valueOf(value));
                case "customerName" -> packetRepository.saveCustomerName(idPacket, value);
                case "address" -> packetRepository.saveAddress(idPacket, value);
                case "date" -> {
                    packetRepository.saveDate(
                            idPacket,
                            new SimpleDateFormat("yyyy-MM-dd'T'HH:mm").parse(value));
                }
                case "barcode" -> packetRepository.saveBarcode(idPacket, value);
                default -> {
                    if(fieldPacket != null){
                        packet = getPacketById(idPacket);
                        ReflectionUtils.setField(fieldPacket, packet, field.get(firstKey));
                        packetRepository.save(packet);
                    }

                }
            }
            packet = packetRepository.findById(idPacket).orElseThrow(() -> new PacketNotFoundException(idPacket,"Packet not found!"));
        }
        return packet;
    }

    @Transactional("tenantTransactionManager")
    public List<ProductsPacketDTO> findPacketRelatedProducts(Long packetId) {
        List<ProductsPacket> productsPackets = productsPacketRepository.findByPacketId(packetId);
        return groupProductsPackets(productsPackets);
    }

    private List<ProductsPacketDTO> groupProductsPackets(List<ProductsPacket> productsPackets) {
        Map<Long, List<ProductsPacket>> groupedByOfferId = productsPackets.stream()
                .collect(Collectors.groupingBy(ProductsPacket::getPacketOfferId));

        List<ProductsPacketDTO> productsPacketDTOs = new ArrayList<>();

        for (Map.Entry<Long, List<ProductsPacket>> entry : groupedByOfferId.entrySet()) {
            Long packetOfferId = entry.getKey();
            List<ProductsPacket> groupedPackets = entry.getValue();
            Long offerId = groupedPackets.get(0).getOffer().getId();

            List<Long> productIds = groupedPackets.stream()
                    .map(pp -> pp.getProduct().getId())
                    .collect(Collectors.toList());

            List<Long> modelIds = groupedPackets.stream()
                    .map(pp -> pp.getProduct().getModel().getId())
                    .collect(Collectors.toList());

            List<ProductResponse> products = productRepository.getProductsByModelIds(modelIds).stream().map(product -> new ProductResponse(product)).collect(Collectors.toList());

            ProductsPacketDTO dto = new ProductsPacketDTO(
                    offerId,
                    packetOfferId,
                    productIds,
                    products
            );

            productsPacketDTOs.add(dto);
        }

        return productsPacketDTOs;
    }

    @Override
    public int checkPhone(String phoneNumber) {
        return packetRepository.findAllPacketsByPhone_number(phoneNumber);
    }

    @Override
    public List<DashboardCard> syncNotification(String beginDate, String endDate) {
        return packetRepository.createNotification(beginDate, endDate);
    }

    @Override
    public List<PacketStatusDTO> findPacketTimeLineById(Long idPacket) throws PacketNotFoundException {
        return packetRepository.findById(idPacket)
                .orElseThrow(() -> new PacketNotFoundException(idPacket,"Packet not found!"))
                .getPacketStatus().stream()
                .map(PacketStatusDTO::new)
                .collect(Collectors.toList());
    }

    @Override
    public DeliveryResponse createBarCode(Packet packet) throws IOException {
        DeliveryResponse deliveryResponse = deliveryCompanyServiceFactory
                .getDeliveryCompanyService(DeliveryCompanyName.fromString(packet.getDeliveryCompany().getName()))
                .createBarCode(packet);

        LOGGER.debug("deliveryResponse : " + deliveryResponse);
        if (deliveryResponse.getResponseCode() == 200 || deliveryResponse.getResponseCode() == 201) {
            packet.setPrintLink(deliveryResponse.getLink());
            packet.setBarcode(deliveryResponse.getBarCode());
            packet.setDate(new Date());
            packetRepository.save(packet);
            return deliveryResponse;
        }
        return null;
    }


    @Override
    public PacketDTO getLastStatus(long packetId) throws Exception {
        Packet packet = packetRepository.findById(packetId).orElseThrow(() -> new Exception("Packet not found with ID: " + packetId));
        User currentUser = sessionUtils.getCurrentUser();
        return getLastStatus(packet, currentUser);
    }

    @Override
    @Transactional("tenantTransactionManager")
    public PacketDTO getLastStatus(Packet packet, User currentUser) {
        try {
            DeliveryCompanyName deliveryCompanyName = DeliveryCompanyName.fromString(packet.getDeliveryCompany().getName());
            DeliveryCompanyService deliveryCompanyService = deliveryCompanyServiceFactory.getDeliveryCompanyService(deliveryCompanyName);
            DeliveryResponse deliveryResponse = deliveryCompanyService.getLastStatus(packet.getBarcode(), packet.getDeliveryCompany());
            if (deliveryResponse.getState() != null) {
                String deliveryState = deliveryResponse.getState();
                String packetStatus = packet.getStatus();
                LOGGER.debug("deliveryResponse : " + deliveryResponse);
                if (deliveryResponse.getResponseCode() == 200 || deliveryResponse.getResponseCode() == 201 || deliveryResponse.getResponseCode() == 404) {
                    String systemNewStatus = TO_VERIFY.getStatus();

                    if (deliveryResponse.getStatus() == 404 || deliveryState.equals("")) {
                        throw new Exception("Problem API");
                    } else if (deliveryResponse.getStatus() > 199) {
                        //Convert input from first to System Status

                        packet.getPacketStatus().add(new PacketStatus(deliveryCompanyName + ":" + deliveryState, packet, currentUser));
                        //this.savePacketStatusToHistory(packet,deliveryCompanyName+":"+deliveryState);

                        DeliveryCompanyStatus dcStatus = DeliveryCompanyStatus.fromString(deliveryState, deliveryCompanyName);

                        systemNewStatus = mapDeliveryToSystemStatus(dcStatus);
                        packet.setLastDeliveryStatus(deliveryState);
                        if (dcStatus == DeliveryCompanyStatus.WAITING
                                || dcStatus == DeliveryCompanyStatus.RETOUR_DEPOT
                                || (dcStatus == DeliveryCompanyStatus.AU_MAGASIN
                                && packetStatus.equals(IN_PROGRESS_1.getStatus()))) {
                            return new PacketDTO(packet);
                        }

                        systemNewStatus =
                                (systemNewStatus.equals(IN_PROGRESS_1.getStatus())//First always return "en cours"
                                        || systemNewStatus.equals(IN_PROGRESS_2.getStatus())//not in First System
                                        || systemNewStatus.equals(IN_PROGRESS_3.getStatus()))
                                        && !packetStatus.equals(PROBLEM.getStatus())//not in First System
                                        ? upgradeInProgressStatus(packet) : systemNewStatus;
                    }
                    return new PacketDTO(updatePacketStatus(packet, systemNewStatus));
                }
            }
        } catch (Exception e) {
            LOGGER.error("Error " + e);
        }
        return null;
    }

    @Override
    public Packet addAttempt(Note note, Long packetId) throws PacketNotFoundException {
        Packet packet = packetRepository.findById(packetId)
                .orElseThrow(() -> new PacketNotFoundException(packetId," does not exist."));

        User currentUser = sessionUtils.getCurrentUser();
        note.setUser(currentUser);
        note.setPacket(packet);
        //noteRepository.save(note);
        packet.getNotes().add(note);

        packet.getPacketStatus().add(new PacketStatus("tentative: " + packet.getNotes().size() + " " + note.getExplanation(), packet, currentUser));
        //savePacketStatusToHistory(packet, "tentative: " + packet.getNotes().size() + " " + note.getExplanation());
        return packetRepository.save(packet);
    }

    private String mapDeliveryToSystemStatus(DeliveryCompanyStatus status) {
        if (status == null)
            return TO_VERIFY.getStatus();
        return switch (status) {
            case LIVREE, EXCHANGE -> LIVREE.getStatus();
            case RETOUR_EXPEDITEUR,
                    RETOUR_DEFINITIF, RETOUR_CLIENT_AGENCE,
                    RETOUR_RECU -> RETURN.getStatus();
            case ANNULER, WAITING, A_VERIFIER -> TO_VERIFY.getStatus();
            default -> IN_PROGRESS_1.getStatus();
        };
    }

    private String upgradeInProgressStatus(Packet packet) {
        DeliveryCompanyName deliveryCompanyName = DeliveryCompanyName.fromString(packet.getDeliveryCompany().getName());
        SystemStatus systemStatus = SystemStatus.fromString(packet.getStatus());
        if (checkSameDateStatus(packet) && !packet.getStatus().equals(CANCELED.getStatus()))
            return packet.getStatus();

        return switch (systemStatus) {
            case IN_PROGRESS_1 -> IN_PROGRESS_2.getStatus();
            case IN_PROGRESS_2 -> IN_PROGRESS_3.getStatus();
            case IN_PROGRESS_3 -> TO_VERIFY.getStatus();
            case TO_VERIFY ->
                    mapDeliveryToSystemStatus(DeliveryCompanyStatus.fromString(packet.getLastDeliveryStatus(), deliveryCompanyName));
            default -> IN_PROGRESS_1.getStatus();
        };
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
        GlobalConf globalConf = globalConfRepository.findAll().stream().findFirst().orElseThrow(() -> new Exception("Global configuration not found!"));
        Packet packet = packetRepository.findById(idPacket).orElseThrow(() -> new PacketNotFoundException(idPacket,"Packet not found!"));

        Packet newPacket = new Packet(packet, globalConf.getDeliveryCompany());

        packetRepository.save(newPacket);

        newPacket.setProductsPackets(packet.getProductsPackets().stream()
                .map(productPacket -> new ProductsPacket(productPacket, newPacket))
                .collect(Collectors.toList()));
        User currentUser = sessionUtils.getCurrentUser();
        newPacket.getPacketStatus().add(new PacketStatus(CREATION.getStatus(), newPacket, currentUser));

        packetRepository.save(newPacket);
        packet.setHaveExchange(true);
        packetRepository.save(packet);
        return new PacketDTO(newPacket);
    }

    public List<String> updatePacketsByBarCodes(BarCodeStatusDTO barCodeStatusDTO) {
        List<String> errors = new ArrayList<>();
        String newState = barCodeStatusDTO.getStatus();
        barCodeStatusDTO.getBarCodes().forEach(barCode -> {
            try {
                Packet packet = getPacketByBarcode(barCode);
                //System.out.println("updatePacketsByBarCodes"+barCode);
                if (!packet.getStatus().equals(RETURN_RECEIVED.getStatus())) {
                    updatePacketStatus(packet, newState);
                } else {
                    errors.add(barCode + " déja récu");
                }
            } catch (Exception e) {
                errors.add(barCode + " erreur");
                e.printStackTrace();
            }
        });
        return errors;
    }

    public Packet updatePacketStatus(Packet packet, String status) {
        if (packet.getExchangeId() != null) {
            if (status.equals(PAID.getStatus()) || status.equals(LIVREE.getStatus()))
                return updateExchangePacketStatusToPaid(packet, status);
            if (status.equals(RETURN_RECEIVED.getStatus())) {
                return updateExchangePacketStatusToReturnReceived(packet);
            }
        }
        return updatePacketStatusAndSaveToHistory(packet, status);
    }

    public Packet updateExchangePacketStatusToReturnReceived(Packet packet) {
        Long id = packet.getExchangeId();
        Packet exchangePacket = getPacketById(id);
        if (packet.getStatus().equals(LIVREE.getStatus())
                || packet.getStatus().equals(PAID.getStatus())
                || exchangePacket.getStatus().equals(RETURN.getStatus())
                || exchangePacket.getStatus().equals(RETURN_RECEIVED.getStatus()))
            return updatePacketStatusAndSaveToHistory(exchangePacket, RETURN_RECEIVED.getStatus());
        else return updatePacketStatusAndSaveToHistory(packet, RETURN_RECEIVED.getStatus());
    }

    public Packet updateExchangePacketStatusToPaid(Packet packet, String status) {
        Long id = packet.getExchangeId();
        Packet exchangePacket = getPacketById(id);
        packet.setPrice(exchangePacket.getPrice() - exchangePacket.getDiscount() + packet.getPrice() - packet.getDiscount());
        if (!exchangePacket.getStatus().equals(RETURN_RECEIVED.getStatus()))
            updatePacketStatusAndSaveToHistory(exchangePacket, RETURN.getStatus());
        return updatePacketStatusAndSaveToHistory(packet, status);
    }

    public Packet updatePacketStatusAndSaveToHistory(Packet packet, String status) {

        if (
                !packet.getStatus().equals(status) &&
                        !(
                                packet.getStatus().equals(RETURN.getStatus()) &&
                                        !(
                                                status.equals(RETURN_RECEIVED.getStatus())
                                                        || status.equals(PROBLEM.getStatus())
                                        )
                        )
        ) {
            //updateProducts_Status(packet, status);
            updateProducts_Quantity(packet, status);
            User currentUser = sessionUtils.getCurrentUser();
            packet.getPacketStatus().add(new PacketStatus(status, packet, currentUser));

            return savePacketStatus(packet, status);
        }
        return packet;
    }

    public void updateProducts_Quantity(Packet packet, String status) {
        if (status.equals(RETURN_RECEIVED.getStatus())
                || status.equals(CONFIRMED.getStatus())
                || status.equals(CANCELED.getStatus()))
            updateProductsQuantity(packet, status);
    }

    public void updateProductsQuantity(Packet packet, String status) {
        int quantity = 0;
        if (status.equals(RETURN_RECEIVED.getStatus()) || status.equals(CANCELED.getStatus())) quantity = 1;
        if (status.equals(CONFIRMED.getStatus())) quantity = -1;
        List<ProductsPacket> productsPackets = productsPacketRepository.findByPacketId(packet.getId());
        if (!productsPackets.isEmpty()) {
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

    @Override
    public PacketDTO addProductsToPacket(SelectedProductsDTO selectedProductsDTO) {
        String packetStatus = selectedProductsDTO.getStatus();
        Packet packet = getPacketById(selectedProductsDTO.getIdPacket());

        if (packetStatus != null && packetStatus.equals(OOS.getStatus()) &&
                (packet.getStatus().equals(NOT_CONFIRMED.getStatus()) ||
                        packet.getStatus().equals(NOTSERIOUS.getStatus()) ||
                        packet.getStatus().equals(CANCELED.getStatus()) ||
                        packet.getStatus().equals(UNREACHABLE.getStatus()))) {
            packet.setStatus(OOS.getStatus());
        }

        addProductsToPacket(packet,selectedProductsDTO.getProductsOffers().stream()
                .map(productOffer -> addProductPacket(packet, productOffer))
                .toList());
        packet.setPacketDescription(selectedProductsDTO.getPacketDescription());
        packet.setPrice(selectedProductsDTO.getTotalPrice());
        packet.setDeliveryPrice(selectedProductsDTO.getDeliveryPrice());
        packet.setDiscount(selectedProductsDTO.getDiscount());
        packet.setProductCount(selectedProductsDTO.getProductCount());
        return new PacketDTO(packetRepository.save(packet));
    }

    @Transactional
    public void addProductsToPacket(Packet packet, List<ProductsPacket> productsPackets) {
        for (ProductsPacket productPacket : productsPackets) {
            productPacket.setPacket(packet); // Set the reference to the packet
        }
        packet.getProductsPackets().clear(); // Clear existing productsPackets if necessary
        packet.getProductsPackets().addAll(productsPackets);
        packetRepository.save(packet); // Save the packet to persist changes
    }

    private ProductsPacket addProductPacket(Packet packet, ProductOfferDTO productsPacket) {
        return new ProductsPacket(
                new Product(productsPacket.getProductId()),
                packet,
                new Offer(productsPacket.getOfferId()),
                productsPacket.getPacketOfferIndex(),
                productsPacket.getProfits()
        );
    }

    @Override
    @Transactional("tenantTransactionManager")
    public List<PacketDTO> deleteSelectedPackets(List<Long> packetsId, Note note) throws PacketNotFoundException {
        User currentUser = sessionUtils.getCurrentUser();
        List<Packet> packets = new ArrayList<>();

        try {
            for (Long packetId : packetsId) {
                try {
                    Packet packet = packetRepository.findById(packetId)
                            .orElseThrow(() -> new PacketNotFoundException(packetId,"does not exist."));

                    if (packet.getCustomerPhoneNb() == null || packet.getCustomerPhoneNb().isEmpty()) {
                        packetRepository.deleteById(packetId);
                    } else {
                        note.setUser(currentUser);
                        note.setPacket(packet);
                        packet.getNotes().add(note);
                        System.out.println("note.getStatus():"+note.getStatus());
                        packet.setStatus(DELETED.getStatus());

                        packet.getPacketStatus().add(new PacketStatus(DELETED.getStatus(), packet, currentUser));
                        packets.add(packet);
                    }
                } catch (PacketNotFoundException e) {
                    // Handle the case where the packet is not found
                    // You might want to log this or notify the user
                    System.err.println("Error: " + e.getMessage());
                    // Optionally rethrow the exception if you want it to propagate
                    throw e;
                } catch (Exception e) {
                    // Handle other unexpected exceptions
                    System.err.println("An unexpected error occurred: " + e.getMessage());
                    // Optionally rethrow the exception if you want it to propagate
                    throw e;
                }
            }

            // Save all packets that were processed
            return packetRepository.saveAll(packets).stream().map(PacketDTO::new).collect(Collectors.toList());

        } catch (Exception e) {
            // Handle any error that might have occurred during the process
            System.err.println("Failed to delete selected packets: " + e.getMessage());
            // Optionally rethrow the exception if you want it to propagate
            throw e;
        }
    }
}