package com.clothing.management.controllers;

import com.clothing.management.dto.GroupedCitiesDTO;
import com.clothing.management.entities.City;
import com.clothing.management.entities.Governorate;
import com.clothing.management.services.CityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("city")
@CrossOrigin
public class CityController {

    @Autowired
    CityService cityService;

    @GetMapping(path = "/findAll")
    public List<City> findAllCitys() {
        return cityService.findAllCities();
    }

    @GetMapping(path = "/findGroupedCities")
    public List<GroupedCitiesDTO> findGroupedCities() {
        return cityService.findGroupedCities();
    }

    @GetMapping(path = "/findById/{id}")
    public Optional<City> findByIdCity(@PathVariable Long idCity) {
        return cityService.findCityById(idCity);
    }

    @PostMapping(value = "/add" , produces = "application/json")
    public City addCity(@RequestBody  City city) {
        return cityService.addCity(city);
    }

    @PutMapping(value = "/update" , produces = "application/json")
    public City updateCity(@RequestBody City city) {
        return cityService.updateCity(city);
    }

    @DeleteMapping(value = "/delete" , produces = "application/json")
    public void deleteCity(@RequestBody City city) {
        cityService.deleteCity(city);
    }

    @DeleteMapping(value = "/deleteSelectedCities/{citiesId}" , produces = "application/json")
    public void deleteSelectedCities(@PathVariable List<Long> citiesId) {
        cityService.deleteSelectedCities(citiesId);
    }
}
