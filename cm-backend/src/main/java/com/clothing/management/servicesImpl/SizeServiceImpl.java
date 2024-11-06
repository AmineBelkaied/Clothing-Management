package com.clothing.management.servicesImpl;

import com.clothing.management.entities.Size;
import com.clothing.management.exceptions.custom.alreadyexists.SizeAlreadyExistsException;
import com.clothing.management.exceptions.custom.notfound.PacketNotFoundException;
import com.clothing.management.repository.IProductsPacketRepository;
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
    private final IProductsPacketRepository productsPacketRepository;

    public SizeServiceImpl(ISizeRepository sizeRepository, IProductsPacketRepository productsPacketRepository) {
        this.sizeRepository = sizeRepository;
        this.productsPacketRepository = productsPacketRepository;
    }

    @Override
    public List<Size> findAllSizes() {
        List<Size> sizes = sizeRepository.findAll();
        LOGGER.debug("Found {} sizes", sizes.size());
        return sizes;
    }

    @Override
    public Size findSizeById(Long idSize) {
        Size size = sizeRepository.findById(idSize).orElseThrow(() -> {
            LOGGER.error("Size with ID: {} not found", idSize);
            return new PacketNotFoundException(idSize, "Size not found!");
        });
        return size;
    }

    @Override
    public Optional<Size> findSizeByReference(String reference) {
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
        checkSizeExistence(size.getReference());
        Size savedSize = sizeRepository.save(size);
        LOGGER.info("Size added successfully with ID: {}", savedSize.getId());
        return savedSize;
    }

    @Override
    public Size updateSize(Size size) {
        checkSizeExistence(size.getReference());
        Size updatedSize = sizeRepository.save(size);
        LOGGER.info("Size updated successfully with ID: {}", updatedSize.getId());
        return updatedSize;
    }

    @Override
    public void deleteSizeById(Long idSize) {
        sizeRepository.deleteById(idSize);
        LOGGER.info("Size with ID {} deleted successfully", idSize);
    }

    @Override
    public Long checkSizeUsage(Long id) {
        return productsPacketRepository.countProductsPacketByProduct_Size_Id(id);
    }


    private void checkSizeExistence(String reference) {
        findSizeByReference(reference)
                .ifPresent(existingSize -> {
                    LOGGER.error("Size with reference {} already exists", reference);
                    throw new SizeAlreadyExistsException(existingSize.getId(), existingSize.getReference());
                });
    }
}
