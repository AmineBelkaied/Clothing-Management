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
    private String packetReference;
    @OneToMany(mappedBy = "packet" , cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private Set<ProductsPacket> products;

    @ManyToOne
    @JoinColumn(name = "fbpage_id")
    private FbPage fbPage;
    private Integer price;
    private Date date;
    private String status;

    public Packet() {
    }

    public Packet(String customerName, String customerPhoneNb, City city, String address, String relatedProducts, String packetReference, Set<ProductsPacket> products, FbPage fbPage, Integer price, boolean confirmation, String status, Date date) {
        this.customerName = customerName;
        this.customerPhoneNb = customerPhoneNb;
        this.city = city;
        this.address = address;
        this.relatedProducts = relatedProducts;
        this.packetReference = packetReference;
        this.products = products;
        this.fbPage = fbPage;
        this.price = price;
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

    public City getCity() { return city; }

    public void setCity(City city) { this.city = city; }

    public String getAddress() { return address; }

    public void setAddress(String fullAddress) { this.address = address; }

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

    @Override
    public String toString() {
        return "Packet{" +
                "id=" + id +
                ", customerName='" + customerName + '\'' +
                ", customerPhoneNb='" + customerPhoneNb + '\'' +
                ", address='" + address + '\'' +
                ", city='" + city + '\'' +
                ", relatedProducts='" + relatedProducts + '\'' +
                ", packetReference='" + packetReference + '\'' +
                ", products=" + products +
                ", fbPage=" + fbPage +
                ", price=" + price +
                ", status='" + status + '\'' +
                ", date=" + date +
                '}';
    }
}
