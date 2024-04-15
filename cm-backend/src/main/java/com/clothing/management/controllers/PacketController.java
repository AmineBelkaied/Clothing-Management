package com.clothing.management.controllers;

import com.clothing.management.dto.*;
import com.clothing.management.entities.Packet;
import com.clothing.management.entities.PacketStatus;
import com.clothing.management.models.DashboardCard;
import com.clothing.management.models.ResponsePage;
import com.clothing.management.scheduler.UpdateStatusScheduler;
import com.clothing.management.services.PacketService;

import com.clothing.management.services.StatService;
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
@RequestMapping("packet")
@CrossOrigin
@Secured({"ROLE_ADMIN", "ROLE_USER"})
public class PacketController {

    @Autowired
    PacketService packetService;

    @Autowired
    UpdateStatusScheduler updateStatusScheduler;

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

    @GetMapping(path = "/findAllPacketsByDate")
    public List<Packet> findAllPacketsByDate(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) throws ParseException {
        return packetService.findAllPacketsByDate(startDate, endDate);
    }

    /*@GetMapping(path = "/findAllTodaysPackets")
    public ResponseEntity<ResponsePage> findAllTodaysPackets(@RequestParam(defaultValue = "0") int page,
                                             @RequestParam(defaultValue = "500") int size) {
        try {
            Pageable paging = PageRequest.of(page, size);
            Page<Packet> allTodaysPackets = packetService.findAllTodaysPackets(paging);
            return new ResponseEntity<>(new ResponsePage.Builder()
                    .result(allTodaysPackets.getContent())
                    .currentPage(allTodaysPackets.getNumber())
                    .totalItems(allTodaysPackets.getTotalElements())
                    .totalPages(allTodaysPackets.getTotalPages())
                    .build(), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(new ResponsePage.Builder().build(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }*/

    @GetMapping(path = "/findAllByDate/{date}")
    public List<Packet> findAllPacketsByDate(Date date) {
        return packetService.findAllPacketsByDate(date);
    }

    @GetMapping(path = "/findById/{id}")
    public Optional<Packet> findByIdPacket(@PathVariable Long idPacket) {
        return packetService.findPacketById(idPacket);
    }

    @GetMapping(path = "/findPacketRelatedProducts/{idPacket}")
    public PacketDTO findPacketRelatedProducts(@PathVariable Long idPacket) {
        return packetService.findPacketRelatedProducts(idPacket);
    }

    @GetMapping(value = "/add")
    public Packet addPacket() {
        return packetService.addPacket();
    }

    @PutMapping(value = "/update" , produces = "application/json")
    public Packet updatePacket(@RequestBody Packet packet) {
        return packetService.updatePacket(packet);
    }

    @PutMapping(value = "/patch/{idPacket}")
    public Packet patchPacket(@PathVariable Long idPacket , @RequestBody Map<String , Object> field) throws IOException {
        return packetService.patchPacket(idPacket , field);
    }

    @PostMapping(value = "/valid/{barCode}",produces = "application/json")
    public Packet updatePacketValid(@PathVariable String barCode,@RequestBody String type) throws Exception {
        return packetService.updatePacketValid(barCode,type);
    }

    @DeleteMapping(value = "/deleteById/{idPacket}" , produces = "application/json")
    public void deletePacketById(@PathVariable Long idPacket) {
        packetService.deletePacketById(idPacket);
    }

    @DeleteMapping(value = "/deleteSelectedPackets/{packetsId}" , produces = "application/json")
    public void deleteSelectedPackets(@PathVariable List<Long> packetsId) {
        packetService.deleteSelectedPackets(packetsId);
    }

    //@PostMapping(value = "/updateStatus/{idPacket}/{status}")
    //public void updatePacketStatus(@PathVariable Long idPacket , @PathVariable String status) {
      //    packetService.savePacketStatusToHistory(idPacket , status);
    //}

    @GetMapping(path = "/getPacketTimeLine/{idPacket}")
    public List<PacketStatus> findAllPacketStatus(@PathVariable Long idPacket) throws Exception {
        return packetService.findPacketTimeLineById(idPacket);
    }

    /*@PostMapping(value = "/createBarCode", produces = "application/json")
    public ResponseEntity<DeliveryResponse>  createBarCode(@RequestBody Packet packet) throws IOException, InterruptedException {
        DeliveryResponse deliveryResponse = packetService.createBarCode(packet);
            if(deliveryResponse.getResponseCode() != 200)
                return new ResponseEntity<>(deliveryResponse, HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(deliveryResponse, HttpStatus.OK);
    }*/

    @GetMapping(value = "/getLastStatus")
    @CrossOrigin("*")
    public ResponseEntity<Packet> getLastStatus(@RequestBody Packet packet) throws Exception {
        return new ResponseEntity<>(
                packetService.getLastStatus(packet),
                HttpStatus.OK);
    }

    @PostMapping(value = "/addAttempt/{note}", produces = "application/json")
    public ResponseEntity<Packet> addAttempt(@PathVariable String note,@RequestBody Packet packet) throws ParseException {
        return new ResponseEntity<>(
                packetService.addAttempt(packet,note),
                HttpStatus.OK);
    }

    @PostMapping(value = "/addProducts" , produces = "application/json")
    public Packet addProductsToPacket(@RequestBody SelectedProductsDTO selectedProductsDTO, @RequestParam("stock") Integer stock){
        return packetService.addProductsToPacket(selectedProductsDTO,stock);
    }
    /*@PostMapping(value = "/checkPhone", produces = "application/json")
    public int checkPhone(@RequestBody String phoneNumber) throws Exception {
        return packetService.checkPhone(phoneNumber);
    }*/

    @GetMapping(value = "/checkPacketProductsValidity/{packetId}")
    public List<Packet> checkPacketProductsValidity(@PathVariable Long packetId) throws Exception {
        return packetService.checkPacketProductsValidity(packetId);
    }

    @GetMapping(path = "/createDashboard")
    public List<DashboardCard> createDashboard(){
        return packetService.createDashboard();
    }

    @GetMapping(path = "/syncNotification")
    public List<DashboardCard> syncNotification(){
        return packetService.syncNotification();
    }

    @GetMapping(path = "/duplicatePacket/{idPacket}")
    public Packet duplicatePacket(@PathVariable Long idPacket) {
        return packetService.duplicatePacket(idPacket);
    }
    @GetMapping(path = "/syncAllPacketsStatus")
    public int synchronizeAllPacketsStatus() throws Exception {
        return updateStatusScheduler.cronJobSch();
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
