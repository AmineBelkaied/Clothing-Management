package com.clothing.management.controllers;

import com.clothing.management.auth.mastertenant.entity.MasterTenant;
import com.clothing.management.auth.mastertenant.service.MasterTenantService;
import com.clothing.management.dto.*;
import com.clothing.management.dto.DeliveryCompanyDTOs.BarCodeStatusDTO;
import com.clothing.management.entities.Note;
import com.clothing.management.entities.Packet;
import com.clothing.management.models.DashboardCard;
import com.clothing.management.models.ResponsePage;
import com.clothing.management.scheduler.UpdateStatusScheduler;
import com.clothing.management.services.PacketService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("${api.prefix}/packets")
@CrossOrigin
@Secured({"ROLE_ADMIN", "ROLE_USER"})
public class PacketController {

    private final PacketService packetService;
    private final UpdateStatusScheduler updateStatusScheduler;
    private final MasterTenantService masterTenantService;
    private static final Logger LOGGER = LoggerFactory.getLogger(PacketController.class);

    public PacketController(PacketService packetService, UpdateStatusScheduler updateStatusScheduler, MasterTenantService masterTenantService) {
        this.packetService = packetService;
        this.updateStatusScheduler = updateStatusScheduler;
        this.masterTenantService = masterTenantService;
    }

    @GetMapping
    public List<Packet> findAllPackets() {
        LOGGER.info("Retrieving all packets, sorting by ID in descending order.");
        return packetService.findAllPackets();
    }

    @GetMapping("/paginated")
    public ResponseEntity<ResponsePage> findAllPaginatedPackets(@RequestParam(defaultValue = "0") int page,
                                                                @RequestParam(defaultValue = "100") int size,
                                                                @RequestParam(required = false) String searchText,
                                                                @RequestParam(required = false) String beginDate,
                                                                @RequestParam(required = false) String endDate,
                                                                @RequestParam(required = false) String status,
                                                                @RequestParam(required = false) boolean mandatoryDate) throws ParseException {

        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        LOGGER.info("Finding packets with parameters - Pageable: {}, SearchText: {}, BeginDate: {}, EndDate: {}, Status: {}, MandatoryDate: {}",
                pageable, searchText, beginDate, endDate, status, mandatoryDate);
        Page<PacketDTO> allPackets = packetService.findAllPackets(pageable, searchText, beginDate, endDate, status, mandatoryDate);
        return new ResponseEntity<>(new ResponsePage.Builder()
                .result(allPackets.getContent())
                .currentPage(allPackets.getNumber())
                .totalItems(allPackets.getTotalElements())
                .totalPages(allPackets.getTotalPages())
                .build(), HttpStatus.OK);
    }

    @GetMapping(path = "/validation-packets")
    public ResponseEntity<ResponsePage> findValidationPackets() {
        LOGGER.info("Fetching validation packets.");
        return new ResponseEntity<>(new ResponsePage.Builder()
                .result(packetService.findValidationPackets())
                .build(), HttpStatus.OK);
    }

    @GetMapping("/by-date-range")
    public List<PacketDTO> findAllPacketsByDateRange(@RequestParam(required = false) String beginDate,
                                                     @RequestParam(required = false) String endDate) throws ParseException {
        LOGGER.info("Fetching packets by date range: BeginDate: {}, EndDate: {}", beginDate, endDate);
        return packetService.findAllPacketsByDate(beginDate, endDate);
    }

    @GetMapping("/by-date/{date}")
    public List<Packet> findAllPacketsByDate(@PathVariable Date date) {
        LOGGER.info("Fetching packets by date: {}", date);
        return packetService.findAllPacketsByDate(date);
    }

    @GetMapping("/{id}/related-products")
    public List<ProductsPacketDTO> findPacketRelatedProducts(@PathVariable Long id) throws Exception {
        LOGGER.info("Fetching related products for packet ID: {}", id);
        return packetService.findPacketRelatedProducts(id);
    }

    @PostMapping
    public PacketDTO addPacket() throws Exception {
        LOGGER.info("Adding new packet.");
        return new PacketDTO(packetService.addPacket());
    }

    @PutMapping
    public PacketDTO updatePacket(@RequestBody Packet packet) {
        LOGGER.info("Updating packet with ID: {}", packet.getId());
        return new PacketDTO(packetService.updatePacket(packet));
    }

    @PutMapping("/{id}")
    public PacketDTO patchPacket(@PathVariable Long id, @RequestBody Map<String, Object> field) throws Exception {
        LOGGER.info("Patching packet with ID: {}. Fields: {}", id, field);
        return new PacketDTO(packetService.patchPacket(id, field));
    }

    @PostMapping("/validate/{barcode}")
    public ResponseEntity<PacketValidationDTO> updatePacketValid(@PathVariable String barcode, @RequestBody String type) throws Exception {
        LOGGER.info("Validating packet with barcode: {} and type: {}", barcode, type);
        PacketValidationDTO packet = packetService.updatePacketValid(barcode, type);
        return new ResponseEntity<>(packet, HttpStatus.OK);
    }

    @PostMapping("/batch-delete")
    public List<PacketDTO> deleteSelectedPackets(@RequestParam List<Long> packetsId, @RequestBody Note note) throws Exception {
        LOGGER.info("Deleting selected packets. Packet IDs: {}", packetsId);
        return packetService.deleteSelectedPackets(packetsId, note);
    }

    @GetMapping("/{id}/timeline")
    public List<PacketStatusDTO> findPacketTimeline(@PathVariable Long id) throws Exception {
        LOGGER.info("Fetching timeline for packet ID: {}", id);
        return packetService.findPacketTimeLineById(id);
    }

    @PostMapping("/status")
    public PacketDTO getLastStatus(@RequestBody long packetId) throws Exception {
        LOGGER.info("Fetching last status for packet ID: {}", packetId);
        return packetService.getLastStatus(packetId);
    }

    @PostMapping("/{id}/attempt")
    public ResponseEntity<PacketDTO> addAttempt(@RequestBody Note note, @PathVariable Long id) {
        LOGGER.info("Adding attempt for packet ID: {}", id);
        return new ResponseEntity<>(new PacketDTO(packetService.addAttempt(note, id)), HttpStatus.OK);
    }

    @PostMapping("/add-products")
    public PacketDTO addProductsToPacket(@RequestBody SelectedProductsDTO selectedProductsDTO) throws Exception {
        LOGGER.info("Adding products to packet ID: {}", selectedProductsDTO.getIdPacket());
        return packetService.addProductsToPacket(selectedProductsDTO);
    }

    @GetMapping("/notifications/sync")
    public List<DashboardCard> syncNotifications(@RequestParam(required = false) String beginDate,
                                                 @RequestParam(required = false) String endDate) {
        LOGGER.info("Syncing notifications. BeginDate: {}, EndDate: {}", beginDate, endDate);
        return packetService.syncNotification(beginDate, endDate);
    }

    @GetMapping("/duplicate/{id}")
    public PacketDTO duplicatePacket(@PathVariable Long id) throws Exception {
        LOGGER.info("Duplicating packet with ID: {}", id);
        return packetService.duplicatePacket(id);
    }

    @GetMapping("/status/sync")
    public int synchronizeAllPacketsStatus(@RequestParam("tenantName") String tenantName) {
        LOGGER.info("Synchronizing all packet statuses for tenant: {}", tenantName);
        MasterTenant masterTenant = masterTenantService.findByTenantName(tenantName);
        return updateStatusScheduler.startUpdateStatusCronTask(masterTenant);
    }

    @PostMapping("/barcode/status")
    public ResponseEntity<List<String>> updatePacketsByBarCode(@RequestBody BarCodeStatusDTO barCodeStatusDTO) {
        LOGGER.info("Updating packets by barcode.");
        return new ResponseEntity<>(packetService.updatePacketsByBarCodes(barCodeStatusDTO), HttpStatus.OK);
    }
}
