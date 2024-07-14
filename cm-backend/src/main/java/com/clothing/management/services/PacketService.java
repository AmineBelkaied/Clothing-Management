package com.clothing.management.services;

import com.clothing.management.dto.*;
import com.clothing.management.entities.Note;
import com.clothing.management.entities.Packet;
import com.clothing.management.entities.PacketStatus;
import com.clothing.management.models.DashboardCard;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.io.IOException;
import java.text.ParseException;
import java.util.*;

public interface PacketService {

    public List<Packet> findAllPackets();
    public Page<Packet> findAllPackets(Pageable pageable, String searchText, String startDate, String endDate, String status, boolean mandatoryDate) throws ParseException;
    public List<Packet> findAllPacketsByDate(String start,String end) throws ParseException;
   public List<Packet> findAllPacketsByDate(Date date);
    public List<Packet> findAllDiggiePackets();
    public Optional<Packet> findPacketById(Long idPacket);
    public PacketDTO findPacketRelatedProducts(Long idPacket);
    public Packet addPacket();
    public Packet updatePacket(Packet packet);
    public Packet patchPacket(Long idPacket , Map<String , Object> packet) throws IOException;
    public Packet addProductsToPacket(SelectedProductsDTO selectedProductsDTO,Integer stock);
    public List<Packet> checkPacketProductsValidity(Long packetId);
    public void deletePacketById(Long idPacket);
    public void deleteSelectedPackets(List<Long> packetsId);
    public List<PacketStatus> findPacketTimeLineById(Long idPacket) throws Exception;
    DeliveryResponse createBarCode(Packet packet) throws IOException, InterruptedException;
    Packet getLastStatus(Packet packet) throws Exception;
    Packet addAttempt(Note note, Long packetId) throws Exception;
    int checkPhone(String phoneNumber);
    //List<DashboardCard> createDashboard();
    List<DashboardCard> syncNotification(String beginDate, String endDate);
    Packet duplicatePacket(Long idPacketidPacket);
    List<String> updatePacketsByBarCodes(BarCodeStatusDTO barCodeStatusDTO);
    //Long getExchangeId(Packet packet);
    int deleteEmptyPacket();

    void updatePacketStockForRupture();
    Packet updatePacketValid(String barCode,String type) throws Exception;
}
