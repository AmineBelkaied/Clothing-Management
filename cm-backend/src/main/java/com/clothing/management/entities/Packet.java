package com.clothing.management.entities;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

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

    @OneToMany(mappedBy = "packet" , cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<ProductsPacket> productsPackets;

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

    private Integer attempt;
    private String note;

    @Column(name = "product_count")
    private Integer productCount;


    @Column(name = "have_exchange")
    private boolean haveExchange;

    public void addProductsToPacket(List<ProductsPacket> productsPacket) {
        for (ProductsPacket productPacket : productsPacket) {
            productPacket.setPacket(this);
        }
        this.productsPackets.clear(); // Clear existing productsPackets if necessary
        this.productsPackets.addAll(productsPacket);
    }

    public Packet() {
        this.productsPackets=new ArrayList<>();
        this.packetStatus = new ArrayList<>();
    }

    public Packet(DeliveryCompany deliveryCompany) {
        this.customerName= "";
        this.customerPhoneNb= "";
        this.oldClient= 0;
        this.address= "";
        this.packetDescription= "";
        this.barcode= "";
        this.lastDeliveryStatus = "";
        this.packetStatus = new ArrayList<>();
        this.fbPage = null;
        this.price= 0;
        this.deliveryPrice = 0;
        this.discount = 0;
        this.date=new Date();
        this.status = null;
        this.lastUpdateDate = null;
        this.valid= false;
        this.printLink = null;
        this.deliveryCompany=deliveryCompany;
        this.attempt = 0;
        this.note = "";
        this.haveExchange=false;
        this.productCount=0;
        this.productsPackets = new ArrayList<>();
    }

    public Packet(Long id, String customerName, String customerPhoneNb, Integer oldClient, City city, String address, String packetDescription, String barcode, String lastDeliveryStatus, List<ProductsPacket> productsPackets, List<PacketStatus> packetStatus, FbPage fbPage, double price, double deliveryPrice, double discount, Date date, String status, Date lastUpdateDate, boolean exchange, boolean valid, Integer stock, String printLink,DeliveryCompany deliveryCompany,Integer attempt, String note, Long exchangeId) {
        this.id = id;
        this.customerName = customerName;
        this.customerPhoneNb = customerPhoneNb;
        this.oldClient = oldClient;
        this.city = city;
        this.address = address;
        this.packetDescription = packetDescription;
        this.barcode = barcode;
        this.lastDeliveryStatus = lastDeliveryStatus;
        this.productsPackets = productsPackets;
        this.packetStatus = packetStatus;
        this.fbPage = fbPage;
        this.price = price;
        this.deliveryPrice = deliveryPrice;
        this.discount = discount;
        this.date = date;
        this.status = status;
        this.lastUpdateDate = lastUpdateDate;
        this.valid = valid;
        this.printLink = printLink;
        this.deliveryCompany = deliveryCompany;
        this.attempt = attempt;
        this.exchangeId = exchangeId;
        this.note=note;
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

    public List<ProductsPacket> getProductsPackets() {
        return productsPackets;
    }

    public void setProductsPackets(List<ProductsPacket> productsPackets) {
        this.productsPackets = productsPackets;
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

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public Long getExchangeId() {
        return exchangeId;
    }

    public void setExchangeId(Long exchangeId) {
        this.exchangeId = exchangeId;
    }

    public boolean isHaveExchange() {
        return haveExchange;
    }

    public void setHaveExchange(boolean haveExchange) {
        this.haveExchange = haveExchange;
    }

    public Integer getProductCount() {
        return productCount;
    }

    public void setProductCount(Integer productCount) {
        this.productCount = productCount;
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
                ", printLink='" + printLink + '\'' +
                ", attempt='" + attempt + '\'' +
                ", note='" + note + '\'' +
                ", exchangeId='" + exchangeId + '\'' +
                ", haveExchange='" + haveExchange + '\'' +
                ", productCount='" + productCount + '\'' +
                ", valid='" + valid + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Packet packet)) return false;
        return Double.compare(packet.price, price) == 0 && Double.compare(packet.deliveryPrice, deliveryPrice) == 0 && Double.compare(packet.discount, discount) == 0 && exchange == packet.exchange && valid == packet.valid && haveExchange == packet.haveExchange && id.equals(packet.id) && Objects.equals(customerName, packet.customerName) && Objects.equals(customerPhoneNb, packet.customerPhoneNb) && Objects.equals(city, packet.city) && Objects.equals(address, packet.address) && Objects.equals(packetDescription, packet.packetDescription) && Objects.equals(barcode, packet.barcode) && Objects.equals(lastDeliveryStatus, packet.lastDeliveryStatus) && Objects.equals(oldClient, packet.oldClient) && Objects.equals(productsPackets, packet.productsPackets) && Objects.equals(packetStatus, packet.packetStatus) && Objects.equals(fbPage, packet.fbPage) && Objects.equals(deliveryCompany, packet.deliveryCompany) && Objects.equals(date, packet.date) && Objects.equals(status, packet.status) && Objects.equals(lastUpdateDate, packet.lastUpdateDate) && Objects.equals(printLink, packet.printLink) && Objects.equals(exchangeId, packet.exchangeId) && Objects.equals(attempt, packet.attempt) && Objects.equals(note, packet.note) && Objects.equals(productCount, packet.productCount);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, customerName, customerPhoneNb, city, address, packetDescription, barcode, lastDeliveryStatus, oldClient, productsPackets, packetStatus, fbPage, deliveryCompany, price, deliveryPrice, discount, date, status, lastUpdateDate, exchange, printLink, exchangeId, valid, attempt, note, productCount, haveExchange);
    }
}
