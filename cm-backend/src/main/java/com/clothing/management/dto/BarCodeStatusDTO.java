package com.clothing.management.dto;

import java.util.List;

public class BarCodeStatusDTO {

    private List<String> barCodes;
    private String status;

    public BarCodeStatusDTO(List<String> barCodes, String status) {
        this.barCodes = barCodes;
        this.status = status;
    }


    public List<String> getBarCodes() {
        return barCodes;
    }

    public void setBarCodes(List<String> barCodes) {
        this.barCodes = barCodes;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

}
