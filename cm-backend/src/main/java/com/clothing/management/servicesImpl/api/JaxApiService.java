package com.clothing.management.servicesImpl.api;

import com.clothing.management.dto.DeliveryCompanyDTOs.DeliveryResponseJax;
import com.clothing.management.entities.DeliveryCompany;
import com.clothing.management.entities.Packet;
import com.clothing.management.repository.IGlobalConfRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;

import javax.net.ssl.HttpsURLConnection;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

@Service
public class JaxApiService extends DeliveryCompanyService {

    private final static String apiUrl = "https://core.jax-delivery.com/api/user/colis/";
    protected String comment = " يسمح بفتح الطرد عند طلب الحريف ";
    protected JaxApiService(IGlobalConfRepository globalConfRepository) {
        super(globalConfRepository);
    }

    @Override
    public DeliveryResponseJax createBarCode(Packet packet) throws IOException {
        String url = apiUrl + "add?token=" + packet.getDeliveryCompany().getToken();
        return executeHttpRequest(url, createRequestBody(packet), packet.getDeliveryCompany(), HttpMethod.POST);
    }

    @Override
    public DeliveryResponseJax getLastStatus(String barCode, DeliveryCompany deliveryCompany) throws IOException {
        String url = apiUrl + "getstatut_uptated/" + barCode + "?token=" + deliveryCompany.getToken();
        return executeHttpRequest(url, null, deliveryCompany, HttpMethod.GET);
    }

    /*private DeliveryResponseJax executeHttpRequest(String url, String body) throws IOException {
        setUpGlobalConfParams();
        // Create a new HTTP connection and set the request method to POST
        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
        connection.setRequestMethod(HttpMethod.POST.toString());

        // Enable output and send the request body
        connection.setDoOutput(true);
        try (OutputStream out = connection.getOutputStream()) {
            out.write(body.getBytes());
        }
        DeliveryResponseJax deliveryResponse;
        int responseCode = connection.getResponseCode();
        String responseMessage = connection.getResponseMessage();
        try (BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
            String inputLine;
            StringBuilder response = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            deliveryResponse = getDeliveryResponseJax(responseCode, responseMessage, response);
        }
        LOGGER.debug("deliveryResponse : {} ", deliveryResponse);
        // Disconnect the connection
        connection.disconnect();
        return deliveryResponse;
    }*/

    private DeliveryResponseJax executeHttpRequest(String url, String jsonBody, DeliveryCompany deliveryCompany, HttpMethod method) throws IOException {
        HttpsURLConnection connection = getHttpsURLConnection(url, deliveryCompany, method);
        DeliveryResponseJax deliveryResponse = new DeliveryResponseJax();
        StringBuilder response = new StringBuilder();
        if (method == HttpMethod.POST) {
            connection.setDoOutput(true);
            try (OutputStream outputStream = connection.getOutputStream()) {
                byte[] input = jsonBody.getBytes(StandardCharsets.UTF_8);
                outputStream.write(input, 0, input.length);
            } catch (Exception e) {
                LOGGER.error("Error in writing to OutputStream : ", e);
            }
        }

//first
        int responseCode = connection.getResponseCode();
        String responseMessage = connection.getResponseMessage();
        if (responseCode != HttpURLConnection.HTTP_NOT_FOUND) {
            deliveryResponse = getDeliveryResponseJaxSuccess(connection, response, responseCode, responseMessage);
        } else {
            getDeliveryResponseJaxError(connection, response, deliveryResponse, responseCode);
        }
        //first end

        LOGGER.debug("deliveryResponse : {} ", deliveryResponse);
        // Disconnect the connection
        connection.disconnect();
        return deliveryResponse;
    }

    private static void getDeliveryResponseJaxError(HttpsURLConnection connection, StringBuilder response, DeliveryResponseJax deliveryResponse, int responseCode) {
        InputStream errorStream = connection.getErrorStream();
        if (errorStream != null) {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
            } catch (Exception e) {
                LOGGER.error("Error in reading ErrorStream : ",  e);
            }
        } else {
            LOGGER.error("Error stream is null.");
        }
        deliveryResponse.setResponseMessage("not found");
        deliveryResponse.setStatus(responseCode);
        deliveryResponse.setResponseCode(responseCode);
    }

    private static DeliveryResponseJax getDeliveryResponseJaxSuccess(HttpsURLConnection connection, StringBuilder response, int responseCode, String responseMessage) throws JsonProcessingException {

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                LOGGER.info("line-: {}", line);
                response.append(line);
            }
        } catch (Exception e) {
            LOGGER.error("Error in reading InputStream : ", e);
        }

        return getDeliveryResponseJax(responseCode, responseMessage, response);
    }


    private static DeliveryResponseJax getDeliveryResponseJax(int responseCode, String responseMessage, StringBuilder response) throws JsonProcessingException {
        DeliveryResponseJax deliveryResponse;
        ObjectMapper mapper = new ObjectMapper();
        deliveryResponse = mapper.readValue(response.toString(), DeliveryResponseJax.class);
        deliveryResponse.setResponseCode(responseCode);
        deliveryResponse.setResponseMessage(responseMessage);
        deliveryResponse.setBarCode(deliveryResponse.getBarCode());
        deliveryResponse.setIsError(false);
        deliveryResponse.setState(deliveryResponse.getState());
        deliveryResponse.setStatus(responseCode);
        return deliveryResponse;
    }


    private static HttpsURLConnection getHttpsURLConnection(String url, DeliveryCompany deliveryCompany, HttpMethod method) throws IOException {
        URL urlConnection = new URL(url);
        HttpsURLConnection connection = (HttpsURLConnection) urlConnection.openConnection();
        connection.setRequestMethod(method.name());
        connection.setRequestProperty(HttpHeaders.AUTHORIZATION, "Bearer " + deliveryCompany.getToken());
        connection.setRequestProperty(HttpHeaders.CONTENT_TYPE, "application/json; charset=utf-8");
        connection.setRequestProperty(HttpHeaders.USER_AGENT, "curl/7.29.0");
        connection.setDoOutput(method == HttpMethod.POST);
        return connection;
    }

    private String createRequestBody(Packet packet) {
        String adresse = this.getValue(packet.getAddress().replaceAll(REGEX_NEWLINE," "));
        LOGGER.info(adresse);

        return new StringBuilder()
                .append("{")
                .append("\"referenceExterne\": \"").append("\",")
                .append("\"nomContact\": \"").append(this.getValue(packet.getCustomerName())).append("\",")
                .append("\"tel\": \"").append(this.getPhoneNumber1(packet.getCustomerPhoneNb())).append("\",")
                .append("\"tel2\": \"").append(this.getPhoneNumber2(packet.getCustomerPhoneNb())).append("\",")
                .append("\"adresseLivraison\": \"").append(adresse).append("\",")
                .append("\"governorat\": ").append(packet.getCity().getGovernorate().getJaxCode()).append(",")
                .append("\"delegation\": \"").append(packet.getCity().getName()).append("\",")
                .append("\"description\": \"").append(this.getPacketDesignation(packet)).append(comment).append("\",")
                .append("\"cod\": ").append(this.getPacketPrice(packet)).append(",")
                .append("\"echange\": ").append(packet.getExchangeId() != null ? "1" : "0")
                .append("}")
                .toString();
    }

    @Override
    public Double getPacketPrice(Packet packet) {
        Double price = packet.getPrice() + packet.getDeliveryPrice() - packet.getDiscount();
        return (price == 0.0) ? 0.1 : price;
    }
}
