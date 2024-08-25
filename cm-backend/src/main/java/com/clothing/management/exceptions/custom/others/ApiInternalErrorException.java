package com.clothing.management.exceptions.custom.others;

public class ApiInternalErrorException extends RuntimeException {

    private final String deliveryCompanyName;
    private final String message;

    public ApiInternalErrorException(String deliveryCompanyName, String message) {
        super("An internal error occurred in " + deliveryCompanyName + " API : " + message);
        this.deliveryCompanyName = deliveryCompanyName;
        this.message = message;
    }
}
