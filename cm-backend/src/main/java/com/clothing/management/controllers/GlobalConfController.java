package com.clothing.management.controllers;

import com.clothing.management.entities.GlobalConf;
import com.clothing.management.services.GlobalConfService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("${api.prefix}/global-config")
public class GlobalConfController {
    private final GlobalConfService globalConfService;

    public GlobalConfController(GlobalConfService globalConfService) {
        this.globalConfService = globalConfService;
    }

    @GetMapping
    public ResponseEntity<GlobalConf> getGlobalConfig() {
        GlobalConf globalConf = globalConfService.getGlobalConf();
        if (globalConf != null) {
            return new ResponseEntity<>(globalConf, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping
    public ResponseEntity<Void> upsertGlobalConfig(@RequestBody GlobalConf globalConf) {
        try {
            globalConfService.updateGlobalConf(globalConf);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
}
