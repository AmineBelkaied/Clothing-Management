package com.clothing.management.services;

import com.clothing.management.entities.GlobalConf;
import org.springframework.http.ResponseEntity;

import java.util.Optional;

public interface GlobalConfService {
    GlobalConf getGlobalConf();
    void updateGlobalConf(GlobalConf globalConf);
}

