package com.clothing.management.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.Set;

@Entity
@Table(name="packet_status", indexes = {
        @Index(name = "idx_id", columnList = "id"),
        @Index(name = "idx_packet_id", columnList = "packet_id")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
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
}
