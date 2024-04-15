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

    public List<Packet> findAllPackets();
    public Page<Packet> findAllPackets(String searchText, String startDate, String endDate, String status, Pageable pageable, boolean mandatoryDate);
    //public Page<Packet> findAllPackets(String searchText, int page, int size);
    //public Page<Packet> findAllTodaysPackets(Pageable paging);
    public List<Packet> findAllPacketsByDate(String start,String end);
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
    public List<PacketStatus> findPacketTimeLineById(Long idPacket);
    DeliveryResponse createBarCode(Packet packet) throws IOException, InterruptedException;
    Packet getLastStatus(Packet packet) throws Exception;
    Packet addAttempt(Packet packet,String note) throws ParseException;
    int checkPhone(String phoneNumber);
    List<DashboardCard> createDashboard();
    List<DashboardCard> syncNotification();
    Packet duplicatePacket(Long idPacketidPacket);
    List<String> updatePacketsByBarCodes(BarCodeStatusDTO barCodeStatusDTO);
    Long getExchangeId(Packet packet);
    int deleteEmptyPacket();
    Packet updatePacketValid(String barCode,String type) throws Exception;
}
