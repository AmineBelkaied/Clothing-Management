package com.clothing.management.controllers;

import com.clothing.management.dto.OfferUpdateDTO;
import com.clothing.management.dto.SelectedProductsDTO;
import com.clothing.management.entities.Packet;
import com.clothing.management.services.PacketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.core.annotation.RepositoryEventHandler;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("packet")
@CrossOrigin
public class PacketController {

    @Autowired
    PacketService packetService;

    @GetMapping(path = "/findAll")
    public List<Packet> findAllPackets() {
        return packetService.findAllPackets();
    }

    @GetMapping(path = "/findById/{id}")
    public Optional<Packet> findByIdPacket(@PathVariable Long idPacket) {
        return packetService.findPacketById(idPacket);
    }

    @GetMapping(path = "/findPacketRelatedProducts/{idPacket}")
    public List<OfferUpdateDTO> findPacketRelatedProducts(@PathVariable Long idPacket) {
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
    public Packet patchPacket(@PathVariable Long idPacket , @RequestBody Map<String , Object> field) {
        System.out.println("new ");
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
}
