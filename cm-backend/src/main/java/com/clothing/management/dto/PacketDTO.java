package com.clothing.management.dto;

import com.clothing.management.entities.*;

import java.util.Date;
import java.util.List;

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

    public PacketDTO() {
    }

    public PacketDTO(Packet packet) {
            FbPage fbPage =packet.getFbPage();
            City city = packet.getCity();
            this.id = packet.getId();
            this.customerName= packet.getCustomerName();
            this.customerPhoneNb= packet.getCustomerPhoneNb();
            this.oldClient= packet.getOldClient();
            this.cityId = city != null ? city.getId() : null;
            this.address= packet.getAddress();
            this.packetDescription= packet.getPacketDescription();
            this.barcode= packet.getBarcode();
            this.lastDeliveryStatus = packet.getLastDeliveryStatus();
            this.fbPage = fbPage != null ? new FbPageDTO(fbPage) : null;
            this.price= packet.getPrice();
            this.deliveryPrice = packet.getDeliveryPrice();
            this.discount = packet.getDiscount();
            this.status= packet.getStatus();
            this.date= packet.getDate();
            this.lastUpdateDate = packet.getLastUpdateDate();
            this.valid= packet.isValid();
            this.stock= packet.getProductsPackets().size()>0?getStock(packet.getProductsPackets(), packet.getBarcode()):0;
            this.printLink = packet.getPrintLink();
            this.deliveryCompanyName =packet.getDeliveryCompany().getName();
            this.haveExchange=packet.isHaveExchange();
            this.notes = packet.getNotes();
            this.totalPrice = packet.getPrice()+packet.getDeliveryPrice()-packet.getDiscount();
            this.cityName = city != null ? city.getGovernorate().getName() + '-' + city.getName() : "";
    }

    private long getStock(List<ProductsPacket> productsPackets, String barcode){
        return (barcode == null || barcode.equals(""))?productsPackets.stream()
                .mapToLong(productsPacket -> productsPacket.getProduct().getQuantity()) // Assuming getQte() returns the quantity
                .min()
                .orElse(-1):100;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getCustomerPhoneNb() {
        return customerPhoneNb;
    }

    public void setCustomerPhoneNb(String customerPhoneNb) {
        this.customerPhoneNb = customerPhoneNb;
    }

    public Long getCityId() {
        return cityId;
    }

    public void setCityId(Long cityId) {
        this.cityId = cityId;
    }

    public String getCityName() {
        return cityName;
    }
    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPacketDescription() {
        return packetDescription;
    }

    public void setPacketDescription(String packetDescription) {
        this.packetDescription = packetDescription;
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public String getLastDeliveryStatus() {
        return lastDeliveryStatus;
    }

    public void setLastDeliveryStatus(String lastDeliveryStatus) {
        this.lastDeliveryStatus = lastDeliveryStatus;
    }

    public Integer getOldClient() {
        return oldClient;
    }

    public void setOldClient(Integer oldClient) {
        this.oldClient = oldClient;
    }

    public FbPageDTO getFbPage() {
        return fbPage;
    }

    public void setFbPage(FbPageDTO fbPage) {
        this.fbPage = fbPage;
    }

    public String getDeliveryCompanyName() {
        return deliveryCompanyName;
    }

    public void setDeliveryCompanyName(String deliveryCompanyName) {
        this.deliveryCompanyName = deliveryCompanyName;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public double getDeliveryPrice() {
        return deliveryPrice;
    }

    public void setDeliveryPrice(double deliveryPrice) {
        this.deliveryPrice = deliveryPrice;
    }

    public double getDiscount() {
        return discount;
    }

    public void setDiscount(double discount) {
        this.discount = discount;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Date getLastUpdateDate() {
        return lastUpdateDate;
    }

    public void setLastUpdateDate(Date lastUpdateDate) {
        this.lastUpdateDate = lastUpdateDate;
    }

    public String getPrintLink() {
        return printLink;
    }

    public void setPrintLink(String printLink) {
        this.printLink = printLink;
    }

    public Long getExchangeId() {
        return exchangeId;
    }

    public void setExchangeId(Long exchangeId) {
        this.exchangeId = exchangeId;
    }

    public boolean isValid() {
        return valid;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }

    public Long getStock() {
        return stock;
    }

    public void setStock(Long stock) {
        this.stock = stock;
    }

    public boolean isHaveExchange() {
        return haveExchange;
    }

    public void setHaveExchange(boolean haveExchange) {
        this.haveExchange = haveExchange;
    }

    public List<Note> getNotes() {
        return notes;
    }

    public void setNotes(List<Note> notes) {
        this.notes = notes;
    }
}
