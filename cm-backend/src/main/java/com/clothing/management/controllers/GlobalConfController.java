package com.clothing.management.controllers;

import com.clothing.management.entities.GlobalConf;
import com.clothing.management.services.GlobalConfService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/globalConf")
public class GlobalConfController {

    private GlobalConfService globalConfService;

    @Autowired
    public GlobalConfController(GlobalConfService globalConfService) {
        this.globalConfService = globalConfService;
    }

    @GetMapping(path = "/get")
    public GlobalConf getGlobalConf() {
        System.out.println("globalConf0000000");
        GlobalConf globalConf = globalConfService.getGlobalConf();
        System.out.println("globalConf000");
        System.out.println(globalConf);
        return globalConf;
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

