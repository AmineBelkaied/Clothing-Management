package com.clothing.management.servicesImpl;

import com.clothing.management.dto.GroupedCitiesDTO;
import com.clothing.management.entities.City;
import com.clothing.management.entities.Governorate;
import com.clothing.management.repository.ICityRepository;
import com.clothing.management.repository.IGovernorateRepository;
import com.clothing.management.services.CityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static java.util.stream.Collectors.groupingBy;

@Service
public class CityServiceImpl implements CityService {

    @Autowired
    ICityRepository cityRepository;

    @Autowired
    IGovernorateRepository governorateRepository;

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
        Map<Governorate, List<City>> groupedCitiesDTO = cityRepository.findAll()
                .stream()
                .collect(groupingBy(City::getGovernorate));
        //Map<Governorate, List<City>> groupedCitiesDTOSQL = cityRepository.findAllgroupedCities();

        List<GroupedCitiesDTO> groupedCitiesDTOList = new ArrayList<>();
        for (Governorate governorate : groupedCitiesDTO.keySet()) {
            GroupedCitiesDTO groupedCity = new GroupedCitiesDTO(governorate , groupedCitiesDTO.get(governorate));
            groupedCitiesDTOList.add(groupedCity);
        }
        return groupedCitiesDTOList;
    }
}
