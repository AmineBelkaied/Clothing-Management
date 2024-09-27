package com.clothing.management.controllers;

import com.clothing.management.auth.mastertenant.entity.MasterTenant;
import com.clothing.management.auth.mastertenant.service.MasterTenantService;
import com.clothing.management.dto.*;
import com.clothing.management.dto.DeliveryCompanyDTOs.BarCodeStatusDTO;
import com.clothing.management.entities.Note;
import com.clothing.management.entities.Packet;
import com.clothing.management.mappers.PacketMapper;
import com.clothing.management.models.DashboardCard;
import com.clothing.management.models.ResponsePage;
import com.clothing.management.scheduler.UpdateStatusScheduler;
import com.clothing.management.services.PacketService;
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
    private final PacketMapper packetMapper;

    public PacketController(PacketService packetService, UpdateStatusScheduler updateStatusScheduler, MasterTenantService masterTenantService, PacketMapper packetMapper) {
        this.packetService = packetService;
        this.updateStatusScheduler = updateStatusScheduler;
        this.masterTenantService = masterTenantService;
        this.packetMapper = packetMapper;
    }

    @GetMapping
    public List<Packet> findAllPackets() {
        return packetService.findAllPackets();
    }

    @GetMapping("/paginated")
    public ResponseEntity<ResponsePage> findAllPaginatedPackets(@RequestParam(defaultValue = "0") int page,
                                                                @RequestParam(defaultValue = "100") int size,
                                                                @RequestParam(required = false) String searchText,
                                                                @RequestParam(required = false) String beginDate,
                                                                @RequestParam(required = false) String endDate,
                                                                @RequestParam(required = false) String status,
                                                                @RequestParam(required = false) boolean mandatoryDate) {
        try {
            Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
            Page<PacketDTO> allPackets = packetService.findAllPackets(pageable, searchText, beginDate, endDate, status, mandatoryDate);
            return new ResponseEntity<>(new ResponsePage.Builder()
                    .result(allPackets.getContent())
                    .currentPage(allPackets.getNumber())
                    .totalItems(allPackets.getTotalElements())
                    .totalPages(allPackets.getTotalPages())
                    .build(), HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(new ResponsePage.Builder().build(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping(path = "/validation-packets")
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
    public List<PacketDTO> findAllPacketsByDateRange(@RequestParam(required = false) String beginDate,
                                                  @RequestParam(required = false) String endDate) throws ParseException {
        return packetService.findAllPacketsByDate(beginDate, endDate);
    }

    @GetMapping("/by-date/{date}")
    public List<Packet> findAllPacketsByDate(@PathVariable Date date) {
        return packetService.findAllPacketsByDate(date);
    }

    @GetMapping("/{id}/related-products")
    public List<ProductsPacketDTO> findPacketRelatedProducts(@PathVariable Long id) throws Exception {
        return packetService.findPacketRelatedProducts(id);
    }

    @PostMapping
    public PacketDTO addPacket() throws Exception {
        return packetMapper.toDto(packetService.addPacket());
    }

    @PutMapping
    public PacketDTO updatePacket(@RequestBody Packet packet) {return packetMapper.toDto(packetService.updatePacket(packet));}

    @PutMapping("/{id}")
    public PacketDTO patchPacket(@PathVariable Long id, @RequestBody Map<String, Object> fields) throws Exception {
        return packetMapper.toDto(packetService.patchPacket(id, fields));
    }

    @PostMapping("/validate/{barcode}")
    public ResponseEntity<PacketValidationDTO> updatePacketValid(@PathVariable String barcode, @RequestBody String type){
        PacketValidationDTO packet = null;
        try {
            packet = packetService.updatePacketValid(barcode,type);
            return new ResponseEntity<>(packet, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(packet, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/batch-delete")
    public List<PacketDTO> deleteSelectedPackets(@RequestParam List<Long> packetsId, @RequestBody Note note) throws Exception {
        return packetService.deleteSelectedPackets(packetsId, note);
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
    public ResponseEntity<PacketDTO> addAttempt(@RequestBody Note note, @PathVariable Long id) {
        return new ResponseEntity<>(packetMapper.toDto(packetService.addAttempt(note, id)), HttpStatus.OK);
    }

    @PostMapping("/add-products")
    public PacketDTO addProductsToPacket(@RequestBody SelectedProductsDTO selectedProductsDTO) throws Exception {
        return packetService.addProductsToPacket(selectedProductsDTO);
    }

    @GetMapping("/notifications/sync")
    public List<DashboardCard> syncNotifications(@RequestParam(required = false) String beginDate,
                                                 @RequestParam(required = false) String endDate) {
        return packetService.syncNotification(beginDate, endDate);
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
