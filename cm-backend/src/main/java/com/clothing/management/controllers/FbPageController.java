package com.clothing.management.controllers;

import com.clothing.management.entities.FbPage;
import com.clothing.management.services.FbPageService;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("fbPage")
@CrossOrigin
@Secured({"ROLE_ADMIN", "ROLE_USER"})
public class FbPageController {
    private final FbPageService fbPageService;

    public FbPageController(FbPageService fbPageService){
        this.fbPageService = fbPageService;
    }

    @GetMapping(path = "/findAll")
    public List<FbPage> findAllFbPages() {
        return fbPageService.findAllFbPages();
    }

    @GetMapping(path = "/findById/{id}")
    public Optional<FbPage> findByIdFbPage(@PathVariable Long id) {
        return fbPageService.findFbPageById(id);
    }

    @PostMapping(value = "/add" , produces = "application/json")
    public FbPage addFbPage(@RequestBody  FbPage FbPage) {
        return fbPageService.addFbPage(FbPage);
    }

    @PutMapping(value = "/update" , produces = "application/json")
    public FbPage updateFbPage(@RequestBody FbPage FbPage) {
        return fbPageService.updateFbPage(FbPage);
    }

    @DeleteMapping(value = "/delete" , produces = "application/json")
    public void deleteFbPage(@RequestBody FbPage FbPage) {
        fbPageService.deleteFbPage(FbPage);
    }

    @DeleteMapping(value = "/deleteById/{idFbPage}")
    public void deleteSizeById(@PathVariable Long idFbPage) {
        fbPageService.deleteFbPageById(idFbPage);
    }
}
