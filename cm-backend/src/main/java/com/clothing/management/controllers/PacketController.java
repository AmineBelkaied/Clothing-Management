package com.clothing.management.controllers;

import com.clothing.management.auth.mastertenant.entity.MasterTenant;
import com.clothing.management.auth.mastertenant.service.MasterTenantService;
import com.clothing.management.auth.util.JwtTokenUtil;
import com.clothing.management.dto.*;
import com.clothing.management.entities.Note;
import com.clothing.management.entities.Packet;
import com.clothing.management.entities.PacketStatus;
import com.clothing.management.models.DashboardCard;
import com.clothing.management.models.ResponsePage;
import com.clothing.management.scheduler.UpdateStatusScheduler;
import com.clothing.management.services.PacketService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.text.ParseException;
import java.util.*;

@RestController
@RequestMapping("${api.prefix}/packets")
@CrossOrigin
@Secured({"ROLE_ADMIN", "ROLE_USER"})
public class PacketController {

    @Autowired
    PacketService packetService;

    @Autowired
    UpdateStatusScheduler updateStatusScheduler;

    @Autowired
    MasterTenantService masterTenantService;

    @Autowired
    JwtTokenUtil jwtTokenUtil;

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

    @GetMapping("/by-date-range")
    public List<Packet> findAllPacketsByDateRange(@RequestParam(required = false) String startDate,
                                                  @RequestParam(required = false) String endDate) throws ParseException {
        return packetService.findAllPacketsByDate(startDate, endDate);
    }

    @GetMapping("/by-date/{date}")
    public List<Packet> findAllPacketsByDate(@PathVariable Date date) {
        return packetService.findAllPacketsByDate(date);
    }

    @GetMapping("/{id}")
    public Optional<Packet> findPacketById(@PathVariable Long id) {
        return packetService.findPacketById(id);
    }

    @GetMapping("/{id}/related-products")
    public PacketDTO findPacketRelatedProducts(@PathVariable Long id) {
        return packetService.findPacketRelatedProducts(id);
    }

    @PostMapping
    public Packet addPacket() {
        return packetService.addPacket();
    }

    @PutMapping
    public Packet updatePacket(@RequestBody Packet packet) {return packetService.updatePacket(packet);}

    @PutMapping("/{id}")
    public Packet patchPacket(@PathVariable Long id, @RequestBody Map<String, Object> fields) throws IOException {
        return packetService.patchPacket(id, fields);
    }

    @PostMapping("/validate/{barcode}")
    public Packet updatePacketValid(@PathVariable String barcode, @RequestBody String type) throws Exception {
        return packetService.updatePacketValid(barcode, type);
    }

    @DeleteMapping("/{id}")
    public void deletePacketById(@PathVariable Long id) {
        packetService.deletePacketById(id);
    }

    @DeleteMapping("/batch-delete")
    public void deleteSelectedPackets(@RequestBody List<Long> packetIds) {
        packetService.deleteSelectedPackets(packetIds);
    }

    @GetMapping("/{id}/timeline")
    public List<PacketStatus> findPacketTimeline(@PathVariable Long id) throws Exception {
        return packetService.findPacketTimeLineById(id);
    }

    @PostMapping("/status")
    public ResponseEntity<Packet> getLastStatus(@RequestBody Packet packet) throws Exception {
        return new ResponseEntity<>(packetService.getLastStatus(packet), HttpStatus.OK);
    }

    @PostMapping("/{id}/attempt")
    public ResponseEntity<Packet> addAttempt(@RequestBody Note note, @PathVariable Long id) throws Exception {
        return new ResponseEntity<>(packetService.addAttempt(note, id), HttpStatus.OK);
    }

    @PostMapping("/add-products")
    public Packet addProductsToPacket(@RequestBody SelectedProductsDTO selectedProductsDTO, @RequestParam("stock") Integer stock) {
        return packetService.addProductsToPacket(selectedProductsDTO, stock);
    }

    @GetMapping("/{id}/check-validity")
    public List<Packet> checkPacketProductsValidity(@PathVariable Long id) throws Exception {
        return packetService.checkPacketProductsValidity(id);
    }

    @GetMapping("/notifications/sync")
    public List<DashboardCard> syncNotifications(@RequestParam(required = false) String startDate,
                                                 @RequestParam(required = false) String endDate) {
        return packetService.syncNotification(startDate, endDate);
    }

    @GetMapping("/duplicate/{id}")
    public Packet duplicatePacket(@PathVariable Long id) {
        return packetService.duplicatePacket(id);
    }

    @GetMapping("/status/sync")
    public int synchronizeAllPacketsStatus(@RequestParam("tenantName") String tenantName) {
        MasterTenant masterTenant = masterTenantService.findByTenantName(tenantName);
        return updateStatusScheduler.startUpdateStatusCronTask(masterTenant);
    }

    @GetMapping("/rupture/sync")
    public void synchronizeRuptureStatus() {
        updateStatusScheduler.updatePacketStockForRuptureStatus();
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
