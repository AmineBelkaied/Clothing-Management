package com.clothing.management.controllers;

import com.clothing.management.dto.ModelDeleteDTO;
import com.clothing.management.entities.FbPage;
import com.clothing.management.services.FbPageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private static final Logger LOGGER = LoggerFactory.getLogger(FbPageController.class);

    public FbPageController(FbPageService fbPageService) {
        this.fbPageService = fbPageService;
    }

    @GetMapping
    public ResponseEntity<List<FbPage>> getAllFbPages() {
        LOGGER.info("Fetching all Facebook pages.");
        List<FbPage> fbPages = fbPageService.findAllFbPages();
        return new ResponseEntity<>(fbPages, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<FbPage> getFbPageById(@PathVariable Long id) {
        LOGGER.info("Fetching Facebook page with ID: {}", id);
        Optional<FbPage> fbPage = fbPageService.findFbPageById(id);
        return fbPage.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                .orElseGet(() -> {
                    LOGGER.warn("Facebook page with ID {} not found.", id);
                    return new ResponseEntity<>(HttpStatus.NOT_FOUND);
                });
    }

    @PostMapping
    public ResponseEntity<FbPage> saveFbPage(@RequestBody FbPage fbPage) {
        LOGGER.info("Saving a new Facebook page: {}", fbPage.getName());
        FbPage createdFbPage = fbPageService.saveFbPage(fbPage);
        return new ResponseEntity<>(createdFbPage, HttpStatus.CREATED);
    }

    @GetMapping("/check-fb-page-usage/{id}")
    public ResponseEntity<Long> checkFbPageUsage(@PathVariable Long id) {
        LOGGER.info("Checking size with id: {}", id);
        Long fbPageUsage = fbPageService.checkFbPageUsage(id);
        LOGGER.info("Size with id used : {} .", fbPageUsage);
        return new ResponseEntity<>(fbPageUsage, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFbPageById(@PathVariable Long id) {
        LOGGER.info("Deleting Facebook page with ID: {}", id);
        fbPageService.deleteFbPageById(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
