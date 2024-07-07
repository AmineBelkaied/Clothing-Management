package com.clothing.management.servicesImpl.api;

import com.clothing.management.dto.DeliveryResponseNavex;
import com.clothing.management.entities.DeliveryCompany;
import com.clothing.management.entities.Packet;
import com.clothing.management.repository.IGlobalConfRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

@Service
public class NavexApiService extends DeliveryCompanyService {

    private final static String apiUrl = "https://app.navex.tn/api/";
    private final static String endUrl ="/v1/post.php";

    protected NavexApiService(IGlobalConfRepository globalConfRepository) {
        super(globalConfRepository);
    }

    public DeliveryResponseNavex createBarCode(Packet packet) throws IOException {
        String url = apiUrl + packet.getDeliveryCompany().getApiName() + "-" + packet.getDeliveryCompany().getToken() + endUrl;
        return executeHttpRequest(url, createRequestBody(packet));
    }

    public DeliveryResponseNavex getLastStatus(String barCode, DeliveryCompany deliveryCompany) throws IOException {
        StringBuilder body = new StringBuilder();
        body.append("code=").append(barCode);
        String url = apiUrl + deliveryCompany.getApiName() + "-etat-" + deliveryCompany.getToken() + endUrl;
        return executeHttpRequest(url, body.toString());
    }

    private DeliveryResponseNavex executeHttpRequest(String url, String body) throws IOException {
        setUpGlobalConfParams();
        // Create a new HTTP connection and set the request method to POST
        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
        connection.setRequestMethod(HttpMethod.POST.toString());

        // Enable output and send the request body
        connection.setDoOutput(true);
        try (OutputStream out = connection.getOutputStream()) {
            out.write(body.getBytes());
        }
        DeliveryResponseNavex deliveryResponse;
        int responseCode = connection.getResponseCode();
        String responseMessage = connection.getResponseMessage();
        try (BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
            String inputLine;
            StringBuilder response = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            deliveryResponse = getDeliveryResponseNavex(responseCode, responseMessage, response);
        }
        LOGGER.debug("deliveryResponse : {} ", deliveryResponse);
        // Disconnect the connection
        connection.disconnect();
        return deliveryResponse;
    }

    private static DeliveryResponseNavex getDeliveryResponseNavex(int responseCode, String responseMessage, StringBuilder response) throws JsonProcessingException {
        DeliveryResponseNavex deliveryResponse;
        ObjectMapper mapper = new ObjectMapper();
        deliveryResponse = mapper.readValue(response.toString(), DeliveryResponseNavex.class);
        deliveryResponse.setResponseCode(responseCode);
        deliveryResponse.setResponseMessage(responseMessage);
        deliveryResponse.setBarCode(deliveryResponse.getStatus_message());
        deliveryResponse.setState(deliveryResponse.getEtat());
        deliveryResponse.setLink(deliveryResponse.getLien());
        deliveryResponse.setMessage(deliveryResponse.getStatus_message());
        deliveryResponse.setStatus(responseCode);
        deliveryResponse.setError(false);
        return deliveryResponse;
    }

    private String createRequestBody(Packet packet) {

        String adresse = this.getValue(packet.getAddress().replaceAll(REGEX_NEWLINE," "));
        StringBuilder body = new StringBuilder();
        body.append("nom=")
                .append(this.getValue(packet.getCustomerName()))
                .append("&gouvernerat=")
                .append(packet.getCity().getGovernorate().getName())
                .append("&ville=")
                .append(packet.getCity().getName())
                .append("&adresse=")
                .append(adresse)
                .append("&tel=")
                .append(this.getPhoneNumber1(packet.getCustomerPhoneNb()))
                .append("&tel2=")
                .append(this.getPhoneNumber2(packet.getCustomerPhoneNb()))
                .append("&designation=")
                .append(this.getPacketDesignation(packet))
                .append("&msg=")
                .append(comment)
                .append("&echange=")
                .append(packet.getExchangeId() != null?"1":"0")
                .append("&ouvrir=Oui")
                .append("&article=")
                .append(exchangeProduct)
                .append("&nb_echange=")
                .append(packet.getExchangeId() != null?"1":"0")
                .append("&nb_article=")
                .append(1)
                .append("&prix=")
                .append(this.getPacketPrice(packet)+"");
        return body.toString();
    }

    @Override
    public Double getPacketPrice(Packet packet) {
        return packet.getPrice() + packet.getDeliveryPrice() - packet.getDiscount();
    }
}
