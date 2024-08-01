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
        Optional<GlobalConf> optionalGlobalConf = globalConfRepository.findAll().stream().findFirst();
        optionalGlobalConf.ifPresent(globalConf -> {;
            comment = globalConf.getComment();
            exchangeProduct = globalConf.getExchangeComment();
        });
    }

    protected String getPhoneNumber1(String telephoneNumber1) {
        String value = getValue(telephoneNumber1);
        if (!value.isEmpty() && value.contains("/")) {
            return value.substring(0, 8);
        }
        return value;
    }

    protected String getPhoneNumber2(String telephoneNumber2) {
        String value = getValue(telephoneNumber2);
        if (!value.isEmpty() && value.contains("/")) {
            return value.substring(value.indexOf('/') + 1).trim();
        }
        return "";
    }

    protected String getValue(String fieldName) {
        return Objects.requireNonNullElse(fieldName, "");
    }

    protected String getPacketDesignation(Packet packet) {
        StringBuilder designation = new StringBuilder();
        designation.append(getValue(packet.getId().toString()))
                .append(" ");

        if (packet.getFbPage() != null) {
            designation.append(getValue(packet.getFbPage().getName()));
        }

        designation.append(" | ")
                .append(getValue(packet.getPacketDescription().replace(REG, ", ")));

        return designation.toString();
    }
}
