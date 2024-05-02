package com.clothing.management.servicesImpl.api;

import com.clothing.management.dto.DeliveryResponseNavex;
import com.clothing.management.entities.DeliveryCompany;
import com.clothing.management.entities.GlobalConf;
import com.clothing.management.entities.Packet;
import com.clothing.management.repository.IGlobalConfRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

@Service
public class NavexApiService {

    @Autowired
    public IGlobalConfRepository globalConfRepository;
    public static final String apiUrl = "https://app.navex.tn/api/";
    //public String apiName ="apiName";
    public static final String endUrl ="/v1/post.php";
    //public static final String getLastStatusEndPoint = "https://app.navex.tn/api/strada-etat-SDFKSNC48IK329084J34534LJLJ453DJL/v1/post.php";
    public static final String reg = "/,/gi";
    public static final String regBS = "/\\n/gi";
    private String exchangeProduct="Diggie pants";//diggie
    private String comment="يسمح بفتح الطرد عند طلب الحريف";//diggie


    public NavexApiService() {
        /*GlobalConf globalConf = globalConfService.getGlobalConf();
        System.out.println(globalConf);
        if (globalConf != null) {
            System.out.println("globalConf");
            comment = globalConf.getComment();
            bearerToken = globalConf.getDeliveryCompany().getToken();
            exchangeProduct = globalConf.getExchangeComment();
            apiName = globalConf.getDeliveryCompany().getApiName();
            //System.out.println(comment);
        }*/
    }

    public DeliveryResponseNavex createBarCode(Packet packet) throws IOException {
        String url = apiUrl+packet.getDeliveryCompany().getApiName()+"-"+packet.getDeliveryCompany().getToken()+endUrl;
        return executeHttpRequest(url, createRequestBody(packet));
    }

    public DeliveryResponseNavex getLastStatus(String barCode, DeliveryCompany deliveryCompany) throws IOException {
        StringBuilder body = new StringBuilder();
        body.append("code=").append(barCode);
        String url = apiUrl+deliveryCompany.getApiName()+"-etat-"+deliveryCompany.getToken()+endUrl;
        return executeHttpRequest(url, body.toString());
    }

    private DeliveryResponseNavex executeHttpRequest(String url, String body) throws IOException {
        GlobalConf globalConf = globalConfRepository.findAll().stream().findFirst().orElse(null);
        System.out.println("global:"+globalConf);
        if (globalConf != null) {
            comment = globalConf.getComment();
            exchangeProduct = globalConf.getExchangeComment();
        }
        // Create a new HTTP connection and set the request method to POST
        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
        connection.setRequestMethod("POST");

        // Enable output and send the request body
        connection.setDoOutput(true);
        try (OutputStream out = connection.getOutputStream()) {
            out.write(body.getBytes());
        }
        DeliveryResponseNavex deliveryResponse= new DeliveryResponseNavex();
        int responseCode = connection.getResponseCode();
        String responseMessage = connection.getResponseMessage();
        try (BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
            String inputLine;
            StringBuilder response = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            ObjectMapper mapper = new ObjectMapper();
            deliveryResponse = mapper.readValue(response.toString(), DeliveryResponseNavex.class);
            deliveryResponse.setResponseCode(responseCode);
            deliveryResponse.setResponseMessage(responseMessage);
        }

        // Disconnect the connection
        connection.disconnect();
        return deliveryResponse;
    }

    private String createRequestBody(Packet packet) {
        StringBuilder body = new StringBuilder();
        body.append("nom=")
                .append(this.getValue(packet.getCustomerName()))
                .append("&gouvernerat=")
                .append(packet.getCity().getGovernorate().getName())
                .append("&ville=")
                .append(packet.getCity().getName())
                .append("&adresse=")
                .append(this.getValue(packet.getAddress()).replace(this.regBS, " "))
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
        return packet.getPrice() + packet.getDeliveryPrice() - packet.getDiscount();
    }
}
