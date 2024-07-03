package com.clothing.management.servicesImpl.api;

import com.clothing.management.enums.DeliveryCompanyName;
import org.springframework.stereotype.Service;

@Service
public class DeliveryCompanyServiceFactory {

    private final NavexApiService navexApiService;
    private final FirstApiService firstApiService;

    private final JaxApiService jaxApiService;

    public DeliveryCompanyServiceFactory(NavexApiService navexApiService,
                                         FirstApiService firstApiService,
                                         JaxApiService jaxApiService) {
        this.navexApiService = navexApiService;
        this.firstApiService = firstApiService;
        this.jaxApiService = jaxApiService;
    }

    public DeliveryCompanyService getDeliveryCompanyService(String deliveryCompanyName) {
        DeliveryCompanyName company = DeliveryCompanyName.fromString(deliveryCompanyName);

        return switch (company) {
            case FIRST -> firstApiService;
            case NAVEX -> navexApiService;
            case JAX -> jaxApiService;
            default -> throw new IllegalArgumentException("Invalid deliveryCompany name");
        };

    }
}
