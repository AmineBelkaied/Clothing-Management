package com.clothing.management.services;

import com.clothing.management.entities.Size;

import java.util.List;
import java.util.Optional;

public interface SizeService {

    public List<Size> findAllSizes();
    public Optional<Size> findSizeById(Long idSize);
    public Size addSize(Size size);
    public Size updateSize(Size size);
    public void deleteSizeById(Long idSize);
}
