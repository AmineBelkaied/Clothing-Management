package com.clothing.management.controllers;

import com.clothing.management.entities.Size;
import com.clothing.management.services.SizeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("size")
@CrossOrigin
public class SizeController {

    @Autowired
    SizeService sizeService;

    @GetMapping(path = "/findAll")
    public List<Size> findAllSizes() {
        return sizeService.findAllSizes();
    }

    @GetMapping(path = "/findById/{id}")
    public Optional<Size> findByIdSize(@PathVariable Long idSize) {
        return sizeService.findSizeById(idSize);
    }

    @PostMapping(value = "/add" , produces = "application/json")
    public Size addSize(@RequestBody  Size size) throws Exception {
        return sizeService.addSize(size);
    }

    @PutMapping(value = "/update" , produces = "application/json")
    public Size updateSize(@RequestBody Size size) throws Exception {
        return sizeService.updateSize(size);
    }

    @DeleteMapping(value = "/deleteById/{idSize}")
    public void deleteSizeById(@PathVariable Long idSize) {
        sizeService.deleteSizeById(idSize);
    }
}
