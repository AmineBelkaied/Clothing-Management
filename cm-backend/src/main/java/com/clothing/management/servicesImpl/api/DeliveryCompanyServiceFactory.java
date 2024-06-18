package com.clothing.management.servicesImpl.api;

import com.clothing.management.enums.DeliveryCompanyName;
import org.springframework.stereotype.Service;

@Service
public class DeliveryCompanyServiceFactory {

    private final NavexApiService navexApiService;
    private final FirstApiService firstApiService;

    public DeliveryCompanyServiceFactory(NavexApiService navexApiService,
                                         FirstApiService firstApiService) {
        this.navexApiService = navexApiService;
        this.firstApiService = firstApiService;
    }

    public DeliveryCompanyService getDeliveryCompanyService(String deliveryCompanyName) {
        DeliveryCompanyName company = DeliveryCompanyName.fromString(deliveryCompanyName);

        return switch (company) {
            case FIRST -> firstApiService;
            case NAVEX -> navexApiService;
            default -> throw new IllegalArgumentException("Invalid deliveryCompany name");
        };

    }
}
