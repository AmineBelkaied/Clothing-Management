package com.clothing.management.servicesImpl.api;

import com.clothing.management.dto.DeliveryCompanyDTOs.DeliveryResponseFirst;
import com.clothing.management.entities.DeliveryCompany;
import com.clothing.management.entities.Packet;
import com.clothing.management.repository.IGlobalConfRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.minidev.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

@Service
public class FirstApiService extends DeliveryCompanyService {

    private static final Logger LOGGER = LoggerFactory.getLogger(FirstApiService.class);  // Added logger

    private final static String createBarCodeEndPoint = "https://www.firstdeliverygroup.com/api/v2/create";
    private final static String getLastStatusEndPoint = "https://www.firstdeliverygroup.com/api/v2/etat";

    protected FirstApiService(IGlobalConfRepository globalConfRepository) {
        super(globalConfRepository);
    }

    @Override
    public DeliveryResponseFirst createBarCode(Packet packet) throws IOException {
        LOGGER.info("Creating barcode for packet with ID: {}", packet.getId());  // Log packet ID
        String jsonBody = createJsonPacketForFirst(packet).toString();
        return executeHttpRequest(createBarCodeEndPoint, jsonBody, packet.getDeliveryCompany());
    }

    @Override
    public DeliveryResponseFirst getLastStatus(String barCode, DeliveryCompany deliveryCompany) throws IOException {
        LOGGER.info("Fetching last status for barcode: {}", barCode);  // Log barcode
        JSONObject jsonBody = createJsonBarCode(barCode);
        return executeHttpRequest(getLastStatusEndPoint, jsonBody.toString(), deliveryCompany);
    }

    private DeliveryResponseFirst executeHttpRequest(String url, String jsonBody, DeliveryCompany deliveryCompany) throws IOException {
        LOGGER.debug("Executing HTTP request to URL: {} with body: {}", url, jsonBody);  // Log URL and body
        HttpsURLConnection connection = getHttpsURLConnection(url, deliveryCompany);

        StringBuilder response = new StringBuilder();
        DeliveryResponseFirst deliveryResponse = new DeliveryResponseFirst();

        try (OutputStream outputStream = connection.getOutputStream()) {
            byte[] input = jsonBody.getBytes(StandardCharsets.UTF_8);
            outputStream.write(input, 0, input.length);
        } catch (Exception e) {
            LOGGER.error("Error in writing to OutputStream: ", e);  // Log error
        }

        int responseCode = connection.getResponseCode();
        String responseMessage = connection.getResponseMessage();
        LOGGER.info("Response received. Code: {}, Message: {}", responseCode, responseMessage);  // Log response code and message

        if (responseCode != HttpURLConnection.HTTP_NOT_FOUND) {
            deliveryResponse = getDeliveryResponseFirstSuccess(connection, response, responseCode, responseMessage);
        } else {
            getDeliveryResponseFirstError(connection, response, deliveryResponse, responseCode);
        }
        connection.disconnect();
        LOGGER.debug("Connection closed.");
        return deliveryResponse;
    }

    private static void getDeliveryResponseFirstError(HttpsURLConnection connection, StringBuilder response, DeliveryResponseFirst deliveryResponse, int responseCode) {
        LOGGER.warn("Received error response with code: {}", responseCode);  // Log warning for error response
        InputStream errorStream = connection.getErrorStream();
        if (errorStream != null) {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(errorStream))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
            } catch (Exception e) {
                LOGGER.error("Error reading ErrorStream: ", e);  // Log error
            }
        } else {
            LOGGER.error("Error stream is null.");
        }
        deliveryResponse.setResponseMessage("not found");
        deliveryResponse.setStatus(responseCode);
        deliveryResponse.setResponseCode(responseCode);
        deliveryResponse.setIsError(true);
    }

    private static DeliveryResponseFirst getDeliveryResponseFirstSuccess(HttpsURLConnection connection, StringBuilder response, int responseCode, String responseMessage) throws JsonProcessingException {
        LOGGER.info("Processing successful response with code: {}", responseCode);  // Log successful response
        DeliveryResponseFirst deliveryResponse;
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                LOGGER.debug("Response line: {}", line);  // Log each line of the response
                response.append(line);
            }
        } catch (Exception e) {
            LOGGER.error("Error reading InputStream: ", e);  // Log error
        }
        ObjectMapper mapper = new ObjectMapper();
        deliveryResponse = mapper.readValue(response.toString(), DeliveryResponseFirst.class);
        deliveryResponse.setStatus(deliveryResponse.getStatus());
        deliveryResponse.setLink(deliveryResponse.getResult().getLink());
        deliveryResponse.setState(deliveryResponse.getResult().getState());
        deliveryResponse.setBarCode(deliveryResponse.getResult().getBarCode());
        deliveryResponse.setIsError(deliveryResponse.isError());
        deliveryResponse.setMessage(deliveryResponse.getMessage());
        deliveryResponse.setResponseCode(responseCode);
        deliveryResponse.setResponseMessage(responseMessage);
        return deliveryResponse;
    }

    private static HttpsURLConnection getHttpsURLConnection(String url, DeliveryCompany deliveryCompany) throws IOException {
        LOGGER.debug("Setting up HTTPS connection to URL: {}", url);  // Log URL for connection setup
        URL urlConnection = new URL(url);
        HttpsURLConnection connection = (HttpsURLConnection) urlConnection.openConnection();
        connection.setRequestMethod(HttpMethod.POST.name());
        connection.setRequestProperty(HttpHeaders.AUTHORIZATION, "Bearer " + deliveryCompany.getToken());
        connection.setRequestProperty(HttpHeaders.CONTENT_TYPE, "application/json; charset=utf-8");
        connection.setRequestProperty(HttpHeaders.USER_AGENT, "curl/7.29.0");
        connection.setDoOutput(true);
        return connection;
    }

    private JSONObject createJsonBarCode(String barCode) {
        LOGGER.debug("Creating JSON object for barcode: {}", barCode);  // Log barcode
        JSONObject json = new JSONObject();
        json.put("barCode", barCode);
        return json;
    }

    private JSONObject createJsonPacketForFirst(Packet packet) {
        LOGGER.debug("Creating JSON packet for Packet ID: {}", packet.getId());  // Log packet ID
        setUpGlobalConfParams();
        JSONObject json = new JSONObject();
        json.put("Client", createClientJson(packet));
        json.put("Produit", createProductJson(packet));

        return json;
    }

    private JSONObject createClientJson(Packet packet) {
        String adresse = this.getValue(packet.getAddress().replaceAll(REGEX_NEWLINE, " "));
        LOGGER.debug("Creating client JSON for Packet ID: {}", packet.getId());  // Log packet ID
        JSONObject client = new JSONObject();
        client.put("nom", getValue(packet.getCustomerName()));
        client.put("gouvernerat", packet.getCity().getGovernorate().getName());
        client.put("ville", packet.getCity().getName());
        client.put("adresse", adresse);
        client.put("telephone", getPhoneNumber1(packet.getCustomerPhoneNb()));
        client.put("telephone2", getPhoneNumber2(packet.getCustomerPhoneNb()));
        return client;
    }

    private JSONObject createProductJson(Packet packet) {
        LOGGER.debug("Creating product JSON for Packet ID: {}", packet.getId());  // Log packet ID
        JSONObject product = new JSONObject();
        product.put("prix", getPacketPrice(packet));
        product.put("designation", getPacketDesignation(packet));
        product.put("nombreArticle", 1);
        product.put("commentaire", comment);
        product.put("echange", packet.getExchangeId() != null ? "oui" : "non");
        product.put("article", exchangeProduct);
        product.put("nombreEchange", packet.getExchangeId() != null ? 1 : 0);
        return product;
    }

    @Override
    public Double getPacketPrice(Packet packet) {
        Double price = packet.getPrice() + packet.getDeliveryPrice() - packet.getDiscount();
        if (price == 0.0) price = 0.1;
        LOGGER.debug("Calculated price for Packet ID: {} is {}", packet.getId(), price);  // Log packet ID and price
        return price;
    }
}
