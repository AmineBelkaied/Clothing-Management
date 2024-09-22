package com.clothing.management.dto;

import com.clothing.management.entities.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PacketDTO {
    private long id;
    private Date date;
    private String customerName;
    private String customerPhoneNb;
    private Long cityId;

    private String cityName;
    private String address;
    private String packetDescription;
    private String barcode;
    private String lastDeliveryStatus;
    private Integer oldClient;
    private FbPageDTO fbPage;
    private String deliveryCompanyName;
    private double price;
    private double deliveryPrice;
    private double discount;
    private String status;
    private Date lastUpdateDate;
    private String printLink;
    private boolean valid;
    private long stock;
    private long exchangeId;
    private boolean haveExchange;
    private double totalPrice;

    private List<Note> notes;
}
