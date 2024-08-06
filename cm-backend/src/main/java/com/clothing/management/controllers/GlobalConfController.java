package com.clothing.management.controllers;

import com.clothing.management.entities.GlobalConf;
import com.clothing.management.services.GlobalConfService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/globalConf")
public class GlobalConfController {
    private final GlobalConfService globalConfService;

    public GlobalConfController(GlobalConfService globalConfService) {
        this.globalConfService = globalConfService;
    }

    @GetMapping(path = "/get")
    public GlobalConf getGlobalConf() {
        return globalConfService.getGlobalConf();
    }

    @PutMapping(value = "/add" , produces = "application/json")
    public ResponseEntity<Void> addGlobalConf(@RequestBody GlobalConf globalConf) {
        globalConfService.updateGlobalConf(globalConf);
        return ResponseEntity.ok().build();
    }

    @PutMapping(value = "/update" , produces = "application/json")
    public ResponseEntity<Void> updateGlobalConf(@RequestBody GlobalConf globalConf) {
        globalConfService.updateGlobalConf(globalConf);
        return ResponseEntity.ok().build();
    }
}

