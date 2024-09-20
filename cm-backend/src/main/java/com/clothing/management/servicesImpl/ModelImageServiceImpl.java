package com.clothing.management.servicesImpl;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

import com.clothing.management.entities.Model;
import com.clothing.management.entities.ModelImage;
import com.clothing.management.repository.ModelImageRepository;
import com.clothing.management.services.ModelImageService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class ModelImageServiceImpl implements ModelImageService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ModelImageServiceImpl.class);

    @Autowired
    private ModelImageRepository imageRepo;

    private final Path root = Paths.get("src/main/resources/public/uploads");

    @Override
    public ModelImage uploadImage(MultipartFile file, Long modelId) {
        String fullPath = root.resolve(file.getOriginalFilename()).toString();
        ModelImage mImage = new ModelImage();
        Optional<ModelImage> existingImage = imageRepo.findByModelId(modelId);

        if (existingImage.isPresent()) {
            mImage = existingImage.get(); // Reuse existing image if present
        }

        mImage.setName(file.getOriginalFilename());
        mImage.setType(file.getContentType());
        mImage.setImagePath(fullPath);
        mImage.setModel(Model.builder().id(modelId).build());
        try {
            Files.copy(file.getInputStream(), root.resolve(file.getOriginalFilename()));
            LOGGER.info("File {} uploaded successfully to {}", file.getOriginalFilename(), fullPath);
        } catch (FileAlreadyExistsException e) {
            LOGGER.warn("File {} already exists. Skipping file upload.", file.getOriginalFilename());
            // Proceed to save ModelImage metadata if the file already exists
        } catch (IOException e) {
            LOGGER.error("Error uploading file {}: {}", file.getOriginalFilename(), e.getMessage());
            throw new RuntimeException("Failed to upload file: " + e.getMessage());
        }

        return imageRepo.save(mImage);
    }

    @Override
    public byte[] downloadImage(Long modelId) throws IOException {
        Optional<ModelImage> imageObject = imageRepo.findByModelId(modelId);

        if (imageObject.isEmpty()) {
            LOGGER.error("Image for model ID {} not found.", modelId);
            throw new RuntimeException("Image not found for model ID: " + modelId);
        }

        String fullPath = imageObject.get().getImagePath();
        LOGGER.info("Downloading image from path: {}", fullPath);

        File imageFile = new File(fullPath);

        if (!imageFile.exists()) {
            LOGGER.error("File at path {} does not exist.", fullPath);
            throw new IOException("File not found at path: " + fullPath);
        }

        return Files.readAllBytes(imageFile.toPath());
    }
}
