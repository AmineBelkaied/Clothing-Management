

package com.clothing.management.dto;

import net.minidev.json.annotate.JsonIgnore;

public class DeliveryResponseFirst extends DeliveryResponse {
    private int status;
    private boolean isError;
    private String message;
    private Result result;

    @JsonIgnore
    public int responseCode;
    @JsonIgnore
    public String responseMessage;

    public DeliveryResponseFirst() {
        super();
    }

    public DeliveryResponseFirst(int status, boolean isError, String message, Result result) {
        this.status = status;
        this.isError = isError;
        this.message = message;
        this.result = result;
    }

    /*public DeliveryResponseFirst(int status, String isError, String message, Result result) {
        super(status, isError, message, result.getLink(), result.getState(), result.getBarCode());
    }*/
    public int getStatus() {
        return status;
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