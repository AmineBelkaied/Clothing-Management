package com.clothing.management.controllers;

import com.clothing.management.dto.*;
import com.clothing.management.entities.Packet;
import com.clothing.management.entities.PacketStatus;
import com.clothing.management.scheduler.UpdateStatusScheduler;
import com.clothing.management.services.PacketService;
import org.springframework.beans.factory.annotation.Autowired;
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

    @GetMapping(path = "/findAllTodaysPackets")
    public List<Packet> findAllTodaysPackets() {
        return packetService.findAllTodaysPackets();
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

    @PostMapping(value = "/updateStatus/{idPacket}/{status}")
    public void updatePacketStatus(@PathVariable Long idPacket , @PathVariable String status) {
          packetService.savePacketStatusToHistory(idPacket , status);
    }
    @GetMapping(path = "/findPacketStatus/{idPacket}")
    public List<PacketStatus> findAllPacketStatus(@PathVariable Long idPacket) {
        return packetService.findPacketStatusById(idPacket);
    }

    @PostMapping(value = "/createBarCode", produces = "application/json")
    public ResponseEntity<DeliveryResponseFirst>  createBarCode(@RequestBody Packet packet, @RequestParam("deliveryCompany") String deliveryCompany) throws IOException, InterruptedException {
        DeliveryResponseFirst deliveryResponse = packetService.createBarCode(packet, deliveryCompany);
            if(deliveryResponse.getResponseCode() != 200)
                return new ResponseEntity<>(deliveryResponse, HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(deliveryResponse, HttpStatus.OK);
    }

    @PostMapping(value = "/getLastStatus", produces = "application/json")
    public ResponseEntity<Packet> getLastStatus(@RequestBody Packet packet, @RequestParam("deliveryCompany") String deliveryCompany) throws Exception {
        return new ResponseEntity<>(
                packetService.getLastStatus(packet, deliveryCompany),
                HttpStatus.OK);
    }


    @GetMapping(path = "/duplicatePacket/{idPacket}")
    public Packet duplicatePacket(@PathVariable Long idPacket) {
        return packetService.duplicatePacket(idPacket);
    }
    @GetMapping(path = "/syncAllPacketsStatus")
    public void synchronizeAllPacketsStatus() throws Exception {
        updateStatusScheduler.cronJobSch();
    }

    @PostMapping(value = "/updatePacketsByBarCode", produces = "application/json")
    public ResponseEntity<List<String>> updatePacketsByBarCode(@RequestBody BarCodeStatusDTO barCodeStatusDTO) {
        try {
            return new ResponseEntity<>(packetService.updatePacketsByBarCode(barCodeStatusDTO), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(packetService.updatePacketsByBarCode(barCodeStatusDTO), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
