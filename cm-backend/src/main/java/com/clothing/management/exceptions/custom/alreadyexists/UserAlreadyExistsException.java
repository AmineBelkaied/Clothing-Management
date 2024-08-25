package com.clothing.management.exceptions.custom.alreadyexists;

import com.clothing.management.exceptions.generic.AlreadyExistsException;

public class UserAlreadyExistsException extends AlreadyExistsException {

    public UserAlreadyExistsException(Long userId, String userName) {
        super("User", userId, userName);
    }
}
