package com.clothing.management.services;

import com.clothing.management.dto.*;
import com.clothing.management.entities.Packet;
import com.clothing.management.entities.PacketStatus;
import com.clothing.management.models.DashboardCard;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.io.IOException;
import java.text.ParseException;
import java.util.*;

public interface PacketService {

 Packet getPacketById(Long packetId) throws Exception;

 Packet getPacketByBarcode(String barCode) throws Exception;

 public List<Packet> findAllPackets();
    public Page<Packet> findAllPackets(Pageable pageable, String searchText, String startDate, String endDate, String status, boolean mandatoryDate) throws ParseException;
    public List<PacketDTO> findAllPacketsByDate(String start,String end) throws ParseException;
   public List<Packet> findAllPacketsByDate(Date date);
    public List<Packet> findAllDiggiePackets();
    //public Optional<Packet> findPacketById(Long idPacket);
    public List<ProductsPacketDTO> findPacketRelatedProducts(Long idPacket) throws Exception;
    public Packet addPacket() throws Exception;
    public Packet updatePacket(Packet packet);
    public Packet patchPacket(Long idPacket , Map<String , Object> packet) throws Exception;
    public PacketDTO addProductsToPacket(SelectedProductsDTO selectedProductsDTO) throws Exception;
    // List<Packet> checkPacketProductsValidity(Long packetId);
    public void deletePacketById(Long idPacket) throws Exception;
    public void deleteSelectedPackets(List<Long> packetsId) throws Exception;
    public List<PacketStatus> findPacketTimeLineById(Long idPacket) throws Exception;
    DeliveryResponse createBarCode(Packet packet) throws IOException, InterruptedException;
    PacketDTO getLastStatus(long packetId) throws Exception;
    Packet getLastStatus(Packet packet) throws Exception;
    Packet addAttempt(Long packetId,String note);
    int checkPhone(String phoneNumber);
    //List<DashboardCard> createDashboard();
    List<DashboardCard> syncNotification(String beginDate, String endDate);
    PacketDTO duplicatePacket(Long idPacketidPacket) throws Exception;
    List<String> updatePacketsByBarCodes(BarCodeStatusDTO barCodeStatusDTO);
    //Long getExchangeId(Packet packet);
    int deleteEmptyPacket();

 PacketValidationDTO updatePacketValid(String barCode, String type) throws Exception;
}
