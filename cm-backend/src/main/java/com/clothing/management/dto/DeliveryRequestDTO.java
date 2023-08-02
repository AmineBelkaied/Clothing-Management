package com.clothing.management.dto;

public class DeliveryRequestDTO {

    String receiverName;
    String governorate;
    String city;
    String address;
    String telephone;
    String telephone2;
    String designation;
    Long packetCount;
    Double price;

    public DeliveryRequestDTO() {
    }

    public DeliveryRequestDTO(String receiverName, String governorate, String city, String address, String telephone, String telephone2, String designation, Long packetCount, Double price) {
        this.receiverName = receiverName;
        this.governorate = governorate;
        this.city = city;
        this.address = address;
        this.telephone = telephone;
        this.telephone2 = telephone2;
        this.designation = designation;
        this.packetCount = packetCount;
        this.price = price;
    }

    public String getReceiverName() {
        return receiverName;
    }

    public void setReceiverName(String receiverName) {
        this.receiverName = receiverName;
    }

    public String getGovernorate() {
        return governorate;
    }

    public void setGovernorate(String governorate) {
        this.governorate = governorate;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getTelephone2() {
        return telephone2;
    }

    public void setTelephone2(String telephone2) {
        this.telephone2 = telephone2;
    }

    public String getDesignation() {
        return designation;
    }

    public void setDesignation(String designation) {
        this.designation = designation;
    }

    public Long getPacketCount() {
        return packetCount;
    }

    public void setPacketCount(Long packetCount) {
        this.packetCount = packetCount;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    @Override
    public String toString() {
        return "DeliveryRequestDTO{" +
                "receiverName='" + receiverName + '\'' +
                ", governorate='" + governorate + '\'' +
                ", city='" + city + '\'' +
                ", address='" + address + '\'' +
                ", telephone='" + telephone + '\'' +
                ", telephone2='" + telephone2 + '\'' +
                ", designation='" + designation + '\'' +
                ", packetCount=" + packetCount +
                ", price=" + price +
                '}';
    }
}
