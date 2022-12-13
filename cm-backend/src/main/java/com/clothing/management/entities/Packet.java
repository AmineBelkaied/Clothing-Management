package com.clothing.management.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.util.Date;
import java.util.Set;

@Entity
public class Packet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String customerName;
    private String customerPhoneNb;
    @OneToOne
    @JoinColumn(name = "city_id")
    private City city;
    private String address;
    private String relatedProducts;
    private String packetDescription;
    private String packetReference;
    @OneToMany(mappedBy = "packet" , cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private Set<ProductsPacket> products;

    @ManyToOne
    @JoinColumn(name = "fbpage_id")
    private FbPage fbPage;
    private double price;
    private double deliveryPrice;
    private double discount;
    private Date date;
    private String status;

    private String barrecode;

    public Packet() {
    }

    public Packet(Long id, String customerName, String customerPhoneNb, City city, String address, String relatedProducts, String packetDescription, String packetReference, Set<ProductsPacket> products, FbPage fbPage, double price, double deliveryPrice, double discount, Date date, String status, String barrecode) {
        this.id = id;
        this.customerName = customerName;
        this.customerPhoneNb = customerPhoneNb;
        this.city = city;
        this.address = address;
        this.relatedProducts = relatedProducts;
        this.packetDescription = packetDescription;
        this.packetReference = packetReference;
        this.products = products;
        this.fbPage = fbPage;
        this.price = price;
        this.deliveryPrice = deliveryPrice;
        this.discount = discount;
        this.date = date;
        this.status = status;
        this.barrecode = barrecode;
    }

    public String getBarrecode() {
        return barrecode;
    }

    public void setBarrecode(String barrecode) {
        this.barrecode = barrecode;
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

    public Set<ProductsPacket> getProducts() {
        return products;
    }

    public void setProducts(Set<ProductsPacket> products) {
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

    @Override
    public String toString() {
        return "Packet{" +
                "id=" + id +
                ", customerName='" + customerName + '\'' +
                ", customerPhoneNb='" + customerPhoneNb + '\'' +
                ", city=" + city +
                ", address='" + address + '\'' +
                ", relatedProducts='" + relatedProducts + '\'' +
                ", packetDescription='" + packetDescription + '\'' +
                ", packetReference='" + packetReference + '\'' +
                ", products=" + products +
                ", fbPage=" + fbPage +
                ", price=" + price +
                ", deliveryPrice=" + deliveryPrice +
                ", discount=" + discount +
                ", date=" + date +
                ", status='" + status + '\'' +
                ", barrecode='" + barrecode + '\'' +
                '}';
    }
}
