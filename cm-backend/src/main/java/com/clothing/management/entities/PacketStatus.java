package com.clothing.management.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;
import java.util.Date;
import java.util.Set;

@Entity
@Table(name="packet_status", indexes = {
        @Index(name = "idx_id", columnList = "id"),
        @Index(name = "idx_packet_id", columnList = "packet_id")
})
public class PacketStatus {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Date date;
    private String status;
    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "packet_id")
    private Packet packet;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
    public PacketStatus() {
        this.id = null;
        this.date = new Date();
    }

    public PacketStatus(Long id, Date date, String status, Packet packet, User user) {
        this.id = id;
        this.date = date;
        this.status = status;
        this.packet = packet;
        this.user = user;
    }
    public PacketStatus(String status, Packet packet, User user) {
        this.id = null;
        this.date = new Date();
        this.status = status;
        this.packet = packet;
        this.user = user;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Packet getPacket() {
        return packet;
    }

    public void setPacket(Packet packet) {
        this.packet = packet;
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

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public String toString() {
        return "Packet{" +
                "id=" + id +
                ", date=" + date +
                ", status='" + status + '\'' +
                '}';
    }

}
