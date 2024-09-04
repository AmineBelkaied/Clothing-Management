package com.clothing.management.services;

import com.clothing.management.entities.Size;

import java.util.List;
import java.util.Optional;

public interface SizeService {

    List<Size> findAllSizes();
    Optional<Size> findSizeById(Long idSize);
    Optional<Size> findSizeByReference(String reference);
    Size addSize(Size size) throws Exception;
    Size updateSize(Size size) throws Exception;
    void deleteSizeById(Long idSize);
}
