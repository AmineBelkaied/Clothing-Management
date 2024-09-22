package com.clothing.management.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PacketValidationDTO {
    private Long id;
    private String customerName;
    private String customerPhoneNb;
    private String packetDescription;
    private String barcode;
    private String fbPageName;
    private String deliveryCompanyName;
    private double price;
    private Date date;
    private boolean valid;
    private Long exchangeId;
    private boolean haveExchange;
}
