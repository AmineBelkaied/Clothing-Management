package com.clothing.management.servicesImpl;

import com.clothing.management.entities.Color;
import com.clothing.management.entities.Size;
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
    public Size addSize(Size size) throws Exception {
        Size sizeByReference = sizeRepository.findByReference(size.getReference());
        if(sizeByReference != null)
            throw new Exception("Cette référence existe déjà");
        return sizeRepository.save(size);
    }

    @Override
    public Size updateSize(Size size) throws Exception {
        Size sizeByReference = sizeRepository.findByReference(size.getReference());
        if(sizeByReference != null)
            throw new Exception("Cette référence existe déjà");
        return sizeRepository.save(size);
    }

    @Override
    public void deleteSizeById(Long idSize) {
        sizeRepository.deleteById(idSize);
    }
}
