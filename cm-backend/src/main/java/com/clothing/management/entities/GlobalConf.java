package com.clothing.management.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;

@Entity
@Table(name = "global_conf")
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
public class GlobalConf{

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

    public GlobalConf() {}

    public GlobalConf(String applicationName, DeliveryCompany deliveryCompany, String comment, String exchangeComment, String cronExpression) {
        this.applicationName = applicationName;
        this.deliveryCompany = deliveryCompany;
        this.comment = comment;
        this.exchangeComment = exchangeComment;
        this.cronExpression = cronExpression;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getApplicationName() {
        return applicationName;
    }

    public void setApplicationName(String applicationName) {
        this.applicationName = applicationName;
    }

    public DeliveryCompany getDeliveryCompany() {
        return deliveryCompany;
    }

    public void setDeliveryCompany(DeliveryCompany deliveryCompany) {
        this.deliveryCompany = deliveryCompany;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getExchangeComment() {
        return exchangeComment;
    }

    public void setExchangeComment(String exchangeComment) {
        this.exchangeComment = exchangeComment;
    }

    public String getCronExpression() {
        return cronExpression;
    }

    public void setCronExpression(String cronExpression) {
        this.cronExpression = cronExpression;
    }

    @Override
    public String toString() {
        return "GlobalConf{" +
                "id=" + id +
                ", applicationName='" + applicationName + '\'' +
                ", deliveryCompany=" + deliveryCompany +
                ", comment='" + comment + '\'' +
                ", exchangeComment='" + exchangeComment + '\'' +
                ", cronExpression='" + cronExpression + '\'' +
                '}';
    }
}
