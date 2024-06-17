package com.clothing.management.servicesImpl.api;

import com.clothing.management.dto.DeliveryResponseFirst;
import com.clothing.management.entities.DeliveryCompany;
import com.clothing.management.entities.Packet;
import com.clothing.management.repository.IGlobalConfRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.minidev.json.JSONObject;
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

    private final static String createBarCodeEndPoint = "https://www.firstdeliverygroup.com/api/v2/create";
    private final static String getLastStatusEndPoint = "https://www.firstdeliverygroup.com/api/v2/etat";

    protected FirstApiService(IGlobalConfRepository globalConfRepository) {
        super(globalConfRepository);
    }

    @Override
    public DeliveryResponseFirst createBarCode(Packet packet) throws IOException {
        String jsonBody = createJsonPacketForFirst(packet).toString();
        return executeHttpRequest(createBarCodeEndPoint, jsonBody,packet.getDeliveryCompany());
    }

    @Override
    public DeliveryResponseFirst getLastStatus(String barCode, DeliveryCompany deliveryCompany) throws IOException {
        JSONObject jsonBody = createJsonBarCode(barCode);
        return executeHttpRequest(getLastStatusEndPoint, jsonBody.toString(),deliveryCompany);
    }

    private DeliveryResponseFirst executeHttpRequest(String url, String jsonBody,DeliveryCompany deliveryCompany) throws IOException {
        HttpsURLConnection connection = getHttpsURLConnection(url, deliveryCompany);

        StringBuilder response = new StringBuilder();
        DeliveryResponseFirst deliveryResponse = new DeliveryResponseFirst();

        try (OutputStream outputStream = connection.getOutputStream()) {
            byte[] input = jsonBody.getBytes(StandardCharsets.UTF_8);
            outputStream.write(input, 0, input.length);
        } catch (Exception e) {
            LOGGER.error("Error in writing to OutputStream : ", e);
        }

        int responseCode = connection.getResponseCode();
        String responseMessage = connection.getResponseMessage();
        if (responseCode != HttpURLConnection.HTTP_NOT_FOUND) {
            deliveryResponse = getDeliveryResponseFirstSuccess(connection, response, responseCode, responseMessage);
        } else {
            getDeliveryResponseFirstError(connection, response, deliveryResponse, responseCode);
        }
        connection.disconnect();
        return deliveryResponse;
    }

    private static void getDeliveryResponseFirstError(HttpsURLConnection connection, StringBuilder response, DeliveryResponseFirst deliveryResponse, int responseCode) {
        InputStream errorStream = connection.getErrorStream();
        if (errorStream != null) {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(errorStream))) {
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
        deliveryResponse.setIsError(true);
    }

    private static DeliveryResponseFirst getDeliveryResponseFirstSuccess(HttpsURLConnection connection, StringBuilder response, int responseCode, String responseMessage) throws JsonProcessingException {
        DeliveryResponseFirst deliveryResponse;
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                LOGGER.info("line: {}", line);
                response.append(line);
            }
        } catch (Exception e) {
            LOGGER.error("Error in reading InputStream : ", e);
        }
        ObjectMapper mapper = new ObjectMapper();
        deliveryResponse = mapper.readValue(response.toString(), DeliveryResponseFirst.class);
        deliveryResponse.setResponseCode(responseCode);
        deliveryResponse.setResponseMessage(responseMessage);
        return deliveryResponse;
    }

    private static HttpsURLConnection getHttpsURLConnection(String url, DeliveryCompany deliveryCompany) throws IOException {
        URL urlConnection = new URL(url);
        HttpsURLConnection connection = (HttpsURLConnection) urlConnection.openConnection();
        connection.setRequestMethod(HttpMethod.POST.name());
        connection.setRequestProperty(HttpHeaders.AUTHORIZATION,"Bearer " + deliveryCompany.getToken());
        connection.setRequestProperty(HttpHeaders.CONTENT_TYPE, "application/json; charset=utf-8");
        connection.setRequestProperty(HttpHeaders.USER_AGENT, "curl/7.29.0");
        connection.setDoOutput(true);
        return connection;
    }

    private JSONObject createJsonBarCode(String barCode) {
        JSONObject json = new JSONObject();
        json.put("barCode", barCode);
        return json;
    }

    private JSONObject createJsonPacketForFirst(Packet packet) {
        setUpGlobalConfParams();

        JSONObject json = new JSONObject();
        json.put("Client", createClientJson(packet));
        json.put("Produit", createProductJson(packet));

        return json;
    }

    private JSONObject createClientJson(Packet packet) {
        JSONObject client = new JSONObject();
        client.put("nom", getValue(packet.getCustomerName()));
        client.put("gouvernerat", packet.getCity().getGovernorate().getName());
        client.put("ville", packet.getCity().getName());
        client.put("adresse", getValue(packet.getAddress()).replace(REG_BS, " "));
        client.put("telephone", getPhoneNumber1(packet.getCustomerPhoneNb()));
        client.put("telephone2", getPhoneNumber2(packet.getCustomerPhoneNb()));
        return client;
    }

    private JSONObject createProductJson(Packet packet) {
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
        if(price == 0.0 ) price = 0.1;
        return price;
    }
}
