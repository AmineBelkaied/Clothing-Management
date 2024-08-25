package com.clothing.management.exceptions.custom.alreadyexists;

import com.clothing.management.exceptions.generic.AlreadyExistsException;

public class CityAlreadyExistsException extends AlreadyExistsException {

    public CityAlreadyExistsException(Long cityId, String cityName) {
        super("City", cityId, cityName);
    }
}
