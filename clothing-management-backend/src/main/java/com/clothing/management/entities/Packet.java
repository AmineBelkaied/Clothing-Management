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
    private String governorate;
    private String address;
    private String relatedProducts;
    private String packetReference;
    @OneToMany(mappedBy = "packet" , cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private Set<ProductsPacket> products;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "fbpage_id")
    private FbPage fbPage;
    private Integer price;
    private boolean confirmation;
    private String status;
    private Date date;

    public Packet() {
    }

    public Packet(String customerName, String customerPhoneNb, String governorate, String address, String relatedProducts, String packetReference, Set<ProductsPacket> products, FbPage fbPage, Integer price, boolean confirmation, String status, Date date) {
        this.customerName = customerName;
        this.customerPhoneNb = customerPhoneNb;
        this.governorate = governorate;
        this.address = address;
        this.relatedProducts = relatedProducts;
        this.packetReference = packetReference;
        this.products = products;
        this.fbPage = fbPage;
        this.price = price;
        this.confirmation = confirmation;
        this.status = status;
        this.date = date;
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

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

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

    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }

    public boolean isConfirmation() {
        return confirmation;
    }

    public void setConfirmation(boolean confirmation) {
        this.confirmation = confirmation;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getGovernorate() {
        return governorate;
    }

    public void setGovernorate(String governorate) {
        this.governorate = governorate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Packet{" +
                "id=" + id +
                ", customerName='" + customerName + '\'' +
                ", customerPhoneNb='" + customerPhoneNb + '\'' +
                ", governorate='" + governorate + '\'' +
                ", address='" + address + '\'' +
                ", relatedProducts='" + relatedProducts + '\'' +
                ", packetReference='" + packetReference + '\'' +
                ", products=" + products +
                ", fbPage=" + fbPage +
                ", price=" + price +
                ", confirmation=" + confirmation +
                ", status='" + status + '\'' +
                ", date=" + date +
                '}';
    }
}
