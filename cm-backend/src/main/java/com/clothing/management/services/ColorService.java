package com.clothing.management.services;

import com.clothing.management.entities.Color;

import java.util.List;
import java.util.Optional;

public interface ColorService {

    List<Color> findAllColors();
    Optional<Color> findColorById(Long idColor);
    Optional<Color> findColorByName(String name);
    Color addColor(Color color) throws Exception;
    Color updateColor(Color color);
    void deleteColor(Color color);
    void deleteColorById(Long idColor);
    Long checkColorUsage(Long id);
}
