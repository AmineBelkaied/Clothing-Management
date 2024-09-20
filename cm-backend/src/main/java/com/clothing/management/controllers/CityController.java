package com.clothing.management.controllers;

import com.clothing.management.dto.GroupedCitiesDTO;
import com.clothing.management.entities.City;
import com.clothing.management.services.CityService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("${api.prefix}/cities")
@CrossOrigin
@Secured({"ROLE_ADMIN", "ROLE_USER"})
public class CityController {

    private final CityService cityService;
    private static final Logger LOGGER = LoggerFactory.getLogger(CityController.class);

    public CityController(CityService cityService) {
        this.cityService = cityService;
    }

    @GetMapping
    public ResponseEntity<List<City>> getAllCities() {
        LOGGER.info("Fetching all cities.");
        List<City> cities = cityService.findAllCities();
        return new ResponseEntity<>(cities, HttpStatus.OK);
    }

    @GetMapping("/grouped-by-governorate")
    public ResponseEntity<List<GroupedCitiesDTO>> getGroupedCities() {
        LOGGER.info("Fetching cities grouped by governorate.");
        List<GroupedCitiesDTO> groupedCities = cityService.findGroupedCities();
        return new ResponseEntity<>(groupedCities, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<City> getCityById(@PathVariable Long id) {
        LOGGER.info("Fetching city with ID: {}", id);
        Optional<City> city = cityService.findCityById(id);
        return city.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                .orElseGet(() -> {
                    LOGGER.warn("City with ID {} not found.", id);
                    return new ResponseEntity<>(HttpStatus.NOT_FOUND);
                });
    }

    @PostMapping
    public ResponseEntity<City> createCity(@RequestBody City city) {
        LOGGER.info("Creating a new city with name: {}", city.getName());
        City createdCity = cityService.addCity(city);
        return new ResponseEntity<>(createdCity, HttpStatus.CREATED);
    }

    @PutMapping
    public ResponseEntity<City> updateCity(@RequestBody City city) {
        LOGGER.info("Updating city with ID: {}", city.getId());
        City updatedCity = cityService.updateCity(city);
        return new ResponseEntity<>(updatedCity, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCityById(@PathVariable Long id) {
        LOGGER.info("Deleting city with ID: {}", id);
        cityService.deleteCityById(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping("/batch-delete")
    public ResponseEntity<Void> deleteSelectedCities(@RequestBody List<Long> cityIds) {
        LOGGER.info("Deleting selected cities. City IDs: {}", cityIds);
        cityService.deleteSelectedCities(cityIds);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
