package com.clothing.management.servicesImpl.api;

import com.clothing.management.dto.DeliveryCompanyDTOs.DeliveryResponseNavex;
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
import java.nio.charset.StandardCharsets;
import java.net.URLEncoder;

@Service
public class NavexApiService extends DeliveryCompanyService {

    private static final String API_URL = "https://app.navex.tn/api/";
    private static final String END_URL = "/v1/post.php";

    protected NavexApiService(IGlobalConfRepository globalConfRepository) {
        super(globalConfRepository);
    }

    public DeliveryResponseNavex createBarCode(Packet packet) throws IOException {
        String url = API_URL + packet.getDeliveryCompany().getApiName() + "-" + packet.getDeliveryCompany().getToken() + END_URL;
        String requestBody = createRequestBody(packet);
        LOGGER.debug("Request URL: {}", url);
        LOGGER.debug("Request Body: {}", requestBody);
        return executeHttpRequest(url, requestBody);
    }

    public DeliveryResponseNavex getLastStatus(String barCode, DeliveryCompany deliveryCompany) throws IOException {
        StringBuilder body = new StringBuilder();
        body.append("code=").append(URLEncoder.encode(barCode, StandardCharsets.UTF_8));
        String url = API_URL + deliveryCompany.getApiName() + "-etat-" + deliveryCompany.getToken() + END_URL;
        return executeHttpRequest(url, body.toString());
    }

    private DeliveryResponseNavex executeHttpRequest(String url, String body) throws IOException {
        setUpGlobalConfParams();

        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
        connection.setRequestMethod(HttpMethod.POST.toString());
        connection.setDoOutput(true);
        connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        connection.setRequestProperty("User-Agent", "curl/7.29.0");

        try (OutputStream out = connection.getOutputStream()) {
            out.write(body.getBytes());
        }

        int responseCode = connection.getResponseCode();
        String responseMessage = connection.getResponseMessage();
        StringBuilder response = new StringBuilder();

        try (BufferedReader in = new BufferedReader(new InputStreamReader(
                responseCode >= 400 ? connection.getErrorStream() : connection.getInputStream()))) {
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
        } finally {
            connection.disconnect();
        }

        LOGGER.debug("Response Code: {}", responseCode);
        LOGGER.debug("Response Message: {}", responseMessage);
        LOGGER.debug("Response Body: {}", response.toString());

        if (responseCode >= 400) {
            LOGGER.error("HTTP Error {}: {}", responseCode, responseMessage);
            throw new IOException("Error response from API: " + responseMessage);
        }

        DeliveryResponseNavex deliveryResponse = getDeliveryResponseNavex(responseCode, responseMessage, response);
        LOGGER.debug("deliveryResponse : {} ", deliveryResponse);

        return deliveryResponse;
    }

    private static DeliveryResponseNavex getDeliveryResponseNavex(int responseCode, String responseMessage, StringBuilder response) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        DeliveryResponseNavex deliveryResponse = mapper.readValue(response.toString(), DeliveryResponseNavex.class);
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

    private String createRequestBody(Packet packet) throws IOException {
        String adresse = this.getValue(packet.getAddress().replaceAll(REGEX_NEWLINE, " "));
        return new StringBuilder()
                .append("nom=").append(URLEncoder.encode(this.getValue(packet.getCustomerName()), "UTF-8"))
                .append("&gouvernerat=").append(URLEncoder.encode(packet.getCity().getGovernorate().getName(), "UTF-8"))
                .append("&ville=").append(URLEncoder.encode(packet.getCity().getName(), "UTF-8"))
                .append("&adresse=").append(URLEncoder.encode(adresse, "UTF-8"))
                .append("&tel=").append(URLEncoder.encode(this.getPhoneNumber1(packet.getCustomerPhoneNb()), "UTF-8"))
                .append("&tel2=").append(this.getPhoneNumber2(packet.getCustomerPhoneNb()))
                .append("&designation=").append(this.getPacketDesignation(packet))
                .append("&msg=").append(comment)
                .append("&echange=").append(URLEncoder.encode(packet.getExchangeId() != null ? "1" : "0", "UTF-8"))
                .append("&ouvrir=").append(URLEncoder.encode("Oui", "UTF-8"))
                .append("&article=").append(URLEncoder.encode(exchangeProduct, "UTF-8"))
                .append("&nb_echange=").append(URLEncoder.encode(packet.getExchangeId() != null ? "1" : "0", "UTF-8"))
                .append("&nb_article=").append(URLEncoder.encode("1", "UTF-8"))
                .append("&prix=").append(URLEncoder.encode(this.getPacketPrice(packet).toString(), "UTF-8"))
                .toString();
    }

    @Override
    public Double getPacketPrice(Packet packet) {
        return packet.getPrice() + packet.getDeliveryPrice() - packet.getDiscount();
    }
}
