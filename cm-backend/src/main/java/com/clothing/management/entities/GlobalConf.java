package com.clothing.management.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "global_conf")
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GlobalConf {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "application_name")
    private String applicationName;

    @OneToOne
    @JoinColumn(name = "delivery_company_id")
    private DeliveryCompany deliveryCompany;

    private String comment;

    @Column(name = "exchange_comment")
    private String exchangeComment;

    @Column(name = "cron_expression")
    private String cronExpression;
}
