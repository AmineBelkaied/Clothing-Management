package com.clothing.management.dto;

import com.clothing.management.entities.*;

import java.util.Date;
import java.util.List;

public class PacketDTO {
    private Long id;
    private Date date;
    private String customerName;
    private String customerPhoneNb;
    private City city;
    private String address;
    private String packetDescription;
    private String barcode;
    private String lastDeliveryStatus;
    private Integer oldClient;
    private List<ProductsPacketDTO> productsPackets;
    private FbPage fbPage;
    private DeliveryCompany deliveryCompany;
    private double price;
    private double deliveryPrice;
    private double discount;
    private String status;
    private Date lastUpdateDate;
    private String printLink;
    private boolean valid;
    private Integer stock;
    private Integer attempt;
    private String note;
    private Integer productCount;
    private Long exchangeId;
    private boolean haveExchange;
    public PacketDTO() {
        }
    public PacketDTO(Packet packet) {
        this.id = packet.getId();
        this.customerName= packet.getCustomerName();
        this.customerPhoneNb= packet.getCustomerPhoneNb();
        this.oldClient= packet.getOldClient();
        this.city =packet.getCity();
        this.address= packet.getAddress();
        this.packetDescription= packet.getPacketDescription();
        this.barcode= packet.getBarcode();
        this.lastDeliveryStatus = packet.getLastDeliveryStatus();
        this.fbPage = packet.getFbPage();
        this.price= packet.getPrice();
        this.deliveryPrice = packet.getDeliveryPrice();
        this.discount = packet.getDiscount();
        this.status= packet.getStatus();
        this.date= packet.getDate();
        this.lastUpdateDate = packet.getLastUpdateDate();
        this.valid= packet.isValid();
        this.stock= packet.getProductsPackets().size()>0?getStock(packet.getProductsPackets(), packet.getBarcode()):0;
        this.printLink = packet.getPrintLink();
        this.deliveryCompany=packet.getDeliveryCompany();
        this.attempt = packet.getAttempt();
        this.note = packet.getNote();
        this.haveExchange=packet.isHaveExchange();
        this.productCount=packet.getProductCount();
    }

    private int getStock(List<ProductsPacket> productsPackets, String barcode){
        return (barcode == null || barcode.equals(""))?productsPackets.stream()
                .mapToInt(productsPacket -> productsPacket.getProduct().getQuantity()) // Assuming getQte() returns the quantity
                .min()
                .orElse(100):100;
    }
    /*private void setPacketDescription(List<ProductsPacket> productsPackets, String barcode){
        String description = productsPackets.stream()
                .mapToInt(productsPacket -> productsPacket.getProduct().getQuantity()) // Assuming getQte() returns the quantity
                .min()
                .orElse(100);
        return; description;
    }*/

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
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

    public City getCity() {
        return city;
    }

    public void setCity(City city) {
        this.city = city;
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

    public List<ProductsPacketDTO> getProductsPackets() {
        return productsPackets;
    }

    public void setProductsPackets(List<ProductsPacketDTO> productsPackets) {
        this.productsPackets = productsPackets;
    }

    public FbPage getFbPage() {
        return fbPage;
    }

    public void setFbPage(FbPage fbPage) {
        this.fbPage = fbPage;
    }

    public DeliveryCompany getDeliveryCompany() {
        return deliveryCompany;
    }

    public void setDeliveryCompany(DeliveryCompany deliveryCompany) {
        this.deliveryCompany = deliveryCompany;
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

    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }

    public Integer getAttempt() {
        return attempt;
    }

    public void setAttempt(Integer attempt) {
        this.attempt = attempt;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public Integer getProductCount() {
        return productCount;
    }

    public void setProductCount(Integer productCount) {
        this.productCount = productCount;
    }

    public boolean isHaveExchange() {
        return haveExchange;
    }

    public void setHaveExchange(boolean haveExchange) {
        this.haveExchange = haveExchange;
    }
}
