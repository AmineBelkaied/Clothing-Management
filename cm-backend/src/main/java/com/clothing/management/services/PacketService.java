package com.clothing.management.services;

import com.clothing.management.dto.*;
import com.clothing.management.dto.DeliveryCompanyDTOs.BarcodeStatusDTO;
import com.clothing.management.dto.DeliveryCompanyDTOs.DeliveryResponse;
import com.clothing.management.entities.Note;
import com.clothing.management.entities.Packet;
import com.clothing.management.entities.User;
import com.clothing.management.exceptions.custom.notfound.PacketNotFoundException;
import com.clothing.management.models.DashboardCard;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.io.IOException;
import java.text.ParseException;
import java.util.*;

public interface PacketService {

 Packet getPacketById(Long packetId) throws Exception;

 Packet getPacketByBarcode(String barcode) throws Exception;
 List<Packet> findAllPackets();
 Page<PacketDTO> findAllPackets(Pageable pageable, String searchText, String beginDate, String endDate, String status) throws ParseException;
 List<PacketValidationDTO> findValidationPackets();
 List<PacketDTO> findAllPacketsByDate(String start,String end) throws ParseException;
 List<Packet> findAllPacketsByDate(Date date);
 List<Packet> findSyncPackets();
 List<ProductsPacketDTO> findPacketRelatedProducts(Long idPacket) throws Exception;
 Packet addPacket() throws Exception;
 Packet updatePacket(Packet packet);
 Packet patchPacket(Long idPacket , Map<String , Object> packet) throws Exception;
 PacketDTO addProductsToPacket(SelectedProductsDTO selectedProductsDTO) throws Exception;
 List<PacketDTO> deleteSelectedPackets(List<Long> packetsId, Note note) throws PacketNotFoundException, Exception;
 List<PacketStatusDTO> findPacketTimeLineById(Long idPacket) throws Exception;
 DeliveryResponse createBarCode(Packet packet) throws IOException, InterruptedException;
 PacketDTO getLastStatus(long packetId) throws Exception;
 PacketDTO getLastStatus(Packet packet, User user) throws Exception;
 Packet addAttempt(Note note, Long packetId) throws PacketNotFoundException;
 int checkPhone(String phoneNumber);
 List<DashboardCard> syncNotification(String searchField,String beginDate, String endDate);
 PacketDTO duplicatePacket(Long idPacket) throws Exception;
 List<String> updatePacketsByBarcodes(BarcodeStatusDTO barcodeStatusDTO);
 int deleteEmptyPacket();
 PacketValidationDTO updatePacketValid(String barcode, String type) throws Exception;
}
