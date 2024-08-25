package com.clothing.management.exceptions.custom.others;

public class UserNotAuthenticatedException extends RuntimeException {

    public UserNotAuthenticatedException(String message) {
        super(message);
    }
}
