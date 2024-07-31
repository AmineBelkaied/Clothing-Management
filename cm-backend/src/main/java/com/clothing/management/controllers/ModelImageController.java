package com.clothing.management.controllers;

import com.clothing.management.models.ResponseMessage;
import com.clothing.management.services.ModelImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("${api.prefix}/images")
@CrossOrigin
@Secured({"ROLE_ADMIN", "ROLE_USER"})
public class ModelImageController {

    private final ModelImageService modelImageService;

    @Autowired
    public ModelImageController(ModelImageService modelImageService) {
        this.modelImageService = modelImageService;
    }

    @PostMapping("/upload/{modelId}")
    public ResponseEntity<ResponseMessage> uploadImage(@RequestParam("file") MultipartFile file, @PathVariable Long modelId) {
        try {
            modelImageService.uploadImage(file, modelId);
            String message = "Uploaded the file successfully: " + file.getOriginalFilename();
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseMessage(message, file.getBytes()));
        } catch (IOException e) {
            String message = "Could not upload the file: " + file.getOriginalFilename() + ". Error: " + e.getMessage();
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(new ResponseMessage(message, null));
        }
    }

    @GetMapping("/download/{modelId}")
    public ResponseEntity<byte[]> downloadImage(@PathVariable Long modelId) {
        try {
            byte[] image = modelImageService.downloadImage(modelId);
            return ResponseEntity.ok()
                    .contentType(MediaType.IMAGE_PNG)  // Adjust to the appropriate MIME type if necessary
                    .body(image);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);  // Return 404 if the image is not found
        }
    }
}
