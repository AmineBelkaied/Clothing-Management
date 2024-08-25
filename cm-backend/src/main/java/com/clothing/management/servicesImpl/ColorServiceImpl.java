package com.clothing.management.servicesImpl;

import com.clothing.management.entities.Color;
import com.clothing.management.exceptions.custom.alreadyexists.ColorAlreadyExistsException;
import com.clothing.management.repository.IColorRepository;
import com.clothing.management.services.ColorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ColorServiceImpl implements ColorService {

    private final IColorRepository colorRepository;

    @Autowired
    public ColorServiceImpl(IColorRepository colorRepository) {
        this.colorRepository = colorRepository;
    }

    @Override
    public List<Color> findAllColors() {
        return colorRepository.findAll();
    }

    @Override
    public Optional<Color> findColorById(Long idColor) {
        return colorRepository.findById(idColor);
    }

    @Override
    public Optional<Color> findColorByName(String name) {
        return colorRepository.findByNameIsIgnoreCase(name);
    }

    @Override
    public Color addColor(Color color) {
        checkColorExistence(color.getName());
        return colorRepository.save(color);
    }

    @Override
    public Color updateColor(Color color) {
        checkColorExistence(color.getName());
        return colorRepository.save(color);
    }

    @Override
    public void deleteColor(Color color) {
        colorRepository.delete(color);
    }

    @Override
    public void deleteColorById(Long idColor) {
        colorRepository.deleteById(idColor);
    }

    private void checkColorExistence(String name) {
        findColorByName(name)
                .ifPresent(existingColor -> {
                    throw new ColorAlreadyExistsException(existingColor.getId(), existingColor.getName());
                });
    }
}
