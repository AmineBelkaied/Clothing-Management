package com.clothing.management.servicesImpl;
import com.clothing.management.auth.util.SessionUtils;
import com.clothing.management.dto.*;
import com.clothing.management.dto.DeliveryCompanyDTOs.BarcodeStatusDTO;
import com.clothing.management.dto.DeliveryCompanyDTOs.DeliveryResponse;
import com.clothing.management.enums.DeliveryCompanyName;
import com.clothing.management.enums.DeliveryCompanyStatus;
import com.clothing.management.enums.SystemStatus;
import com.clothing.management.exceptions.custom.notfound.PacketNotFoundException;
import com.clothing.management.exceptions.generic.EntityNotFoundException;
import com.clothing.management.mappers.PacketMapper;
import com.clothing.management.mappers.PacketStatusMapper;
import com.clothing.management.models.DashboardCard;
import com.clothing.management.servicesImpl.api.DeliveryCompanyService;
import com.clothing.management.servicesImpl.api.DeliveryCompanyServiceFactory;
import com.clothing.management.entities.*;
import com.clothing.management.repository.*;
import com.clothing.management.services.PacketService;
import com.clothing.management.utils.EntityBuilderHelper;
import com.clothing.management.utils.PacketBuilderHelper;
import com.clothing.management.utils.SystemStatusUtil;
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
import java.util.stream.Stream;

import static com.clothing.management.enums.SystemStatus.*;

@Transactional("tenantTransactionManager")
@Service
public class PacketServiceImpl implements PacketService {

    private static final Logger LOGGER = LoggerFactory.getLogger(PacketServiceImpl.class);
    private final IPacketRepository packetRepository;
    private final IProductRepository productRepository;
    private final IProductsPacketRepository productsPacketRepository;
    private final DeliveryCompanyServiceFactory deliveryCompanyServiceFactory;
    private final SessionUtils sessionUtils;
    private final IGlobalConfRepository globalConfRepository;
    private final PacketBuilderHelper packetBuilderHelper;
    private final EntityBuilderHelper entityBuilderHelper;
    private final PacketMapper packetMapper;
    private final PacketStatusMapper packetStatusMapper;

    @Autowired
    public PacketServiceImpl(
            IPacketRepository packetRepository,
            IProductRepository productRepository,
            IProductsPacketRepository productsPacketRepository,
            DeliveryCompanyServiceFactory deliveryCompanyServiceFactory,
            SessionUtils sessionUtils,
            IGlobalConfRepository globalConfRepository, PacketBuilderHelper packetBuilderHelper,
            EntityBuilderHelper entityBuilderHelper, PacketMapper packetMapper, PacketStatusMapper packetStatusMapper) {
        this.packetRepository = packetRepository;
        this.productRepository = productRepository;
        this.productsPacketRepository = productsPacketRepository;
        this.deliveryCompanyServiceFactory = deliveryCompanyServiceFactory;
        this.globalConfRepository = globalConfRepository;
        this.sessionUtils = sessionUtils;
        this.packetBuilderHelper = packetBuilderHelper;
        this.entityBuilderHelper = entityBuilderHelper;
        this.packetMapper = packetMapper;
        this.packetStatusMapper = packetStatusMapper;
    }

    @Override
    public Packet getPacketById(Long packetId) throws PacketNotFoundException {
        LOGGER.info("Attempting to retrieve packet with ID: {}", packetId);

        return packetRepository.findById(packetId)
                .orElseThrow(() -> {
                    LOGGER.error("Packet with ID: {} not found", packetId);
                    return new PacketNotFoundException(packetId, "Packet not found!");
                });
    }

    @Override
    public Packet getPacketByBarcode(String barcode) throws EntityNotFoundException {
        LOGGER.info("Attempting to retrieve packet with Barcode: {}", barcode);

        return packetRepository.findByBarcode(barcode)
                .orElseThrow(() -> {
                    LOGGER.error("Packet with Barcode: {} not found", barcode);
                    return new EntityNotFoundException("BarCode", 0L, barcode);
                });
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
            Date begin = dateFormat.parse(beginDate);
            Date end = dateFormat.parse(endDate);

            if (searchText != null) {
                LOGGER.debug("Searching packets by field and date.");
                Page<PacketDTO> result = packetRepository.findAllPacketsByFieldAndDate(searchText, begin, end, pageable)
                        .map(packetMapper::toDto);
                LOGGER.info("Found {} packets matching search text and date.", result.getTotalElements());
                return result;
            }

            if (status != null) {
                if (status.equals("Tous"))
                    return packetRepository.findAllPacketsByDate(dateFormat.parse(beginDate), dateFormat.parse(endDate), pageable)
                            .map(packetMapper::toDto);
                return packetRepository.findAllPacketsByDateAndStatus(dateFormat.parse(beginDate), dateFormat.parse(endDate), convertStatusToList(status), pageable)
                        .map(packetMapper::toDto);
            }

        } else {
            if (searchText != null) {
                LOGGER.debug("Searching packets by field.");
                Page<PacketDTO> result = packetRepository.findAllPacketsByField(searchText, pageable)
                        .map(packetMapper::toDto);
                LOGGER.info("Found {} packets matching search text.", result.getTotalElements());
                return result;
            }
            if (beginDate != null && status != null) {
                if (status.equals("Tous"))
                    return packetRepository.findAllPacketsByDate(dateFormat.parse(beginDate), dateFormat.parse(endDate), pageable)
                            .map(packetMapper::toDto);
                return packetRepository.findAllPacketsByStatus(SystemStatusUtil.getIgnoredDateStatusList(), convertStatusToList(status), dateFormat.parse(beginDate), dateFormat.parse(endDate), pageable)
                        .map(packetMapper::toDto);
            }

            if (status != null) {
                LOGGER.debug("Searching packets by status.");
                Page<PacketDTO> result = packetRepository.findAllPacketsByStatus(convertStatusToList(status), pageable)
                        .map(packetMapper::toDto);
                LOGGER.info("Found {} packets matching status.", result.getTotalElements());
                return result;
            }
        }

        LOGGER.debug("Searching packets by date.");
        Page<PacketDTO> result = packetRepository.findAllPacketsByDate(dateFormat.parse(beginDate), dateFormat.parse(endDate), pageable)
                .map(packetMapper::toDto);
        LOGGER.info("Found {} packets matching date.", result.getTotalElements());
        return result;
    }


    @Override
    @Transactional(readOnly = true, transactionManager = "tenantTransactionManager")
    public List<PacketValidationDTO> findValidationPackets() {
        return packetRepository.findValidationPackets().stream()
                .map(packetMapper::toValidationDto)
                .collect(Collectors.toList());
    }

    private List<String> convertStatusToList(String status) {
        LOGGER.debug("Converting status string to list: {}", status);

        List<String> statusList = Arrays.asList(status.split(",", -1));

        LOGGER.debug("Converted status list: {}", statusList);
        return statusList;
    }

    public List<PacketDTO> findAllPacketsByDate(String beginDate, String endDate) throws ParseException {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        List<PacketDTO> packets = packetRepository.findAllPacketsByDate(dateFormat.parse(beginDate), dateFormat.parse(endDate))
                .stream().map(packetMapper::toDto).collect(Collectors.toList());

        LOGGER.info("Retrieved {} packets between {} and {}.", packets.size(), beginDate, endDate);
        return packets;
    }

    @Override
    public List<Packet> findSyncPackets() {
        LOGGER.info("Finding synchronized packets with predefined statuses.");

        List<SystemStatus> status = Arrays.asList(
                PAID,
                CANCELED,
                DELIVERED,
                DELETED,
                RETURN,
                NOT_SERIOUS,
                RETURN_RECEIVED,
                PROBLEM
        );

        List<Packet> syncPackets = packetRepository.findAllDiggiePackets(Stream.of(status).map(String::valueOf).toList());

        LOGGER.info("Retrieved {} synchronized packets.", syncPackets.size());
        return syncPackets;
    }


    @Override
    public List<Packet> findAllPacketsByDate(Date date) {
        List<Packet> packets = packetRepository.findAllByDate(date);

        LOGGER.info("Retrieved {} packets for date: {}", packets.size(), date);
        return packets;
    }

    @Override
    public int deleteEmptyPacket() {
        LOGGER.info("Deleting empty packets.");

        int deletedCount = packetRepository.deleteEmptyPacket();

        LOGGER.info("Deleted {} empty packets.", deletedCount);
        return deletedCount;
    }

    @Override
    public Packet addPacket() throws Exception {
        GlobalConf globalConf = globalConfRepository.findAll().stream()
                .findFirst()
                .orElseThrow(() -> new Exception("globalConf not found"));

        Packet packet = Packet.builder()
                .deliveryCompany(globalConf.getDeliveryCompany())
                .build();
        User currentUser = sessionUtils.getCurrentUser();
            packet.setFbPage(globalConf.getFbPage());
        packet.getPacketStatus().add(entityBuilderHelper.createPacketStatusBuilder(CREATION.getStatus(), packet, currentUser).build());
        Packet savedPacket = packetRepository.save(packet);

        LOGGER.info("Added new packet with ID: {}", savedPacket.getId());
        return savedPacket;
    }

    @Override
    public Packet updatePacket(Packet packet) {
        Packet updatedPacket = packetRepository.save(packet);

        LOGGER.info("Updated packet with ID: {}", updatedPacket.getId());
        return updatedPacket;
    }

    @Override
    public PacketValidationDTO updatePacketValid(String barcode, String type) {
        Packet nonValidPacket;
        try {
            nonValidPacket = getPacketByBarcode(barcode);
            if (nonValidPacket == null) {
                LOGGER.warn("No packet found for barcode: {}", barcode);
                return null;
            }
            Packet packet;
            if (type.equals(CONFIRMED.getStatus())) {
                nonValidPacket.setValid(true);
                packet = packetRepository.save(nonValidPacket);
            } else {
                packet = updatePacketStatus(nonValidPacket, RETURN_RECEIVED);
            }
            return packetMapper.toValidationDto(packet);

        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public Packet patchPacket(Long idPacket, Map<String, Object> field) throws Exception {
        Packet packet = null;
        Optional<String> firstKeyOptional = field.keySet().stream().findFirst();
        if (firstKeyOptional.isPresent()) {
            String firstKey = firstKeyOptional.get();
            Field fieldPacket = ReflectionUtils.findField(Packet.class, firstKey);
            String value = String.valueOf(field.get(firstKey));
            LOGGER.debug("Processing field: {} with value: {}", firstKey, value);

            try {
                switch (firstKey) {
                    case "status" -> {
                        packet = getPacketById(idPacket);
                        if (value.equals(CONFIRMED.getStatus())) {
                            createBarCode(packet);
                        }
                        updatePacketStatus(packet, SystemStatus.fromKey(value));
                        LOGGER.info("Updated status for packet ID: {} to {}", idPacket, value);
                    }
                    case "customerPhoneNb" -> {
                        int existCount = 0;
                        if (!value.isEmpty()) {
                            existCount = checkPhone(value);
                        }
                        packetRepository.savePhoneNumber(idPacket, value, existCount);
                        LOGGER.info("Updated customer phone number for packet ID: {}", idPacket);
                    }
                    case "fbPage" -> {
                        packetRepository.saveFbPage(idPacket, Long.valueOf(value));
                        LOGGER.info("Updated Facebook page ID for packet ID: {}", idPacket);
                    }
                    case "city" -> {
                        packetRepository.saveCity(idPacket, Long.valueOf(value));
                        LOGGER.info("Updated city ID for packet ID: {}", idPacket);
                    }
                    case "customerName" -> {
                        packetRepository.saveCustomerName(idPacket, value);
                        LOGGER.info("Updated customer name for packet ID: {}", idPacket);
                    }
                    case "address" -> {
                        packetRepository.saveAddress(idPacket, value);
                        LOGGER.info("Updated address for packet ID: {}", idPacket);
                    }
                    case "date" -> {
                        packetRepository.saveDate(
                                idPacket,
                                new SimpleDateFormat("yyyy-MM-dd'T'HH:mm").parse(value));
                        LOGGER.info("Updated date for packet ID: {}", idPacket);
                    }
                    case "barcode" -> {
                        packetRepository.saveBarcode(idPacket, value);
                        LOGGER.info("Updated barcode for packet ID: {}", idPacket);
                    }
                    default -> {
                        if (fieldPacket != null) {
                            packet = getPacketById(idPacket);
                            ReflectionUtils.setField(fieldPacket, packet, field.get(firstKey));
                            packetRepository.save(packet);
                            LOGGER.info("Updated custom field '{}' for packet ID: {}", firstKey, idPacket);
                        }
                    }
                }
            } catch (Exception e) {
                LOGGER.error("Error updating packet with ID: {}. Field: {}. Error: {}", idPacket, firstKey, e.getMessage(), e);
                throw e; // rethrow the exception after logging
            }

            packet = packetRepository.findById(idPacket)
                    .orElseThrow(() -> new PacketNotFoundException(idPacket, "Packet not found!"));

            LOGGER.info("Successfully retrieved updated packet with ID: {}", idPacket);
        } else {
            LOGGER.warn("No fields provided to patch for packet ID: {}", idPacket);
        }

        return packet;
    }

    @Transactional("tenantTransactionManager")
    public List<ProductsPacketDTO> findPacketRelatedProducts(Long packetId) {
        List<ProductsPacket> productsPackets = productsPacketRepository.findByPacketId(packetId);

        if (productsPackets.isEmpty()) {
            LOGGER.warn("No related products found for packet ID: {}", packetId);
        } else {
            LOGGER.info("Found {} related products for packet ID: {}", productsPackets.size(), packetId);
        }

        return groupProductsPackets(productsPackets);
    }

    private List<ProductsPacketDTO> groupProductsPackets(List<ProductsPacket> productsPackets) {
        LOGGER.debug("Grouping {} ProductsPacket objects by offer ID.", productsPackets.size());

        Map<Long, List<ProductsPacket>> groupedByOfferId = productsPackets.stream()
                .collect(Collectors.groupingBy(ProductsPacket::getPacketOfferId));

        List<ProductsPacketDTO> productsPacketDTOs = new ArrayList<>();

        for (Map.Entry<Long, List<ProductsPacket>> entry : groupedByOfferId.entrySet()) {
            Long packetOfferId = entry.getKey();
            List<ProductsPacket> groupedPackets = entry.getValue();

            LOGGER.debug("Processing offer ID: {} with {} grouped packets.", packetOfferId, groupedPackets.size());

            Long offerId = groupedPackets.get(0).getOffer().getId();

            List<Long> productIds = groupedPackets.stream()
                    .map(pp -> pp.getProduct().getId())
                    .collect(Collectors.toList());

            List<Long> modelIds = groupedPackets.stream()
                    .map(pp -> pp.getProduct().getModel().getId())
                    .collect(Collectors.toList());

            LOGGER.debug("Retrieving products for model IDs: {}", modelIds);

            List<ProductResponse> products = productRepository.getProductsByModelIds(modelIds).stream()
                    .map(ProductResponse::new)
                    .collect(Collectors.toList());

            ProductsPacketDTO dto = new ProductsPacketDTO(
                    offerId,
                    packetOfferId,
                    productIds,
                    products
            );

            productsPacketDTOs.add(dto);

            LOGGER.debug("Created ProductsPacketDTO for offer ID: {}", offerId);
        }

        LOGGER.info("Grouped and created {} ProductsPacketDTO objects.", productsPacketDTOs.size());

        return productsPacketDTOs;
    }


    @Override
    public int checkPhone(String phoneNumber) {
        LOGGER.info("Checking phone number: {}", phoneNumber);
        int count = packetRepository.findAllPacketsByPhone_number(phoneNumber, String.valueOf(NOT_SERIOUS));
        LOGGER.debug("Found {} packets with phone number: {}", count, phoneNumber);
        return count;
    }

    @Override
    public List<DashboardCard> syncNotification(String beginDate, String endDate) {
        List<DashboardCard> notifications = packetRepository.createNotification(beginDate, endDate, String.valueOf(PROBLEM));
        LOGGER.debug("Retrieved {} notifications.", notifications.size());
        return notifications;
    }

    @Override
    public List<PacketStatusDTO> findPacketTimeLineById(Long idPacket) throws PacketNotFoundException {
        Packet packet = packetRepository.findById(idPacket)
                .orElseThrow(() -> {
                    LOGGER.error("Packet with ID {} not found.", idPacket);
                    return new PacketNotFoundException(idPacket, "Packet not found!");
                });

        List<PacketStatusDTO> packetStatusDTOs = packet.getPacketStatus().stream()
                .map(packetStatusMapper::toDto)
                .collect(Collectors.toList());

        LOGGER.debug("Found {} status updates for packet ID: {}", packetStatusDTOs.size(), idPacket);
        return packetStatusDTOs;
    }

    @Override
    public DeliveryResponse createBarCode(Packet packet) throws IOException {
        LOGGER.info("Creating barcode for packet ID: {}", packet.getId());

        DeliveryResponse deliveryResponse = deliveryCompanyServiceFactory
                .getDeliveryCompanyService(DeliveryCompanyName.fromString(packet.getDeliveryCompany().getName()))
                .createBarCode(packet);

        LOGGER.debug("Delivery response: {}", deliveryResponse);

        if (deliveryResponse.getResponseCode() == 200 || deliveryResponse.getResponseCode() == 201) {
            packet.setPrintLink(deliveryResponse.getLink());
            packet.setBarcode(deliveryResponse.getBarcode());
            packet.setDate(new Date());
            packetRepository.save(packet);
            return deliveryResponse;
        } else {
            return null;
        }
    }



    @Override
    public PacketDTO getLastStatus(long packetId) throws Exception {
        Packet packet = packetRepository.findById(packetId)
                .orElseThrow(() -> {
                    LOGGER.error("Packet not found with ID: {}", packetId);
                    return new Exception("Packet not found with ID: " + packetId);
                });

        User currentUser = sessionUtils.getCurrentUser();
        return getLastStatus(packet, currentUser);
    }

    @Override
    @Transactional("tenantTransactionManager")
    public PacketDTO getLastStatus(Packet packet, User currentUser) {
        LOGGER.info("Fetching last status for packet with barcode: {}", packet.getBarcode());

        try {
            DeliveryCompanyName deliveryCompanyName = DeliveryCompanyName.fromString(packet.getDeliveryCompany().getName());
            DeliveryCompanyService deliveryCompanyService = deliveryCompanyServiceFactory.getDeliveryCompanyService(deliveryCompanyName);
            DeliveryResponse deliveryResponse = deliveryCompanyService.getLastStatus(packet.getBarcode(), packet.getDeliveryCompany());

            LOGGER.debug("Received delivery response: {}", deliveryResponse);

            if (deliveryResponse.getState() != null) {
                String deliveryState = deliveryResponse.getState();
                SystemStatus packetStatus = SystemStatus.valueOf(packet.getStatus());

                if (deliveryResponse.getResponseCode() == 200 || deliveryResponse.getResponseCode() == 201 || deliveryResponse.getResponseCode() == 404) {
                    SystemStatus systemNewStatus = TO_VERIFY;

                    if (deliveryResponse.getStatus() == 404 || deliveryState.isEmpty()) {
                        LOGGER.error("API returned 404 or empty state for barcode: {}", packet.getBarcode());
                        throw new Exception("Problem API");
                    } else if (deliveryResponse.getStatus() > 199) {
                        packet.getPacketStatus().add(entityBuilderHelper.createPacketStatusBuilder(deliveryCompanyName + ":" + deliveryState, packet, currentUser).build());

                        DeliveryCompanyStatus dcStatus = DeliveryCompanyStatus.fromString(deliveryState, deliveryCompanyName);

                        systemNewStatus = mapDeliveryToSystemStatus(dcStatus);
                        packet.setLastDeliveryStatus(deliveryState);

                        if (dcStatus == DeliveryCompanyStatus.WAITING
                                || dcStatus == DeliveryCompanyStatus.RETOUR_DEPOT
                                || (dcStatus == DeliveryCompanyStatus.AU_MAGASIN
                                && packetStatus.equals(IN_PROGRESS_1))) {
                            return packetMapper.toDto(packet);
                        }

                        systemNewStatus = (systemNewStatus.equals(IN_PROGRESS_1) // First always return "en cours"
                                || systemNewStatus.equals(IN_PROGRESS_2) // Not in First System
                                || systemNewStatus.equals(IN_PROGRESS_3)) // Not in First System
                                && !packetStatus.equals(PROBLEM) // Not in First System
                                ? upgradeInProgressStatus(packet) : systemNewStatus;
                    }
                    return packetMapper.toDto(updatePacketStatus(packet, systemNewStatus));
                }
            }
        } catch (Exception e) {
            LOGGER.error("Error fetching last status for packet with barcode: {}. Exception: {}", packet.getBarcode(), e.getMessage());
        }

        return null;
    }


    @Override
    public Packet addAttempt(Note note, Long packetId) throws PacketNotFoundException {

        Packet packet = packetRepository.findById(packetId)
                .orElseThrow(() -> {
                    LOGGER.error("Packet with ID {} does not exist.", packetId);
                    return new PacketNotFoundException(packetId, " does not exist.");
                });

        User currentUser = sessionUtils.getCurrentUser();
        note.setUser(currentUser);
        note.setPacket(packet);

        // Uncomment these lines if you want to save the note and packet status to history
        // noteRepository.save(note);

        packet.getNotes().add(note);

        packet.getPacketStatus().add(entityBuilderHelper
                .createPacketStatusBuilder("tentative: " + packet.getNotes().size() + " " + note.getExplanation(), packet, currentUser)
                .build());

        // Uncomment this line if you want to save packet status to history
        // savePacketStatusToHistory(packet, statusMessage);

        Packet updatedPacket = packetRepository.save(packet);

        LOGGER.info("Note added and packet updated. Packet ID: {}", packetId);
        return updatedPacket;
    }

    private SystemStatus mapDeliveryToSystemStatus(DeliveryCompanyStatus status) {
        if (status == null) {
            LOGGER.debug("DeliveryCompanyStatus is null, returning default status.");
            return TO_VERIFY;
        }

        SystemStatus systemStatus = switch (status) {
            case DELIVERED, EXCHANGE -> DELIVERED;
            case RETOUR_EXPEDITEUR,
                    RETOUR_DEFINITIF, RETOUR_CLIENT_AGENCE,
                    RETOUR_RECU -> RETURN;
            case ANNULER, WAITING, A_VERIFIER -> TO_VERIFY;
            default -> IN_PROGRESS_1;
        };

        LOGGER.debug("Mapped DeliveryCompanyStatus {} to SystemStatus {}", status, systemStatus);
        return systemStatus;
    }

    private SystemStatus upgradeInProgressStatus(Packet packet) {
        LOGGER.debug("Upgrading status for packet with ID: {}", packet.getId());

        DeliveryCompanyName deliveryCompanyName = DeliveryCompanyName.fromString(packet.getDeliveryCompany().getName());
        SystemStatus systemStatus = SystemStatus.valueOf(packet.getStatus());

        if (checkSameDateStatus(packet) && !SystemStatus.valueOf(packet.getStatus()).equals(CANCELED)) {
            LOGGER.debug("Status has not changed for the same date. Current status: {}", packet.getStatus());
            return SystemStatus.valueOf(packet.getStatus());
        }

        SystemStatus upgradedStatus = switch (systemStatus) {
            case IN_PROGRESS_1 -> IN_PROGRESS_2;
            case IN_PROGRESS_2 -> IN_PROGRESS_3;
            case IN_PROGRESS_3 -> TO_VERIFY;
            case TO_VERIFY -> mapDeliveryToSystemStatus(DeliveryCompanyStatus.fromString(packet.getLastDeliveryStatus(), deliveryCompanyName));
            default -> IN_PROGRESS_1;
        };

        LOGGER.debug("Upgraded status: {}", upgradedStatus);
        return upgradedStatus;
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

        boolean sameDate = (day1 == day2) && (year1 == year2);
        LOGGER.debug("Checking same date status. Packet date: {} (Day: {}, Year: {}), Current date: {} (Day: {}, Year: {}). Same date: {}",
                packet.getLastUpdateDate(), day1, year1, date, day2, year2, sameDate);

        return sameDate;
    }

        @Transactional("tenantTransactionManager")
        public PacketDTO duplicatePacket(Long idPacket) throws Exception {
            GlobalConf globalConf = globalConfRepository.findAll().stream()
                    .findFirst()
                    .orElseThrow(() -> {
                        LOGGER.error("Global configuration not found!");
                        return new Exception("Global configuration not found!");
                    });

            Packet packet = packetRepository.findById(idPacket)
                    .orElseThrow(() -> {
                        LOGGER.error("Packet with ID {} not found!", idPacket);
                        return new PacketNotFoundException(idPacket, "Packet not found!");
                    });

            Packet newPacket = packetBuilderHelper.duplicatePacket(packet, globalConf.getDeliveryCompany());
            packetRepository.save(newPacket);

            newPacket.setProductsPackets(packet.getProductsPackets().stream()
                    .map(productPacket -> ProductsPacket.builder()
                        .packet(newPacket)
                        .product(productPacket.getProduct())
                        .packetOfferId(productPacket.getPacketOfferId())
                        .offer(productPacket.getOffer())
                        .build())
                    .collect(Collectors.toList()));

            User currentUser = sessionUtils.getCurrentUser();
            newPacket.getPacketStatus().add(entityBuilderHelper.createPacketStatusBuilder(CREATION.getStatus(), newPacket, currentUser).build());

            packetRepository.save(newPacket);
            packet.setHaveExchange(true);
            packetRepository.save(packet);

            LOGGER.info("Packet duplicated successfully. New packet ID: {}", newPacket.getId());
            return packetMapper.toDto(newPacket);
        }

        public List<String> updatePacketsByBarcodes(BarcodeStatusDTO barcodeStatusDTO) {
            LOGGER.info("Updating packets by barcodes. Status: {}", barcodeStatusDTO.getBarcodes());
            List<String> errors = new ArrayList<>();

            barcodeStatusDTO.getBarcodes().forEach(barcode -> {
                try {
                    Packet packet = getPacketByBarcode(barcode);
                    LOGGER.debug("Updating packet with barcode: {}",barcode);

                    if (!RETURN_RECEIVED.equals(SystemStatus.valueOf(packet.getStatus()))) {
                        updatePacketStatus(packet, PAID);
                    } else {
                        LOGGER.warn("Barcode {} already received",barcode);
                        errors.add(barcode + " déja récu");
                    }
                } catch (Exception e) {
                    LOGGER.error("Error updating packet with barcode {}: {}",barcode, e.getMessage());
                    errors.add(barcode + " erreur");
                }
            });

            return errors;
        }

        public Packet updatePacketStatus(Packet packet, SystemStatus status) {
            LOGGER.debug("Updating packet status. Packet ID: {}, New Status: {}", packet.getId(), status);

            if (packet.getExchangeId() != null) {
                if (status.equals(DELIVERED)) {
                    //status.equals(PAID.getStatus()) correction ahmed should add alert before paid if not received
                    return updateExchangePacketStatusToPaid(packet, status);
                }
                if (status.equals(RETURN_RECEIVED)) {
                    return updateExchangePacketStatusToReturnReceived(packet);
                }
            }
            return updatePacketStatusAndSaveToHistory(packet, status);
        }

        public Packet updateExchangePacketStatusToReturnReceived(Packet packet) {
            LOGGER.debug("Updating exchange packet status to RETURN_RECEIVED. Packet ID: {}", packet.getId());

            Long id = packet.getExchangeId();
            Packet exchangePacket = getPacketById(id);

            if (DELIVERED.equals(SystemStatus.valueOf(packet.getStatus()))
                    || PAID.equals(SystemStatus.valueOf(packet.getStatus()))
                    || RETURN.equals(SystemStatus.valueOf(exchangePacket.getStatus()))
                    || RETURN_RECEIVED.equals(SystemStatus.valueOf(exchangePacket.getStatus()))) {
                return updatePacketStatusAndSaveToHistory(exchangePacket, RETURN_RECEIVED);
            } else {
                return updatePacketStatusAndSaveToHistory(packet, RETURN_RECEIVED);
            }
        }

        public Packet updateExchangePacketStatusToPaid(Packet packet, SystemStatus status) {
            LOGGER.debug("Updating exchange packet status to PAID. Packet ID: {}", packet.getId());

            Long id = packet.getExchangeId();
            Packet exchangePacket = getPacketById(id);
            packet.setDiscount(exchangePacket.getPrice() - packet.getDiscount() + exchangePacket.getDiscount());

            if (!RETURN_RECEIVED.equals(SystemStatus.valueOf(exchangePacket.getStatus()))) {
                updatePacketStatusAndSaveToHistory(exchangePacket, RETURN);
            }
            return updatePacketStatusAndSaveToHistory(packet, status);
        }

        public Packet updatePacketStatusAndSaveToHistory(Packet packet, SystemStatus status) {
            LOGGER.debug("Updating packet status and saving to history. Packet ID: {}, Status: {}", packet.getId(), status);

            if (!SystemStatus.valueOf(packet.getStatus()).equals(status) &&
                    !(RETURN.equals(SystemStatus.valueOf(packet.getStatus())) &&
                            !(status.equals(RETURN_RECEIVED) || status.equals(PROBLEM)))) {

                // Uncomment this line if you want to update product status
                // updateProducts_Status(packet, status);
                updateProducts_Quantity(packet, status);

                User currentUser = sessionUtils.getCurrentUser();
                packet.getPacketStatus().add(entityBuilderHelper.createPacketStatusBuilder(status.toString(), packet, currentUser).build());

                return savePacketStatus(packet, status);
            }
            return packet;
        }

        public void updateProducts_Quantity(Packet packet, SystemStatus status) {
            if (status.equals(RETURN_RECEIVED)
                    || status.equals(CONFIRMED)
                    || status.equals(CANCELED)) {
                updateProductsQuantity(packet, status);
            }
        }

        public void updateProductsQuantity(Packet packet, SystemStatus status) {
            LOGGER.debug("Updating products quantity for packet ID: {}, Status: {}", packet.getId(), status);

            int quantity = 0;
            if (status.equals(RETURN_RECEIVED) || status.equals(CANCELED)) quantity = 1;
            if (status.equals(CONFIRMED)) quantity = -1;

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
            LOGGER.debug("Updating product quantity. Product ID: {}, Quantity Change: {}", product.getId(), quantityChange);

            product.setQuantity(product.getQuantity() + quantityChange);
            product.setDate(new Date());
            productRepository.save(product);
        }

        public Packet savePacketStatus(Packet packet, SystemStatus status) {
            LOGGER.debug("Saving packet status. Packet ID: {}, Status: {}", packet.getId(), status);

            packet.setStatus(String.valueOf(status));
            packet.setLastUpdateDate(new Date());

            // Uncomment this line if you want to save packet status directly
            // packetRepository.savePacketStatus(packet.getId(), status, packet.getLastDeliveryStatus());

            return packetRepository.save(packet);
        }

        @Override
        public PacketDTO addProductsToPacket(SelectedProductsDTO selectedProductsDTO) {
            String packetStatus = selectedProductsDTO.getStatus();
            Packet packet = getPacketById(selectedProductsDTO.getIdPacket());

            if (OOS.equals(SystemStatus.valueOf(packetStatus)) &&
                    (NOT_CONFIRMED.equals(SystemStatus.valueOf(packet.getStatus())) ||
                            NOT_SERIOUS.equals(SystemStatus.valueOf(packet.getStatus())) ||
                            CANCELED.equals(SystemStatus.valueOf(packet.getStatus())) ||
                            UNREACHABLE.equals(SystemStatus.valueOf(packet.getStatus())))) {
                packet.setStatus(String.valueOf(OOS));
                LOGGER.debug("Packet status updated to OOS for packet ID: {}", packet.getId());
            }

            List<ProductsPacket> productsPackets = selectedProductsDTO.getProductsOffers().stream()
                    .map(productOffer -> addProductPacket(packet, productOffer))
                    .toList();
            addProductsToPacket(packet, productsPackets);

            packet.setPacketDescription(selectedProductsDTO.getPacketDescription());
            packet.setPrice(selectedProductsDTO.getTotalPrice());
            packet.setDeliveryPrice(selectedProductsDTO.getDeliveryPrice());
            packet.setDiscount(selectedProductsDTO.getDiscount());
            packet.setChangedPrice(false);
            Packet savedPacket = packetRepository.save(packet);
            //LOGGER.info("Products added to packet ID: {}. New packet details: {}", packet.getId(), savedPacket);

            return packetMapper.toDto(savedPacket);
        }

        @Transactional
        public void addProductsToPacket(Packet packet, List<ProductsPacket> productsPackets) {
            LOGGER.debug("Adding products to packet ID: {}", packet.getId());

            for (ProductsPacket productPacket : productsPackets) {
                productPacket.setPacket(packet); // Set the reference to the packet
            }
            packet.getProductsPackets().clear(); // Clear existing productsPackets if necessary
            packet.getProductsPackets().addAll(productsPackets);
            packetRepository.save(packet); // Save the packet to persist changes

            LOGGER.info("Products packets updated for packet ID: {}", packet.getId());
        }

        private ProductsPacket addProductPacket(Packet packet, ProductOfferDTO productsPacket) {
            LOGGER.debug("Creating ProductsPacket for Product ID: {} and Offer ID: {}", productsPacket.getProductId(), productsPacket.getOfferId());
            return entityBuilderHelper.createProductsPacketBuilder(
                     Product.builder().id(productsPacket.getProductId()).build(),
                    packet,
                     Offer.builder().id(productsPacket.getOfferId()).build(),
                    productsPacket.getPacketOfferIndex(),
                    productsPacket.getProfits())
            .build();
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
                                .orElseThrow(() -> {
                                    LOGGER.error("Packet with ID {} does not exist", packetId);
                                    return new PacketNotFoundException(packetId, "does not exist.");
                                });

                        if (packet.getCustomerPhoneNb() == null || packet.getCustomerPhoneNb().isEmpty()) {
                            packetRepository.deleteById(packetId);
                            LOGGER.info("Packet ID {} deleted successfully", packetId);
                        } else {
                            note.setUser(currentUser);
                            note.setPacket(packet);
                            packet.getNotes().add(note);
                            LOGGER.debug("Adding note to packet ID {}. Note status: {}", packetId, note.getStatus());

                            packet.setStatus(String.valueOf(DELETED));
                            packet.getPacketStatus().add(entityBuilderHelper.createPacketStatusBuilder(DELETED.getStatus(), packet, currentUser).build());
                            packets.add(packet);
                        }
                    } catch (PacketNotFoundException e) {
                        LOGGER.error("Error processing packet with ID {}: {}", packetId, e.getMessage());
                        throw e;
                    } catch (Exception e) {
                        LOGGER.error("An unexpected error occurred while processing packet with ID {}: {}", packetId, e.getMessage());
                        throw e;
                    }
                }

                // Save all packets that were processed
                List<PacketDTO> result = packetRepository.saveAll(packets).stream().map(packetMapper::toDto).collect(Collectors.toList());
                LOGGER.info("Selected packets deleted successfully. Number of packets processed: {}", packets.size());
                return result;

            } catch (Exception e) {
                LOGGER.error("Failed to delete selected packets: {}", e.getMessage());
                throw e;
            }
        }
}