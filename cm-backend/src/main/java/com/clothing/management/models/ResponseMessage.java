package com.clothing.management.models;

public class ResponseMessage {
    private String message;
    private byte[] bytes;


    public ResponseMessage(String message, byte[] bytes) {
        this.message = message;
        this.bytes = bytes;
    }

    public String getMessage() {
        return message;
    }

    public byte[] getBytes() {
        return bytes;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}