package com.clothing.management.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "packet", indexes = {
        @Index(name = "idx_customer_phone_nb", columnList = "customer_phone_nb"),
        @Index(name = "idx_barcode", columnList = "barcode"),
})
public class Packet {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "customer_name")
    private String customerName;
    @Column(name = "customer_phone_nb")
    private String customerPhoneNb;
    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "city_id")
    private City city;
    private String address;

    @Column(name = "packet_description")
    private String packetDescription;

    private String barcode;
    @Column(name = "last_delivery_status")
    private String lastDeliveryStatus;
    @Column(name = "old_client")
    private Integer oldClient;

    @OneToMany(mappedBy = "packet" , cascade = {CascadeType.PERSIST, CascadeType.PERSIST}, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<ProductsPacket> products;
    @JsonIgnore
    @OneToMany(mappedBy = "packet", fetch = FetchType.EAGER)
    List<PacketStatus> packetStatus;
    @ManyToOne
    @JoinColumn(name = "fbpage_id")
    private FbPage fbPage;
    @ManyToOne
    @JoinColumn(name = "delivery_company")
    private DeliveryCompany deliveryCompany;
    private double price;
    @Column(name = "delivery_price")
    private double deliveryPrice;
    private double discount;
    private Date date;
    private String status;
    @Column(name = "last_update_date")
    private Date lastUpdateDate;
    private boolean exchange;
    @Column(name = "print_link")
    private String printLink;

    @Column(name = "exchange_id")
    private Long exchangeId;

    private boolean valid;

    private Integer stock;

    private Integer attempt;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JsonIgnore
    private List<Note> notes;

    public Packet() {
    }

    public Packet(DeliveryCompany deliveryCompany) {
        this.customerName= "";
        this.customerPhoneNb= "";
        this.oldClient= 0;
        this.address= "";
        this.packetDescription= "";
        this.barcode= "";
        this.lastDeliveryStatus = "";
        this.packetStatus = null;
        this.fbPage = null;
        this.price= 0;
        this.deliveryPrice = 0;
        this.discount = 0;
        this.status= "Non confirmée";
        this.date=new Date();
        this.status = null;
        this.lastUpdateDate = null;
        this.valid= false;
        this.stock= -1;
        this.printLink = null;
        this.deliveryCompany=deliveryCompany;
        this.attempt = 0;
    }

    public Packet(Long id, String customerName, String customerPhoneNb, Integer oldClient, City city, String address, String packetDescription, String barcode, String lastDeliveryStatus, List<ProductsPacket> products, List<PacketStatus> packetStatus, FbPage fbPage, double price, double deliveryPrice, double discount, Date date, String status, Date lastUpdateDate, boolean exchange, boolean valid, Integer stock, String printLink,DeliveryCompany deliveryCompany,Integer attempt, Long exchangeId) {
        this.id = id;
        this.customerName = customerName;
        this.customerPhoneNb = customerPhoneNb;
        this.oldClient = oldClient;
        this.city = city;
        this.address = address;
        this.packetDescription = packetDescription;
        this.barcode = barcode;
        this.lastDeliveryStatus = lastDeliveryStatus;
        this.products = products;
        this.packetStatus = packetStatus;
        this.fbPage = fbPage;
        this.price = price;
        this.deliveryPrice = deliveryPrice;
        this.discount = discount;
        this.date = date;
        this.status = status;
        this.lastUpdateDate = lastUpdateDate;
        this.valid = valid;
        this.stock = stock;
        this.printLink = printLink;
        this.deliveryCompany = deliveryCompany;
        this.attempt = attempt;
        this.exchangeId = exchangeId;
    }

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

    public City getCity() { return city; }

    public void setCity(City city) { this.city = city; }

    public String getAddress() { return address; }

    public void setAddress(String address) { this.address = address; }

    public List<ProductsPacket> getProducts() {
        return products;
    }

    public void setProducts(List<ProductsPacket> products) {
        this.products = products;
    }

    public FbPage getFbPage() {
        return fbPage;
    }

    public void setFbPage(FbPage fbPage) {
        this.fbPage = fbPage;
    }

    public double getPrice() {
        return price;
    }

    public String getPacketDescription() {
        return packetDescription;
    }

    public void setPacketDescription(String packetDescription) {
        this.packetDescription = packetDescription;
    }

    public void setPrice(double price) {
        this.price = price;
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

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public Date getLastUpdateDate() {
        return lastUpdateDate;
    }

    public void setLastUpdateDate(Date lastUpdateDate) {
        this.lastUpdateDate = lastUpdateDate;
    }

    public String getLastDeliveryStatus() {
        return lastDeliveryStatus;
    }

    public void setLastDeliveryStatus(String lastDeliveryStatus) {
        this.lastDeliveryStatus = lastDeliveryStatus;
    }

    public List<PacketStatus> getPacketStatus() {
        return packetStatus;
    }

    public void setPacketStatus(List<PacketStatus> packetStatus) {
        this.packetStatus = packetStatus;
    }

    public boolean isExchange() {
        return exchange;
    }

    public void setExchange(boolean exchange) {
        this.exchange = exchange;
    }

    public String getPrintLink() {
        return printLink;
    }

    public void setPrintLink(String printLink) {
        this.printLink = printLink;
    }

    public Integer getOldClient() {
        return oldClient;
    }

    public void setOldClient(Integer oldClient) {
        this.oldClient = oldClient;
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

    public DeliveryCompany getDeliveryCompany() {
        return deliveryCompany;
    }

    public Integer getAttempt() {
        return attempt;
    }

    public void setAttempt(Integer attempt) {
        this.attempt = attempt;
    }

    public void setDeliveryCompany(DeliveryCompany deliveryCompany) {
        this.deliveryCompany = deliveryCompany;
    }

    public Long getExchangeId() {
        return exchangeId;
    }

    public void setExchangeId(Long exchangeId) {
        this.exchangeId = exchangeId;
    }

    public List<Note> getNotes() {
        return notes;
    }

    public void setNotes(List<Note> notes) {
        this.notes = notes;
    }

    @Override
    public String toString() {
        return "Packet{" +
                "id=" + id +
                ", customerName='" + customerName + '\'' +
                ", customerPhoneNb='" + customerPhoneNb + '\'' +
                ", oldClient='" + oldClient + '\'' +
                ", address='" + address + '\'' +
                ", packetDescription='" + packetDescription + '\'' +
                ", barcode='" + barcode + '\'' +
                ", lastDeliveryStatus='" + lastDeliveryStatus + '\'' +
                ", price=" + price +
                ", deliveryPrice=" + deliveryPrice +
                ", discount=" + discount +
                ", date=" + date +
                ", status='" + status + '\'' +
                ", lastUpdateDate=" + lastUpdateDate +
                ", stock=" + stock +
                ", printLink='" + printLink + '\'' +
                ", attempt='" + attempt + '\'' +
                ", exchangeId='" + exchangeId + '\'' +
                '}';

    }
}
