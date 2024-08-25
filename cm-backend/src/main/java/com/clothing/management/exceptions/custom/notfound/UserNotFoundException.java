package com.clothing.management.exceptions.custom.notfound;

import com.clothing.management.exceptions.generic.EntityNotFoundException;

public class UserNotFoundException extends EntityNotFoundException {

    public UserNotFoundException(Long userId, String userName) {
        super("User", userId, userName);
    }
}
