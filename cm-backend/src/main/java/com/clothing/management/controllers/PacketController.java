package com.clothing.management.controllers;

import com.clothing.management.dto.*;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("packet")
@CrossOrigin
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
            Pageable pageable = PageRequest.of(page, size);
            Page<Packet> allPackets = packetService.findAllPackets(searchText, startDate, endDate, status, pageable, mandatoryDate);
            return new ResponseEntity<>(new ResponsePage.Builder()
                    .result(allPackets.getContent())
                    .currentPage(allPackets.getNumber())
                    .totalItems(allPackets.getTotalElements())
                    .totalPages(allPackets.getTotalPages())
                    .build(), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(new ResponsePage.Builder().build(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping(path = "/findAllPacketsByDate")
    public List<Packet> findAllPacketsByDate(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
            return packetService.findAllPacketsByDate(startDate, endDate);
    }

    @GetMapping(path = "/findAllTodaysPackets")
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
    }

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

    @PostMapping(value = "/add" , produces = "application/json")
    public Packet addPacket(@RequestBody  Packet packet) {
        return packetService.addPacket(packet);
    }

    @PutMapping(value = "/update" , produces = "application/json")
    public Packet updatePacket(@RequestBody Packet packet) {

        return packetService.updatePacket(packet);
    }

    @PatchMapping(value = "/patch/{idPacket}")
    public Packet patchPacket(@PathVariable Long idPacket , @RequestBody Map<String , Object> field) throws IOException {
        return packetService.patchPacket(idPacket , field);
    }

    @PostMapping(value = "/valid/{barCode}",produces = "application/json")
    public Packet updatePacketValid(@PathVariable String barCode,@RequestBody String type) throws Exception {
        return packetService.updatePacketValid(barCode,type);
    }

    @PostMapping(value = "/addProducts" , produces = "application/json")
    public void addProductsToPacket(@RequestBody SelectedProductsDTO selectedProductsDTO){
        packetService.addProductsToPacket(selectedProductsDTO);
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
    public List<PacketStatus> findAllPacketStatus(@PathVariable Long idPacket) {
        return packetService.findPacketTimeLineById(idPacket);
    }

    @PostMapping(value = "/createBarCode", produces = "application/json")
    public ResponseEntity<DeliveryResponseFirst>  createBarCode(@RequestBody Packet packet, @RequestParam("deliveryCompany") String deliveryCompany) throws IOException, InterruptedException {
        DeliveryResponseFirst deliveryResponse = packetService.createBarCode(packet, deliveryCompany);
            if(deliveryResponse.getResponseCode() != 200)
                return new ResponseEntity<>(deliveryResponse, HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(deliveryResponse, HttpStatus.OK);
    }

    @PostMapping(value = "/getLastStatus", produces = "application/json")
    @CrossOrigin("*")
    public ResponseEntity<Packet> getLastStatus(@RequestBody Packet packet, @RequestParam("deliveryCompany") String deliveryCompany) throws Exception {
        return new ResponseEntity<>(
                packetService.getLastStatus(packet, deliveryCompany),
                HttpStatus.OK);
    }
    @PostMapping(value = "/checkPhone", produces = "application/json")
    public int checkPhone(@RequestBody String phoneNumber) throws Exception {
        return packetService.checkPhone(phoneNumber);
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

    @GetMapping(path = "/productsCount/{modelId}")
    public List<ProductsDayCountDTO> productsCount(
            @PathVariable Long modelId ,
            @RequestParam(required = true) String beginDate,
            @RequestParam(required = true) String endDate) {
        return packetService.productsCountByDate(modelId,beginDate,endDate);
    }

    @GetMapping(path = "/statModelSold/{modelId}")
    public Map <String , List<?>> statModelSold(
            @PathVariable Long modelId ,
            @RequestParam(required = true) String beginDate,
            @RequestParam(required = true) String endDate) {
        return packetService.statModelSoldChart(modelId,beginDate,endDate);

    }

    @GetMapping(path = "/statAllModels")
    public Map <String , List<?>> statAllModels(
            @RequestParam(required = true) String beginDate,
            @RequestParam(required = true) String endDate) {
        return packetService.statAllModelsChart(beginDate,endDate);
    }
}
