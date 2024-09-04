package com.clothing.management.controllers;

import com.clothing.management.entities.FbPage;
import com.clothing.management.services.FbPageService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("${api.prefix}/fb-pages")
@CrossOrigin
@Secured({"ROLE_ADMIN", "ROLE_USER"})
public class FbPageController {
    private final FbPageService fbPageService;

    public FbPageController(FbPageService fbPageService){
        this.fbPageService = fbPageService;
    }

    @GetMapping
    public ResponseEntity<List<FbPage>> getAllFbPages() {
        List<FbPage> fbPages = fbPageService.findAllFbPages();
        return new ResponseEntity<>(fbPages, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<FbPage> getFbPageById(@PathVariable Long id) {
        Optional<FbPage> fbPage = fbPageService.findFbPageById(id);
        return fbPage.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PostMapping
    public ResponseEntity<FbPage> createFbPage(@RequestBody FbPage fbPage) {
        FbPage createdFbPage = fbPageService.addFbPage(fbPage);
        return new ResponseEntity<>(createdFbPage, HttpStatus.CREATED);
    }

    @PutMapping
    public ResponseEntity<FbPage> updateFbPage(@RequestBody FbPage fbPage) {
        FbPage updatedFbPage = fbPageService.updateFbPage(fbPage);
        return new ResponseEntity<>(updatedFbPage, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFbPageById(@PathVariable Long id) {
        fbPageService.deleteFbPageById(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
