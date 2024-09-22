package com.clothing.management.dto;

import com.clothing.management.entities.City;
import com.clothing.management.entities.Governorate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GroupedCitiesDTO {

    private Governorate governorate;
    private List<City> cities;

}
