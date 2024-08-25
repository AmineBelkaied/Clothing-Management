package com.clothing.management.exceptions.custom.notfound;

import com.clothing.management.exceptions.generic.EntityNotFoundException;

public class CityNotFoundException extends EntityNotFoundException {

    public CityNotFoundException(Long cityId, String cityName) {
        super("City", cityId, cityName);
    }
}
