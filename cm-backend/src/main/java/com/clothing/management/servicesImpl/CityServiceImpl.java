package com.clothing.management.servicesImpl;

import com.clothing.management.dto.GroupedCitiesDTO;
import com.clothing.management.entities.City;
import com.clothing.management.entities.Governorate;
import com.clothing.management.exceptions.custom.notfound.GovernorateNotFoundException;
import com.clothing.management.repository.ICityRepository;
import com.clothing.management.repository.IGovernorateRepository;
import com.clothing.management.services.CityService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CityServiceImpl implements CityService {

    private static final Logger LOGGER = LoggerFactory.getLogger(CityServiceImpl.class);

    private final ICityRepository cityRepository;
    private final IGovernorateRepository governorateRepository;

    @Autowired
    public CityServiceImpl(ICityRepository cityRepository, IGovernorateRepository governorateRepository) {
        this.cityRepository = cityRepository;
        this.governorateRepository = governorateRepository;
    }

    @Override
    public List<City> findAllCities() {
        List<City> cities = cityRepository.findAll();
        LOGGER.debug("Found {} cities", cities.size());
        return cities;
    }

    @Override
    public Optional<City> findCityById(Long idCity) {
        Optional<City> city = cityRepository.findById(idCity);
        if (city.isPresent()) {
            LOGGER.debug("Found city with ID: {}", idCity);
        } else {
            LOGGER.warn("City with ID {} not found", idCity);
        }
        return city;
    }

    @Override
    public City addCity(City city) {
        governorateRepository.findById(city.getGovernorate().getId())
                .ifPresentOrElse(
                        city::setGovernorate,
                        () -> {
                            LOGGER.error("Governorate with ID {} not found", city.getGovernorate().getId());
                            throw new GovernorateNotFoundException(city.getGovernorate().getId(), city.getGovernorate().getName());
                        });
        City savedCity = cityRepository.save(city);
        LOGGER.info("City added successfully with ID: {}", savedCity.getId());
        return savedCity;
    }

    @Override
    public City updateCity(City city) {
        City updatedCity = cityRepository.save(city);
        LOGGER.info("City updated successfully with ID: {}", updatedCity.getId());
        return updatedCity;
    }

    @Override
    public void deleteCity(City city) {
        cityRepository.delete(city);
        LOGGER.info("City with ID {} deleted successfully", city.getId());
    }

    @Override
    public void deleteSelectedCities(List<Long> citiesId) {
        cityRepository.deleteAllById(citiesId);
        LOGGER.info("Selected cities deleted successfully");
    }

    @Override
    public void deleteCityById(Long id) {
        cityRepository.deleteById(id);
        LOGGER.info("City with ID {} deleted successfully", id);
    }

    @Override
    public List<GroupedCitiesDTO> findGroupedCities() {
        List<GroupedCitiesDTO> groupedCities = cityRepository.findAll()
                .stream()
                .collect(Collectors.groupingBy(City::getGovernorate))
                .entrySet()
                .stream()
                .map(entry -> new GroupedCitiesDTO(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());
        LOGGER.debug("Found {} grouped cities", groupedCities.size());
        return groupedCities;
    }
}
