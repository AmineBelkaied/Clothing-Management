package com.clothing.management.services;

import com.clothing.management.dto.GroupedCitiesDTO;
import com.clothing.management.entities.City;

import java.util.List;
import java.util.Optional;

public interface CityService {
    
    List<City> findAllCities();
    Optional<City> findCityById(Long idCity);
    List<GroupedCitiesDTO> findGroupedCities();
    City addCity(City city);
    City updateCity(City city);
    void deleteCity(City city);
    void deleteSelectedCities(List<Long> citiesId);
    void deleteCityById(Long id);
}
