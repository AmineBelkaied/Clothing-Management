package com.clothing.management.entities;

import com.clothing.management.enums.ClientReason;
import com.clothing.management.enums.SystemStatus;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.Date;

@Entity
@Table(name = "note")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
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
}
