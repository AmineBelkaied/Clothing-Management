package com.clothing.management.services;

import com.clothing.management.dto.OfferUpdateDTO;
import com.clothing.management.dto.SelectedProductsDTO;
import com.clothing.management.entities.Packet;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface PacketService {

    public List<Packet> findAllPackets();
    public List<Packet> findAllTodaysPackets();
    public List<Packet> findAllPacketsByDate(Date date);
    public Optional<Packet> findPacketById(Long idPacket);
    public List<OfferUpdateDTO> findPacketRelatedProducts(Long idPacket);
    public Packet addPacket(Packet packet);
    public Packet updatePacket(Packet packet);
    public Packet patchPacket(Long idPacket , Map<String , Object> packet);
    public void addProductsToPacket(SelectedProductsDTO selectedProductsDTO);
    public void deletePacketById(Long idPacket);
    public void deleteSelectedPackets(List<Long> packetsId);
}
