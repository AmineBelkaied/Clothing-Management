package com.clothing.management.servicesImpl.api;

import com.clothing.management.dto.DeliveryResponseFirst;
import com.clothing.management.entities.Packet;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.minidev.json.JSONObject;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

@Service
public class FirstApiService {

    public static final String createBarCodeEndPoint = "https://www.firstdeliverygroup.com/api/v2/create";
    public static final String getLastStatusEndPoint = "https://www.firstdeliverygroup.com/api/v2/etat";
    public static final String reg = "/,/gi";
    public static final String regBS = "/\\n/gi";

    public static final String bearerToken = "af62884f-bfd1-4aff-8bf4-71dd0c92a7f4";

    public FirstApiService() {
    }

    public DeliveryResponseFirst createBarCode(Packet packet) throws IOException {
        String jsonBody = createJsonPacketForFirst(packet).toString();
        return executeHttpRequest(createBarCodeEndPoint, jsonBody);
    }

    public DeliveryResponseFirst getLastStatus(String barCode) throws IOException {
        JSONObject jsonBody = createJsonBarCode(barCode);
        System.out.println("jsonBody:"+jsonBody);
        return executeHttpRequest(getLastStatusEndPoint, jsonBody.toString());
    }

    private DeliveryResponseFirst executeHttpRequest(String url, String jsonBody) throws IOException {
        URL urlConnection = new URL(url);
        HttpURLConnection connection = (HttpURLConnection) urlConnection.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Authorization", "Bearer " + bearerToken);
        connection.setRequestProperty("Content-Type", "application/json; charset=utf-8");
        connection.setDoOutput(true);

        try (OutputStream outputStream = connection.getOutputStream()) {
            byte[] input = jsonBody.getBytes("utf-8");
            outputStream.write(input, 0, input.length);
        }
        DeliveryResponseFirst deliveryResponse = null;
        int responseCode = connection.getResponseCode();
        System.out.println("responseCode:"+responseCode);
        String responseMessage = connection.getResponseMessage();
        System.out.println("responseMessage:"+responseMessage);

        if(responseCode!= 404){
            System.out.println("!404 ");
            StringBuilder response = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    System.out.println("line "+line);
                    response.append(line);
                }
            }
            System.out.println("Response Body: " + response.toString());

            ObjectMapper mapper = new ObjectMapper();
            deliveryResponse = mapper.readValue(response.toString(), DeliveryResponseFirst.class);
            deliveryResponse.setResponseCode(responseCode);
            deliveryResponse.setMessage(responseMessage);
        }else {
            System.out.println("404 ");
            deliveryResponse.setMessage("not found");
            deliveryResponse.setStatus(404);
            deliveryResponse.setResponseCode(404);
            deliveryResponse.setIsError(true);
        }

        System.out.println("FASdeliveryResponse: " + deliveryResponse.toString());
        connection.disconnect();
        return deliveryResponse;
    }


    private JSONObject createJsonBarCode(String barCode) {
        JSONObject json = new JSONObject();
        json.put("barCode", barCode);
        return json;
    }

    private JSONObject createJsonPacketForFirst(Packet packet) {
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
        produit.put("commentaire", "Le colis peut être ouvert à la demande du client");
        produit.put("echange", packet.isExchange()?"oui":"non");
        produit.put("article", "Diggie pants");
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
