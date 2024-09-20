package com.clothing.management.servicesImpl;

import com.clothing.management.entities.Color;
import com.clothing.management.exceptions.custom.alreadyexists.ColorAlreadyExistsException;
import com.clothing.management.repository.IColorRepository;
import com.clothing.management.services.ColorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ColorServiceImpl implements ColorService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ColorServiceImpl.class);

    private final IColorRepository colorRepository;

    @Autowired
    public ColorServiceImpl(IColorRepository colorRepository) {
        this.colorRepository = colorRepository;
    }

    @Override
    public List<Color> findAllColors() {
        LOGGER.info("Fetching all colors");
        List<Color> colors = colorRepository.findAll();
        LOGGER.debug("Found {} colors", colors.size());
        return colors;
    }

    @Override
    public Optional<Color> findColorById(Long idColor) {
        LOGGER.info("Searching color by ID: {}", idColor);
        Optional<Color> color = colorRepository.findById(idColor);
        if (color.isPresent()) {
            LOGGER.debug("Found color with ID: {}", idColor);
        } else {
            LOGGER.warn("Color with ID {} not found", idColor);
        }
        return color;
    }

    @Override
    public Optional<Color> findColorByName(String name) {
        LOGGER.info("Searching color by name: {}", name);
        Optional<Color> color = colorRepository.findByNameIsIgnoreCase(name);
        if (color.isPresent()) {
            LOGGER.debug("Found color with name: {}", name);
        } else {
            LOGGER.warn("Color with name {} not found", name);
        }
        return color;
    }

    @Override
    public Color addColor(Color color) {
        LOGGER.info("Adding new color with name: {}", color.getName());
        checkColorExistence(color.getName());
        Color savedColor = colorRepository.save(color);
        LOGGER.info("Color added successfully with ID: {}", savedColor.getId());
        return savedColor;
    }

    @Override
    public Color updateColor(Color color) {
        LOGGER.info("Updating color with name: {}", color.getName());
        checkColorExistence(color.getName());
        Color updatedColor = colorRepository.save(color);
        LOGGER.info("Color updated successfully with ID: {}", updatedColor.getId());
        return updatedColor;
    }

    @Override
    public void deleteColor(Color color) {
        LOGGER.info("Deleting color with ID: {}", color.getId());
        colorRepository.delete(color);
        LOGGER.info("Color with ID {} deleted successfully", color.getId());
    }

    @Override
    public void deleteColorById(Long idColor) {
        LOGGER.info("Deleting color by ID: {}", idColor);
        colorRepository.deleteById(idColor);
        LOGGER.info("Color with ID {} deleted successfully", idColor);
    }

    private void checkColorExistence(String name) {
        LOGGER.debug("Checking if color with name {} exists", name);
        findColorByName(name)
                .ifPresent(existingColor -> {
                    LOGGER.error("Color with name {} already exists", name);
                    throw new ColorAlreadyExistsException(existingColor.getId(), existingColor.getName());
                });
    }
}
