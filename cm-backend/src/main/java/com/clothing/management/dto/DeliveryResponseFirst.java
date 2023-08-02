package com.clothing.management.dto;

import net.minidev.json.annotate.JsonIgnore;

public class DeliveryResponseFirst {

    private int status;
    private boolean isError;
    private String message;
    private Result result;

    @JsonIgnore
    int responseCode;

    public DeliveryResponseFirst() {
    }

    public DeliveryResponseFirst(int status, boolean isError, String message, Result result) {
        this.status = status;
        this.isError = isError;
        this.message = message;
        this.result = result;
    }

    public int getStatus() {
        return status;
    }
    public void setResponseCode(int responseCode) {
        this.responseCode = responseCode;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public boolean isError() {
        return isError;
    }

    public void setIsError(boolean isError) {
        this.isError = isError;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Result getResult() {
        return result;
    }

    public void setResult(Result result) {
        this.result = result;
    }

    @Override
    public String toString() {
        return "DeliveryResponseFirst{" +
                "status=" + status +
                ", isError=" + isError +
                ", message='" + message + '\'' +
                ", result=" + result +
                '}';
    }

    public int getResponseCode() {
        return responseCode;
    }

    public static class Result {
        private String barCode;
        private String link;
        private String state;

        public Result() {
        }

        public String getBarCode() {
            return barCode;
        }

        public String getLink() {
            return link;
        }

        public String getState() {
            return state;
        }

        @Override
        public String toString() {
            return "Result{" +
                    "barCode='" + barCode + '\'' +
                    ", link='" + link + '\'' +
                    ", state='" + state + '\'' +
                    '}';
        }
    }
}
