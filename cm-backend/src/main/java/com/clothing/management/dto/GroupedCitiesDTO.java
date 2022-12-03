package com.clothing.management.dto;

import com.clothing.management.entities.City;
import com.clothing.management.entities.Governorate;

import java.util.List;

public class GroupedCitiesDTO {

    private Governorate governorate;
    private List<City> cities;

    public GroupedCitiesDTO() {
    }

    public GroupedCitiesDTO(Governorate governorate, List<City> cities) {
        this.governorate = governorate;
        this.cities = cities;
    }

    public Governorate getGovernorate() {
        return governorate;
    }

    public void setGovernorate(Governorate governorate) {
        this.governorate = governorate;
    }

    public List<City> getCities() {
        return cities;
    }

    public void setCities(List<City> cities) {
        this.cities = cities;
    }

    @Override
    public String toString() {
        return "GroupedCitiesDTO{" +
                "governorate=" + governorate +
                ", cities=" + cities +
                '}';
    }
}
