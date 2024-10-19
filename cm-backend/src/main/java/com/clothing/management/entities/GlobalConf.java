package com.clothing.management.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.boot.context.properties.bind.DefaultValue;

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

    @OneToOne
    @JoinColumn(name = "fb_Page_id")
    private FbPage fbPage;

    @Builder.Default
    @Column(name = "one_source_app")
    private Boolean oneSourceApp = false;

    private String comment;

    @Column(name = "exchange_comment")
    private String exchangeComment;

    @Column(name = "cron_expression")
    private String cronExpression;
}
