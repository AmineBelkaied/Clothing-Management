package com.clothing.management.services;

import com.clothing.management.entities.Color;

import java.util.List;
import java.util.Optional;

public interface ColorService {

    public List<Color> findAllColors();
    public Optional<Color> findColorById(Long idColor);
    public Color addColor(Color color) throws Exception;
    public Color updateColor(Color color) throws Exception;
    public void deleteColor(Color color);
    public void deleteColorById(Long idColor);
}
