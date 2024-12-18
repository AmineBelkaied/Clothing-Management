package com.clothing.management.mappers;

import com.clothing.management.entities.Color;
import com.clothing.management.entities.FbPage;
import com.clothing.management.entities.Size;
import com.clothing.management.repository.IFbPageRepository;
import org.mapstruct.Named;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class OfferMapperHelper {
    private final IFbPageRepository fbPageRepository;

    public OfferMapperHelper(IFbPageRepository fbPageRepository) {
        this.fbPageRepository = fbPageRepository;
    }

    @Named("mapFbPagesToIds")
    public Set<Long> mapFbPagesToIds(List<FbPage> fbPages) {
        return fbPages.stream()
                .map(FbPage::getId)
                .collect(Collectors.toSet());
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
}
