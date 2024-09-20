package com.clothing.management.servicesImpl.api;

import com.clothing.management.dto.DeliveryCompanyDTOs.DeliveryResponse;
import com.clothing.management.entities.DeliveryCompany;
import com.clothing.management.entities.GlobalConf;
import com.clothing.management.entities.Packet;
import com.clothing.management.repository.IGlobalConfRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Objects;
import java.util.Optional;

@Service
public abstract class DeliveryCompanyService {

    protected final IGlobalConfRepository globalConfRepository;
    protected static final Logger LOGGER = LoggerFactory.getLogger(DeliveryCompanyService.class);
    protected static final String REG = "/,/gi";
    protected static final String REG_BS = "/\\n/gi";
    protected static final String REGEX_NEWLINE = "\\r?\\n";

    protected String exchangeProduct = "Diggie pants";
    protected String comment = "يسمح بفتح الطرد عند طلب الحريف";

    protected DeliveryCompanyService(IGlobalConfRepository globalConfRepository) {
        this.globalConfRepository = globalConfRepository;
    }

    public abstract DeliveryResponse createBarCode(Packet packet) throws IOException;

    public abstract DeliveryResponse getLastStatus(String barCode, DeliveryCompany deliveryCompany) throws IOException;

    public abstract Double getPacketPrice(Packet packet);

    protected void setUpGlobalConfParams() {
        LOGGER.info("Setting up global configuration parameters...");
        Optional<GlobalConf> optionalGlobalConf = globalConfRepository.findAll().stream().findFirst();
        if (optionalGlobalConf.isPresent()) {
            GlobalConf globalConf = optionalGlobalConf.get();
            LOGGER.info("Global configuration found: {}", globalConf);
            comment = globalConf.getComment();
            exchangeProduct = globalConf.getExchangeComment();
        } else {
            LOGGER.warn("No global configuration found, using default values.");
        }
    }

    protected String getPhoneNumber1(String telephoneNumber1) {
        LOGGER.debug("Extracting phone number 1 from: {}", telephoneNumber1);
        String value = getValue(telephoneNumber1);
        if (!value.isEmpty() && value.contains("/")) {
            String phone1 = value.substring(0, 8);
            LOGGER.debug("Phone number 1 extracted: {}", phone1);
            return phone1;
        }
        LOGGER.debug("Phone number 1: {}", value);
        return value;
    }

    protected String getPhoneNumber2(String telephoneNumber2) {
        LOGGER.debug("Extracting phone number 2 from: {}", telephoneNumber2);
        String value = getValue(telephoneNumber2);
        if (!value.isEmpty() && value.contains("/")) {
            String phone2 = value.substring(value.indexOf('/') + 1).trim();
            LOGGER.debug("Phone number 2 extracted: {}", phone2);
            return phone2;
        }
        LOGGER.debug("Phone number 2 is empty or not present.");
        return "";
    }

    protected String getValue(String fieldName) {
        String value = Objects.requireNonNullElse(fieldName, "");
        LOGGER.debug("Field value retrieved: {}", value);
        return value;
    }

    protected String getPacketDesignation(Packet packet) {
        LOGGER.debug("Creating packet designation for packet ID: {}", packet.getId());
        StringBuilder designation = new StringBuilder();
        designation.append(getValue(packet.getId().toString()))
                .append(" ");

        if (packet.getFbPage() != null) {
            designation.append(getValue(packet.getFbPage().getName()));
        }

        designation.append(" | ")
                .append(getValue(packet.getPacketDescription().replace(REG, ", ")));

        String packetDesignation = designation.toString();
        LOGGER.debug("Packet designation created: {}", packetDesignation);
        return packetDesignation;
    }
}
