package com.clothing.management.services;

import com.clothing.management.entities.ModelImage;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface ModelImageService {

    ModelImage uploadImage(MultipartFile file, Long modelId);
    byte[] downloadImage(Long modelId) throws IOException;
}
