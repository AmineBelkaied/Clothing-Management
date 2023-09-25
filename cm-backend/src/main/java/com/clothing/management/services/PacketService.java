package com.clothing.management.services;

import com.clothing.management.dto.*;
import com.clothing.management.entities.Packet;
import com.clothing.management.entities.PacketStatus;
import com.clothing.management.models.DashboardCard;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface PacketService {

    public List<Packet> findAllPackets();
    public Page<Packet> findAllPackets(String searchText, String startDate, String endDate, String status, Pageable pageable);
    //public Page<Packet> findAllPackets(String searchText, int page, int size);
    public Page<Packet> findAllTodaysPackets(Pageable paging);
    public List<Packet> findAllPacketsByDate(String start,String end);
    public List<Packet> findAllPacketsByDate(Date date);
    public List<Packet> findAllDiggiePackets();
    public Optional<Packet> findPacketById(Long idPacket);
    public PacketDTO findPacketRelatedProducts(Long idPacket);
    public Packet addPacket(Packet packet);
    public Packet updatePacket(Packet packet);
    public Packet patchPacket(Long idPacket , Map<String , Object> packet) throws IOException;
    public void addProductsToPacket(SelectedProductsDTO selectedProductsDTO);
    public void deletePacketById(Long idPacket);
    public void deleteSelectedPackets(List<Long> packetsId);
    public List<PacketStatus> findPacketTimeLineById(Long idPacket);
    DeliveryResponseFirst createBarCode(Packet packet, String deliveryCompany) throws IOException, InterruptedException;
    Packet getLastStatus(Packet packet, String deliveryCompany) throws Exception;
    int checkPhone(String phoneNumber);
    public List<DashboardCard> createDashboard();
    Packet duplicatePacket(Long idPacket);
    List<String> updatePacketsByBarCodes(BarCodeStatusDTO barCodeStatusDTO);
    Long getExchangeId(Packet packet);
    List<ProductsDayCountDTO> productsCountByDate(Long state, String beginDate, String endDate);
}
