package com.clothing.management.services;

import com.clothing.management.dto.GroupedCitiesDTO;
import com.clothing.management.entities.City;

import java.util.List;
import java.util.Optional;

public interface CityService {
    
    public List<City> findAllCities();
    public Optional<City> findCityById(Long idCity);
    public List<GroupedCitiesDTO> findGroupedCities();
    public City addCity(City city);
    public City updateCity(City city);
    public void deleteCity(City city);
    public void deleteSelectedCities(List<Long> citiesId);
    public void deleteCityById(Long id);
}
