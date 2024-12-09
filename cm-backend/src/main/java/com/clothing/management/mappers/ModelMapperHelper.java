package com.clothing.management.mappers;

import com.clothing.management.entities.Color;
import com.clothing.management.entities.Product;
import com.clothing.management.entities.Size;
import com.clothing.management.repository.IColorRepository;
import com.clothing.management.repository.ISizeRepository;
import org.mapstruct.Named;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class ModelMapperHelper {

    private final IColorRepository colorRepository;
    private final ISizeRepository sizeRepository;

    public ModelMapperHelper(IColorRepository colorRepository, ISizeRepository sizeRepository) {
        this.colorRepository = colorRepository;
        this.sizeRepository = sizeRepository;
    }

    @Named("mapIdsToColors")
    public List<Color> mapIdsToColors(List<Long> ids) {
        // Fetch colors from the database
        return colorRepository.findAllById(ids);
    }

    @Named("mapIdsToSizes")
    public List<Size> mapIdsToSizes(List<Long> ids) {
        // Fetch sizes from the database
        return sizeRepository.findAllById(ids);
    }

    @Named("mapColorsToIds")
    public List<Long> mapColorsToIds(List<Color> colors) {
        return colors.stream()
                .map(Color::getId)
                .collect(Collectors.toList());
    }

    @Named("mapSizesToIds")
    public List<Long> mapSizesToIds(List<Size> sizes) {
        return sizes.stream()
                .map(Size::getId)
                .collect(Collectors.toList());
    }

    @Named("mapToDefaultId")
    public Long mapToDefaultId(List<Product> products) {
        return products
                .stream()
                .filter(product -> product.getColor() == null && product.getSize() == null)
                .findFirst()
                .map(Product::getId)
                .orElse(null);
    }


}

