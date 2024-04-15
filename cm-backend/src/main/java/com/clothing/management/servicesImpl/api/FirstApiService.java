package com.clothing.management.servicesImpl.api;

import com.clothing.management.controllers.GlobalConfController;
import com.clothing.management.dto.DeliveryResponse;
import com.clothing.management.dto.DeliveryResponseFirst;
import com.clothing.management.entities.DeliveryCompany;
import com.clothing.management.entities.GlobalConf;
import com.clothing.management.entities.Packet;
import com.clothing.management.repository.IGlobalConfRepository;
import com.clothing.management.services.GlobalConfService;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.minidev.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.PropertiesPropertySource;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.io.InputStream;
import java.util.Properties;

@Service
public class FirstApiService {

    @Autowired
    public IGlobalConfRepository globalConfRepository;

    //private final String comment="Le colis peut être ouvert à la demande du client";
    public static final String createBarCodeEndPoint = "https://www.firstdeliverygroup.com/api/v2/create";
    public static final String getLastStatusEndPoint = "https://www.firstdeliverygroup.com/api/v2/etat";
    public static final String reg = "/,/gi";
    public static final String regBS = "/\\n/gi";
    //private final String comment="يسمح بفتح الطرد للحريف، لايرجع المال بعد الدفع";//lyft
    //private final String bearerToken="198de763-841f-4b3f-96b0-dcbfa4a6b369";//lyft
    //private final String exchangeProduct="Lyft sport";//lyft

    private String bearerToken="aaa";//"af62884f-bfd1-4aff-8bf4-71dd0c92a7f4";//diggie
    private String exchangeProduct="Diggie pants";//diggie
    private String comment="يسمح بفتح الطرد عند طلب الحريف";//diggie


    public FirstApiService() {
    }

    public DeliveryResponseFirst createBarCode(Packet packet) throws IOException {
        String jsonBody = createJsonPacketForFirst(packet).toString();
        return executeHttpRequest(createBarCodeEndPoint, jsonBody,packet.getDeliveryCompany());
    }

    public DeliveryResponseFirst getLastStatus(String barCode, DeliveryCompany deliveryCompany) throws IOException {
        JSONObject jsonBody = createJsonBarCode(barCode);
        //System.out.println("jsonBody:"+jsonBody);
        return executeHttpRequest(getLastStatusEndPoint, jsonBody.toString(),deliveryCompany);
    }

    private DeliveryResponseFirst executeHttpRequest(String url, String jsonBody,DeliveryCompany deliveryCompany) throws IOException {
        //System.out.println("jsonBody: " + jsonBody);
        URL urlConnection = new URL(url);
        HttpsURLConnection connection = (HttpsURLConnection) urlConnection.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Authorization", "Bearer " + deliveryCompany.getToken());
        connection.setRequestProperty("Content-Type", "application/json; charset=utf-8");
        connection.setRequestProperty("User-Agent", "curl/7.29.0");
        connection.setDoOutput(true);
        StringBuilder response = new StringBuilder();

        DeliveryResponseFirst deliveryResponse = new DeliveryResponseFirst();

        try (OutputStream outputStream = connection.getOutputStream()) {
            byte[] input = jsonBody.getBytes("utf-8");
            outputStream.write(input, 0, input.length);
        } catch (Exception e) {
            System.out.println("Error in writing to OutputStream: " + e);
        }

        int responseCode = connection.getResponseCode();
        String responseMessage = connection.getResponseMessage();

        if (responseCode != HttpURLConnection.HTTP_NOT_FOUND) {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    System.out.println("line: " + line);
                    response.append(line);
                }
            } catch (Exception e) {
                System.out.println("Error in reading InputStream: " + e);
            }
            ObjectMapper mapper = new ObjectMapper();
            deliveryResponse = mapper.readValue(response.toString(), DeliveryResponseFirst.class);
            //deliveryResponse = (DeliveryResponseFirst) new DeliveryResponse(deliveryResponse);
            deliveryResponse.setResponseCode(responseCode);
            deliveryResponse.setResponseMessage(responseMessage);
        } else {
            InputStream errorStream = connection.getErrorStream();
            if (errorStream != null) {
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(errorStream))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                } catch (Exception e) {
                    System.out.println("Error in reading ErrorStream: " + e);
                }
            } else {
                System.out.println("Error stream is null.");
            }

            deliveryResponse.setResponseMessage("not found");
            deliveryResponse.setStatus(responseCode);
            deliveryResponse.setResponseCode(responseCode);
            deliveryResponse.setIsError(true);
        }
        connection.disconnect();
        return deliveryResponse;
    }

    private JSONObject createJsonBarCode(String barCode) {
        JSONObject json = new JSONObject();
        json.put("barCode", barCode);
        return json;
    }

    private JSONObject createJsonPacketForFirst(Packet packet) {
        GlobalConf globalConf = globalConfRepository.findAll().stream().findFirst().orElse(null);
        //System.out.println("global:"+globalConf);
        if (globalConf != null) {
            comment = globalConf.getComment();
            exchangeProduct = globalConf.getExchangeComment();
        }
        JSONObject json = new JSONObject();
        JSONObject client = new JSONObject();
        client.put("nom", this.getValue(packet.getCustomerName()));
        client.put("gouvernerat", packet.getCity().getGovernorate().getName());
        client.put("ville", packet.getCity().getName());
        client.put("adresse", this.getValue(packet.getAddress()).replace(this.regBS, " "));
        client.put("telephone", this.getPhoneNumber1(packet.getCustomerPhoneNb()));
        client.put("telephone2", this.getPhoneNumber2(packet.getCustomerPhoneNb()));
        json.put("Client", client);

        JSONObject produit = new JSONObject();
        produit.put("prix", this.getPacketPrice(packet));
        produit.put("designation", this.getPacketDesignation(packet));
        produit.put("nombreArticle", 1);
        produit.put("commentaire", comment);
        produit.put("echange", packet.isExchange()?"oui":"non");
        produit.put("article", exchangeProduct);
        produit.put("nombreEchange", packet.isExchange()?1:0);
        json.put("Produit", produit);

        return json;
    }

    private String getPhoneNumber1(String telephoneNumber1) {
        if (!this.getValue(telephoneNumber1).isEmpty() && telephoneNumber1.contains("/")) {
            return telephoneNumber1.substring(0, 8);
        }
        return this.getValue(telephoneNumber1);
    }

    private String getPhoneNumber2(String telephoneNumber2) {
        if (!this.getValue(telephoneNumber2).isEmpty() && telephoneNumber2.contains("/")) {
            return telephoneNumber2.substring(9, telephoneNumber2.length());
        }
        return "";
    }

    private String getValue(String fieldName) {
        return fieldName != null ? fieldName : "";
    }

    private String getPacketDesignation(Packet packet) {
        return this.getValue(packet.getId().toString())
                .concat(" ")
                .concat(packet.getFbPage() != null ? this.getValue(packet.getFbPage().getName()) : "")
                .concat(" | ")
                .concat(this.getValue(packet.getPacketDescription().replace(this.reg, ", ")));
    }

    private Double getPacketPrice(Packet packet) {
        Double price = packet.getPrice() + packet.getDeliveryPrice() - packet.getDiscount();
        if(price == 0.0 ) price = 0.1;
        return price;
    }
}
