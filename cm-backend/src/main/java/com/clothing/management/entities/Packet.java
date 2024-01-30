package com.clothing.management.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
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
    private String customerName;
    @Column(name = "customer_phone_nb")
    private String customerPhoneNb;
    @OneToOne
    @JoinColumn(name = "city_id")
    private City city;
    private String address;
    private String relatedProducts;
    private String packetDescription;
    private String packetReference;
    private String barcode;
    private String lastDeliveryStatus;

    private Integer oldClient;

    @OneToMany(mappedBy = "packet" , cascade = {CascadeType.PERSIST, CascadeType.PERSIST}, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<ProductsPacket> products;
    @JsonIgnore
    @OneToMany(mappedBy = "packet")
    List<PacketStatus> packetStatus;
    @ManyToOne
    @JoinColumn(name = "fbpage_id")
    private FbPage fbPage;
    @ManyToOne
    @JoinColumn(name = "deliveryCompany")
    private DeliveryCompany deliveryCompany;
    private double price;
    private double deliveryPrice;
    private double discount;
    private Date date;
    private String status;
    private Date lastUpdateDate;
    private boolean exchange;
    private String printLink;

    private boolean valid;

    private Integer stock;

    public Packet() {
    }

    public Packet(Long id, String customerName, String customerPhoneNb, Integer oldClient, City city, String address, String relatedProducts, String packetDescription, String packetReference, String barcode, String lastDeliveryStatus, List<ProductsPacket> products, List<PacketStatus> packetStatus, FbPage fbPage, double price, double deliveryPrice, double discount, Date date, String status, Date lastUpdateDate, String dgStatus, boolean exchange, boolean valid, Integer stock, String printLink,DeliveryCompany deliveryCompanyId) {
        this.id = id;
        this.customerName = customerName;
        this.customerPhoneNb = customerPhoneNb;
        this.oldClient = oldClient;
        this.city = city;
        this.address = address;
        this.relatedProducts = relatedProducts;
        this.packetDescription = packetDescription;
        this.packetReference = packetReference;
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
        this.exchange = exchange;
        this.valid = valid;
        this.stock = stock;
        this.printLink = printLink;
        this.deliveryCompany = deliveryCompany;
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

    public String getRelatedProducts() { return relatedProducts; }

    public void setRelatedProducts(String relatedProducts) { this.relatedProducts = relatedProducts; }

    public String getPacketReference() { return packetReference; }

    public void setPacketReference(String packetReference) { this.packetReference = packetReference; }

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

    public void setDeliveryCompany(DeliveryCompany deliveryCompany) {
        this.deliveryCompany = deliveryCompany;
    }

    @Override
    public String toString() {
        return "Packet{" +
                "id=" + id +
                ", customerName='" + customerName + '\'' +
                ", customerPhoneNb='" + customerPhoneNb + '\'' +
                ", oldClient='" + oldClient + '\'' +
                ", city=" + city +
                ", address='" + address + '\'' +
                ", packetDescription='" + packetDescription + '\'' +
                ", packetReference='" + packetReference + '\'' +
                ", barcode='" + barcode + '\'' +
                ", lastDeliveryStatus='" + lastDeliveryStatus + '\'' +
                ", fbPage=" + fbPage +
                ", price=" + price +
                ", deliveryPrice=" + deliveryPrice +
                ", discount=" + discount +
                ", date=" + date +
                ", status='" + status + '\'' +
                ", lastUpdateDate=" + lastUpdateDate +
                ", exchange=" + exchange +
                ", stock=" + stock +
                ", printLink='" + printLink + '\'' +
                ", deliveryCompanyId='" + deliveryCompany + '\'' +
                '}';
    }
}
