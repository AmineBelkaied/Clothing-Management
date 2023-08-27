package com.clothing.management.controllers;

import java.io.IOException;

import com.clothing.management.models.ResponseMessage;
import com.clothing.management.services.ModelImageService;
import com.clothing.management.servicesImpl.ModelImageServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


@RestController
@RequestMapping("files")
@CrossOrigin
public class ModelImageController {

    @Autowired
    private ModelImageService modelImageService;

    @ResponseStatus(value = HttpStatus.OK)
    @PostMapping("/upload/{modelId}")
    public ResponseEntity<ResponseMessage> uploadImage(@RequestParam("modalImage") MultipartFile file, @PathVariable Long modelId) throws IOException {
        String message = "";
        try {
            modelImageService.uploadImage(file, modelId);

            message = "Uploaded the file successfully: " + file.getOriginalFilename();
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseMessage(message, file.getBytes()));
        } catch (Exception e) {
            message = "Could not upload the file: " + file.getOriginalFilename() + ". Error: " + e.getMessage();
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(new ResponseMessage(message, file.getBytes()));
        }
    }

    @GetMapping("/download/{modelId}")
    public ResponseEntity<byte[]> downloadImage(@PathVariable Long modelId) throws IOException {
        byte[] image = modelImageService.downloadImage(modelId);
        return ResponseEntity.status(HttpStatus.OK).contentType(MediaType.valueOf("image/png")).body(image);
    }
}