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

import com.clothing.management.servicesImpl.PacketServiceImpl;
import jakarta.websocket.server.PathParam;
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

    public PacketController(PacketService packetService,UpdateStatusScheduler updateStatusScheduler, MasterTenantService masterTenantService){
        this.packetService = packetService;
        this.updateStatusScheduler = updateStatusScheduler;
        this.masterTenantService =masterTenantService;
    }

    @GetMapping
    public List<Packet> findAllPackets() {
        return packetService.findAllPackets();
    }

    @GetMapping("/paginated")
    public ResponseEntity<ResponsePage> findAllPaginatedPackets(@RequestParam(defaultValue = "0") int page,
                                                                @RequestParam(defaultValue = "100") int size,
                                                                @RequestParam(required = false) String searchText,
                                                                @RequestParam(required = false) String startDate,
                                                                @RequestParam(required = false) String endDate,
                                                                @RequestParam(required = false) String status,
                                                                @RequestParam(required = false) boolean mandatoryDate) {
        try {
            Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
            Page<Packet> allPackets = packetService.findAllPackets(pageable, searchText, startDate, endDate, status, mandatoryDate);

            // Map each Packet entity to PacketDTO
            List<PacketDTO> packetDTOList = allPackets.getContent().stream()
                    .map(PacketDTO::new) // Assuming PacketDTO has a constructor that takes a Packet entity
                    .collect(Collectors.toList());

            // Create a new Page object for PacketDTO
            Page<PacketDTO> packetDTOPage = new PageImpl<>(packetDTOList, pageable, allPackets.getTotalElements());

            return new ResponseEntity<>(new ResponsePage.Builder()
                    .result(packetDTOPage.getContent())
                    .currentPage(packetDTOPage.getNumber())
                    .totalItems(packetDTOPage.getTotalElements())
                    .totalPages(packetDTOPage.getTotalPages())
                    .build(), HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(new ResponsePage.Builder().build(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping(path = "/findValidationPackets")
    public ResponseEntity<ResponsePage> findValidationPackets() {
        try {
            return new ResponseEntity<>(new ResponsePage.Builder()
                    .result(packetService.findValidationPackets())
                    .build(), HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(new ResponsePage.Builder().build(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }



    @GetMapping("/by-date-range")
    public List<PacketDTO> findAllPacketsByDateRange(@RequestParam(required = false) String startDate,
                                                  @RequestParam(required = false) String endDate) throws ParseException {
        return packetService.findAllPacketsByDate(startDate, endDate);
    }

    @GetMapping("/by-date/{date}")
    public List<Packet> findAllPacketsByDate(@PathVariable Date date) {
        return packetService.findAllPacketsByDate(date);
    }

    /*@GetMapping("/{id}")
    public Optional<Packet> findPacketById(@PathVariable Long id) {
        return packetService.findPacketById(id);
    }*/

    @GetMapping("/{id}/related-products")
    public List<ProductsPacketDTO> findPacketRelatedProducts(@PathVariable Long id) throws Exception {
        return packetService.findPacketRelatedProducts(id);
    }
    /*@GetMapping(path = "/findPacketRelatedProducts/{idPacket}")
    public List<ProductsPacketDTO> findPacketRelatedProducts(@PathVariable Long idPacket) throws Exception {
        return packetService.findPacketRelatedProducts(idPacket);
    }*/

    @PostMapping
    public PacketDTO addPacket() throws Exception {
        return new PacketDTO(packetService.addPacket());
    }

    @PutMapping
    public PacketDTO updatePacket(@RequestBody Packet packet) {return new PacketDTO(packetService.updatePacket(packet));}

    @PutMapping("/{id}")
    public PacketDTO patchPacket(@PathVariable Long id, @RequestBody Map<String, Object> fields) throws Exception {
        return new PacketDTO(packetService.patchPacket(id, fields));
    }

    @PostMapping("/validate/{barcode}")
    public ResponseEntity<PacketValidationDTO> updatePacketValid(@PathVariable String barCode, @RequestBody String type) throws Exception {
        PacketValidationDTO packet = null;
        try {
            packet = packetService.updatePacketValid(barCode,type);
            return new ResponseEntity<>(packet, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(packet, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/{id}")
    public void deletePacketById(@PathVariable Long id) throws Exception {
        packetService.deletePacketById(id);
    }

    @DeleteMapping("/batch-delete")
    public void deleteSelectedPackets(@RequestBody List<Long> packetIds) throws Exception {
        packetService.deleteSelectedPackets(packetIds);
    }

    @GetMapping("/{id}/timeline")
    public List<PacketStatusDTO> findPacketTimeline(@PathVariable Long id) throws Exception {
        return packetService.findPacketTimeLineById(id);
    }

    @PostMapping("/status")
    public PacketDTO getLastStatus(@RequestBody long packetId) throws Exception {
        return packetService.getLastStatus(packetId);
    }

    @PostMapping("/{id}/attempt")
    public ResponseEntity<PacketDTO> addAttempt(@RequestBody Note note, @PathVariable Long id) throws Exception {
        return new ResponseEntity<>(new PacketDTO(packetService.addAttempt(note, id)), HttpStatus.OK);
    }

    @PostMapping("/add-products")
    public PacketDTO addProductsToPacket(@RequestBody SelectedProductsDTO selectedProductsDTO) throws Exception {
        return packetService.addProductsToPacket(selectedProductsDTO);
    }

    /*@GetMapping("/{id}/check-validity")
    public List<Packet> checkPacketProductsValidity(@PathVariable Long id) throws Exception {
        return packetService.checkPacketProductsValidity(id);
    }*/

    @GetMapping("/notifications/sync")
    public List<DashboardCard> syncNotifications(@RequestParam(required = false) String startDate,
                                                 @RequestParam(required = false) String endDate) {
        return packetService.syncNotification(startDate, endDate);
    }

    @GetMapping("/duplicate/{id}")
    public PacketDTO duplicatePacket(@PathVariable Long id) throws Exception {
        return packetService.duplicatePacket(id);
    }

    @GetMapping("/status/sync")
    public int synchronizeAllPacketsStatus(@RequestParam("tenantName") String tenantName) {
        MasterTenant masterTenant = masterTenantService.findByTenantName(tenantName);
        return updateStatusScheduler.startUpdateStatusCronTask(masterTenant);
    }

    @PostMapping("/barcode/status")
    public ResponseEntity<List<String>> updatePacketsByBarCode(@RequestBody BarCodeStatusDTO barCodeStatusDTO) {
        try {
            return new ResponseEntity<>(packetService.updatePacketsByBarCodes(barCodeStatusDTO), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
