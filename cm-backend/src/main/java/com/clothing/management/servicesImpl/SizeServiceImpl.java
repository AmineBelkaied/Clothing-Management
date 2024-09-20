package com.clothing.management.servicesImpl;

import com.clothing.management.entities.Size;
import com.clothing.management.exceptions.custom.alreadyexists.SizeAlreadyExistsException;
import com.clothing.management.repository.ISizeRepository;
import com.clothing.management.services.SizeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SizeServiceImpl implements SizeService {

    private static final Logger LOGGER = LoggerFactory.getLogger(SizeServiceImpl.class);

    private final ISizeRepository sizeRepository;

    public SizeServiceImpl(ISizeRepository sizeRepository) {
        this.sizeRepository = sizeRepository;
    }

    @Override
    public List<Size> findAllSizes() {
        LOGGER.info("Fetching all sizes");
        List<Size> sizes = sizeRepository.findAll();
        LOGGER.debug("Found {} sizes", sizes.size());
        return sizes;
    }

    @Override
    public Optional<Size> findSizeById(Long idSize) {
        LOGGER.info("Searching size by ID: {}", idSize);
        Optional<Size> size = sizeRepository.findById(idSize);
        if (size.isPresent()) {
            LOGGER.debug("Found size: {}", size.get().getReference());
        } else {
            LOGGER.warn("Size with ID {} not found", idSize);
        }
        return size;
    }

    @Override
    public Optional<Size> findSizeByReference(String reference) {
        LOGGER.info("Searching size by reference: {}", reference);
        Optional<Size> size = sizeRepository.findByReferenceIsIgnoreCase(reference);
        if (size.isPresent()) {
            LOGGER.debug("Found size with reference: {}", reference);
        } else {
            LOGGER.warn("Size with reference {} not found", reference);
        }
        return size;
    }

    @Override
    public Size addSize(Size size) {
        LOGGER.info("Adding new size with reference: {}", size.getReference());
        checkSizeExistence(size.getReference());
        Size savedSize = sizeRepository.save(size);
        LOGGER.info("Size added successfully with ID: {}", savedSize.getId());
        return savedSize;
    }

    @Override
    public Size updateSize(Size size) {
        LOGGER.info("Updating size with reference: {}", size.getReference());
        checkSizeExistence(size.getReference());
        Size updatedSize = sizeRepository.save(size);
        LOGGER.info("Size updated successfully with ID: {}", updatedSize.getId());
        return updatedSize;
    }

    @Override
    public void deleteSizeById(Long idSize) {
        LOGGER.info("Deleting size with ID: {}", idSize);
        sizeRepository.deleteById(idSize);
        LOGGER.info("Size with ID {} deleted successfully", idSize);
    }

    private void checkSizeExistence(String reference) {
        LOGGER.debug("Checking if size with reference {} exists", reference);
        findSizeByReference(reference)
                .ifPresent(existingSize -> {
                    LOGGER.error("Size with reference {} already exists", reference);
                    throw new SizeAlreadyExistsException(existingSize.getId(), existingSize.getReference());
                });
    }
}
