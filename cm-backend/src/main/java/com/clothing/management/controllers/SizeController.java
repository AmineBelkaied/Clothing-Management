package com.clothing.management.controllers;

import com.clothing.management.entities.Size;
import com.clothing.management.services.SizeService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("${api.prefix}/sizes")
@CrossOrigin
@Secured({"ROLE_ADMIN", "ROLE_USER"})
public class SizeController {
    private final SizeService sizeService;
    public SizeController (SizeService sizeService){
        this.sizeService = sizeService;
    }

    @GetMapping
    public ResponseEntity<List<Size>> getAllSizes() {
        try {
            List<Size> sizes = sizeService.findAllSizes();
            return ResponseEntity.ok(sizes);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Size> getSizeById(@PathVariable Long id) {
        try {
            Optional<Size> size = sizeService.findSizeById(id);
            return size.isPresent() ? (ResponseEntity<Size>) ResponseEntity.ok() : ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping
    public ResponseEntity<Size> createSize(@RequestBody Size size) {
        try {
            Size createdSize = sizeService.addSize(size);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdSize);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping
    public ResponseEntity<Size> updateSize(@RequestBody Size size) {
        try {
            Size updatedSize = sizeService.updateSize(size);
            return updatedSize != null ? ResponseEntity.ok(updatedSize) : ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSizeById(@PathVariable Long id) {
        try {
            sizeService.deleteSizeById(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
