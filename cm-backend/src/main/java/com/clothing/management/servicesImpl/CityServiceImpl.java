package com.clothing.management.servicesImpl;

import com.clothing.management.dto.GroupedCitiesDTO;
import com.clothing.management.entities.City;
import com.clothing.management.entities.Governorate;
import com.clothing.management.repository.ICityRepository;
import com.clothing.management.repository.IGovernorateRepository;
import com.clothing.management.services.CityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CityServiceImpl implements CityService {

    private final ICityRepository cityRepository;
    private final IGovernorateRepository governorateRepository;

    @Autowired
    public CityServiceImpl(ICityRepository cityRepository, IGovernorateRepository governorateRepository) {
        this.cityRepository = cityRepository;
        this.governorateRepository = governorateRepository;
    }

    @Override
    public List<City> findAllCities() {
        return cityRepository.findAll();
    }

    @Override
    public Optional<City> findCityById(Long idCity) {
        return cityRepository.findById(idCity);
    }

    @Override
    public City addCity(City city) {
        Optional<Governorate> governorate = governorateRepository.findById(city.getGovernorate().getId());
        if(governorate.isPresent()) {
            city.setGovernorate(governorate.get());
            return cityRepository.save(city);
        }
        return null;
    }

    @Override
    public City updateCity(City city) {
        return cityRepository.save(city);
    }

    @Override
    public void deleteCity(City city) {
        cityRepository.delete(city);
    }

    @Override
    public void deleteSelectedCities(List<Long> citiesId) { cityRepository.deleteAllById(citiesId); }

    @Override
    public List<GroupedCitiesDTO> findGroupedCities() {
        return cityRepository.findAll()
                .stream()
                .collect(Collectors.groupingBy(City::getGovernorate))
                .entrySet()
                .stream()
                .map(entry -> new GroupedCitiesDTO(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());
    }
}
