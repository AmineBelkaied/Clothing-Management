package com.clothing.management.servicesImpl;

import com.clothing.management.entities.Color;
import com.clothing.management.exceptions.custom.alreadyexists.ColorAlreadyExistsException;
import com.clothing.management.repository.IColorRepository;
import com.clothing.management.repository.IProductsPacketRepository;
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
    private final IProductsPacketRepository productsPacketRepository;

    @Autowired
    public ColorServiceImpl(IColorRepository colorRepository, IProductsPacketRepository productsPacketRepository) {
        this.colorRepository = colorRepository;
        this.productsPacketRepository = productsPacketRepository;
    }

    @Override
    public List<Color> findAllColors() {
        List<Color> colors = colorRepository.findAll();
        LOGGER.debug("Found {} colors", colors.size());
        return colors;
    }

    @Override
    public Optional<Color> findColorById(Long idColor) {
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
        checkColorExistence(color.getName());
        Color savedColor = colorRepository.save(color);
        LOGGER.info("Color added successfully with ID: {}", savedColor.getId());
        return savedColor;
    }

    @Override
    public Color updateColor(Color color) {
        Color updatedColor;
        Optional<Color> existingColor = findColorByName(color.getName());
        if (existingColor.isPresent()) {updatedColor = colorRepository.save(color);}
        else updatedColor = colorRepository.save(color);
        LOGGER.info("Color updated successfully with ID: {}", updatedColor.getId());
        return updatedColor;
    }

    @Override
    public void deleteColor(Color color) {
        colorRepository.delete(color);
        LOGGER.info("Color with ID {} deleted successfully", color.getId());
    }

    @Override
    public void deleteColorById(Long idColor) {
        colorRepository.deleteById(idColor);
        LOGGER.info("Color with ID {} deleted successfully", idColor);
    }

    @Override
    public Long checkColorUsage(Long id) {
        return productsPacketRepository.countProductsPacketByProduct_Color_Id(id);
    }

    private void checkColorExistence(String name) {
        findColorByName(name)
                .ifPresent(existingColor -> {
                    LOGGER.error("Color with name {} already exists", name);
                    throw new ColorAlreadyExistsException(existingColor.getId(), existingColor.getName());
                });
    }
}
