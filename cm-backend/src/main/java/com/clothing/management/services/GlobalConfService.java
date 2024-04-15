package com.clothing.management.services;

import com.clothing.management.entities.GlobalConf;
import org.springframework.http.ResponseEntity;

import java.util.Optional;

public interface GlobalConfService {
    public GlobalConf getGlobalConf();
    public void updateGlobalConf(GlobalConf globalConf);

}

