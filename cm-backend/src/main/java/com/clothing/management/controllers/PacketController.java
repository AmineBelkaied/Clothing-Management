package com.clothing.management.controllers;

import com.clothing.management.auth.mastertenant.entity.MasterTenant;
import com.clothing.management.auth.mastertenant.service.MasterTenantService;
import com.clothing.management.auth.util.JwtTokenUtil;
import com.clothing.management.dto.*;
import com.clothing.management.dto.DeliveryCompanyDTOs.BarCodeStatusDTO;
import com.clothing.management.entities.Packet;
import com.clothing.management.entities.PacketStatus;
import com.clothing.management.models.DashboardCard;
import com.clothing.management.models.ResponsePage;
import com.clothing.management.scheduler.UpdateStatusScheduler;
import com.clothing.management.services.PacketService;

import jakarta.websocket.server.PathParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("packet")
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

    @GetMapping(path = "/findAll")
    public List<Packet> findAllPackets() {
        return packetService.findAllPackets();
    }

    @GetMapping(path = "/findAllPaginatedPackets")
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


    @GetMapping(path = "/findAllPacketsByDate")
    public List<PacketDTO> findAllPacketsByDate(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) throws ParseException {
        return packetService.findAllPacketsByDate(startDate, endDate);
    }

    @GetMapping(path = "/findAllByDate/{date}")
    public List<Packet> findAllPacketsByDate(Date date) {
        return packetService.findAllPacketsByDate(date);
    }

    /*@GetMapping(path = "/findById/{id}")
    public Optional<Packet> findByIdPacket(@PathVariable Long id) {
        return packetService.findPacketById(id);
    }*/

    @GetMapping(path = "/findPacketRelatedProducts/{idPacket}")
    public List<ProductsPacketDTO> findPacketRelatedProducts(@PathVariable Long idPacket) throws Exception {
        return packetService.findPacketRelatedProducts(idPacket);
    }

    @GetMapping(value = "/add")
    public PacketDTO addPacket() throws Exception {
        return new PacketDTO(packetService.addPacket());
    }

    @PutMapping(value = "/update" , produces = "application/json")
    public PacketDTO updatePacket(@RequestBody Packet packet) {
        return new PacketDTO(packetService.updatePacket(packet));
    }

    @PutMapping(value = "/patch/{idPacket}")
    public PacketDTO patchPacket(@PathVariable Long idPacket , @RequestBody Map<String , Object> field) throws Exception {
        return new PacketDTO(packetService.patchPacket(idPacket , field));
    }

    @PostMapping(value = "/valid/{barCode}",produces = "application/json")
    public ResponseEntity<PacketValidationDTO> updatePacketValid(@PathVariable String barCode,@RequestBody String type) throws Exception {
        try {
            return new ResponseEntity<>(packetService.updatePacketValid(barCode,type), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(packetService.updatePacketValid(barCode,type), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping(value = "/deleteById/{idPacket}" , produces = "application/json")
    public void deletePacketById(@PathVariable Long idPacket) throws Exception {
        packetService.deletePacketById(idPacket);
    }

    @DeleteMapping(value = "/deleteSelectedPackets/{packetsId}" , produces = "application/json")
    public void deleteSelectedPackets(@PathVariable List<Long> packetsId) throws Exception {
        packetService.deleteSelectedPackets(packetsId);
    }

    @GetMapping(path = "/getPacketTimeLine/{idPacket}")
    public List<PacketStatus> findAllPacketStatus(@PathVariable Long idPacket) throws Exception {
        return packetService.findPacketTimeLineById(idPacket);
    }

    @PostMapping(value = "/getLastStatus")
    @CrossOrigin("*")
    public PacketDTO getLastStatus(@RequestBody long packetId) throws Exception {
        return packetService.getLastStatus(packetId);
    }

    @PostMapping(value = "/addAttempt/{packetId}", produces = "application/json")
    public ResponseEntity<PacketDTO> addAttempt(@PathVariable Long packetId,@RequestBody String note){
        return new ResponseEntity<>(
                new PacketDTO(packetService.addAttempt(packetId,note)),
                HttpStatus.OK);
    }

    @PostMapping(value = "/addProducts" , produces = "application/json")
    public PacketDTO addProductsToPacket(@RequestBody SelectedProductsDTO selectedProductsDTO) throws Exception {
        return packetService.addProductsToPacket(selectedProductsDTO);
    }

    /*@GetMapping(value = "/checkPacketProductsValidity/{packetId}")
    public List<Packet> checkPacketProductsValidity(@PathVariable Long packetId) throws Exception {
        return packetService.checkPacketProductsValidity(packetId);
    }*/

    @GetMapping(path = "/syncNotification")
    public List<DashboardCard> syncNotification(@RequestParam(required = false) String startDate,
                                                @RequestParam(required = false) String endDate){
        return packetService.syncNotification(startDate, endDate);
    }

    @GetMapping(path = "/duplicatePacket/{idPacket}")
    public PacketDTO duplicatePacket(@PathVariable Long idPacket) throws Exception {
        return packetService.duplicatePacket(idPacket);
    }

    @GetMapping(path = "/syncAllPacketsStatus")
    public int synchronizeAllPacketsStatus(@PathParam("tenantName") String tenantName) {
        MasterTenant masterTenant = masterTenantService.findByTenantName(tenantName);
        System.out.println("masterTenant >>  " + masterTenant.getTenantName());
        return updateStatusScheduler.startUpdateStatusCronTask(masterTenant);
    }

    @PostMapping(value = "/updatePacketsByBarCode", produces = "application/json")
    public ResponseEntity<List<String>> updatePacketsByBarCode(@RequestBody BarCodeStatusDTO barCodeStatusDTO) {
        try {
            return new ResponseEntity<>(packetService.updatePacketsByBarCodes(barCodeStatusDTO), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(packetService.updatePacketsByBarCodes(barCodeStatusDTO), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


}
