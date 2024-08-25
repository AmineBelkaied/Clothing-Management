package com.clothing.management.servicesImpl;

import com.clothing.management.entities.Size;
import com.clothing.management.exceptions.custom.alreadyexists.ColorAlreadyExistsException;
import com.clothing.management.exceptions.custom.alreadyexists.SizeAlreadyExistsException;
import com.clothing.management.repository.ISizeRepository;
import com.clothing.management.services.SizeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SizeServiceImpl implements SizeService {

    @Autowired
    ISizeRepository sizeRepository;

    @Override
    public List<Size> findAllSizes() {
        return sizeRepository.findAll();
    }

    @Override
    public Optional<Size> findSizeById(Long idSize) {
        return sizeRepository.findById(idSize);
    }

    @Override
    public Optional<Size> findSizeByReference(String reference) {
        return sizeRepository.findByReferenceIsIgnoreCase(reference);
    }

    @Override
    public Size addSize(Size size) {
        checkSizeExistence(size.getReference());
        return sizeRepository.save(size);
    }

    @Override
    public Size updateSize(Size size) {
        checkSizeExistence(size.getReference());
        return sizeRepository.save(size);
    }

    @Override
    public void deleteSizeById(Long idSize) {
        sizeRepository.deleteById(idSize);
    }

    private void checkSizeExistence(String reference) {
        findSizeByReference(reference)
                .ifPresent(existingSize -> {
                    throw new SizeAlreadyExistsException(existingSize.getId(), existingSize.getReference());
                });
    }
}
