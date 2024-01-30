package com.clothing.management.servicesImpl;

import com.clothing.management.entities.Color;
import com.clothing.management.repository.IColorRepository;
import com.clothing.management.services.ColorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ColorServiceImpl implements ColorService {

    @Autowired
    IColorRepository colorRepository;

    @Override
    public List<Color> findAllColors() {
        return colorRepository.findAll();
    }

    @Override
    public Optional<Color> findColorById(Long idColor) {
        return colorRepository.findById(idColor);
    }

    @Override
    public Color addColor(Color color) throws Exception {
        Color colorByReference = colorRepository.findByReference(color.getReference());
        if(colorByReference != null)
            throw new Exception("Cette référence existe déjà");
        return colorRepository.save(color);
    }

    @Override
    public Color updateColor(Color color) throws Exception {
        Color colorByReference = colorRepository.findByReference(color.getReference());
        if(colorByReference != null)
            throw new Exception("Cette référence existe déjà");
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
}
