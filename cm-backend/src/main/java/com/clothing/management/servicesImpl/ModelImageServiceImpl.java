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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class ModelImageServiceImpl implements ModelImageService{

    @Autowired
    private ModelImageRepository imageRepo;

    private final Path root = Paths.get("src/main/resources/public/uploads");

    public ModelImage uploadImage(MultipartFile file, Long modelId) {
        String fullPath = this.root.toString().concat("\\").concat(file.getOriginalFilename());
        ModelImage mImage = new ModelImage();
        Optional<ModelImage> existingImage = imageRepo.findByModelId(modelId);
        if(existingImage.isPresent()) {
            mImage = existingImage.get();
        }
        mImage.setName(file.getOriginalFilename());
        mImage.setType(file.getContentType());
        mImage.setImagePath(fullPath);
        mImage.setModel(Model.builder().id(modelId).build());
        try {
            Files.copy(file.getInputStream(), this.root.resolve(file.getOriginalFilename()));
        } catch (Exception e) {
            if (e instanceof FileAlreadyExistsException) {
               return imageRepo.save(mImage);
            }
            throw new RuntimeException(e.getMessage());
        }
        return imageRepo.save(mImage);
    }

    public byte[] downloadImage(Long modelId) throws IOException{
        Optional<ModelImage> imageObject = imageRepo.findByModelId(modelId);
        String fullPath = "";
        if(imageObject.isPresent()) {
            fullPath = imageObject.get().getImagePath();
        }
        return Files.readAllBytes(new File(fullPath).toPath());
    }
}