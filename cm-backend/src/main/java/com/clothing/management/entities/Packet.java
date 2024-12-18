package com.clothing.management.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.clothing.management.enums.SystemStatus.NOT_CONFIRMED;

@Entity
@Table(name = "packet", indexes = {
        @Index(name = "idx_customer_phone_nb", columnList = "customer_phone_nb"),
        @Index(name = "idx_barcode", columnList = "barcode"),
})
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
@ToString(exclude = {"city","deliveryCompany","fbPage","productsPackets","packetStatus","notes"})
public class Packet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "customer_name")
    private String customerName;

    @Column(name = "customer_phone_nb")
    private String customerPhoneNb;

    @JsonIgnore
    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "city_id")
    private City city;

    private String address;

    @Column(name = "packet_description", length = 511)
    private String packetDescription;

    private String barcode;

    @Column(name = "last_delivery_status")
    private String lastDeliveryStatus;

    @Builder.Default
    @Column(name = "old_client")
    private Integer oldClient = 0;

    @JsonManagedReference(value = "packet-productsPacket")
    @OneToMany(mappedBy = "packet", cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    @Builder.Default
    private List<ProductsPacket> productsPackets = new ArrayList<>();

    @JsonIgnore
    @OneToMany(mappedBy = "packet", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<PacketStatus> packetStatus = new ArrayList<>();

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fbpage_id")
    private FbPage fbPage;

    @JsonManagedReference
    @ManyToOne
    @JoinColumn(name = "delivery_company")
    private DeliveryCompany deliveryCompany;

    @Builder.Default
    private double price = 0;

    @Column(name = "delivery_price")
    @Builder.Default
    private double deliveryPrice = 0;

    @Builder.Default
    private double discount = 0;

    @Builder.Default
    private Date date = new Date();

    @Builder.Default
    private String status = NOT_CONFIRMED.name();

    @Column(name = "last_update_date")
    private Date lastUpdateDate;

    private boolean exchange;

    @Column(name = "print_link")
    private String printLink;

    @Column(name = "exchange_id")
    private Long exchangeId;

    @Builder.Default
    private boolean valid = false;

    @OneToMany(mappedBy = "packet", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private List<Note> notes;

    @Column(name = "have_exchange")
    @Builder.Default
    private boolean haveExchange = false;

    @Column(name = "changed_price")
    @Builder.Default
    private boolean changedPrice = false;





}
