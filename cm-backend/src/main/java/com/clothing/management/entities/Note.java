package com.clothing.management.entities;

import com.clothing.management.enums.ClientReason;
import com.clothing.management.enums.SystemStatus;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.util.Date;

@Entity
@Table(name = "note")
public class Note {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Date date;

    @Enumerated(EnumType.STRING)
    @Column(name = "client_reason")
    private ClientReason clientReason;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private SystemStatus status;

    private String explanation;

    @ManyToOne
    @JoinColumn(name = "packet_id")
    @JsonIgnore
    private Packet packet;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    public Note() {
    }

    public Note(Long id, Date date, ClientReason clientReason, SystemStatus status, String explanation, Packet packet, User user) {
        this.id = id;
        this.date = date;
        this.clientReason = clientReason;
        this.status = status;
        this.explanation = explanation;
        this.packet = packet;
        this.user = user;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public ClientReason getClientReason() {
        return clientReason;
    }

    public void setClientReason(ClientReason clientReason) {
        this.clientReason = clientReason;
    }

    public String getExplanation() {
        return explanation;
    }

    public void setExplanation(String explanation) {
        this.explanation = explanation;
    }

    public SystemStatus getStatus() {
        return status;
    }

    public void setStatus(SystemStatus status) {
        this.status = status;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Packet getPacket() {
        return packet;
    }

    public void setPacket(Packet packet) {
        this.packet = packet;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public String toString() {
        return "Note{" +
                "id=" + id +
                ", date=" + date +
                ", clientReason=" + clientReason +
                ", status=" + status +
                ", explanation='" + explanation + '\'' +
                ", packet=" + packet +
                ", user=" + user +
                '}';
    }
}
