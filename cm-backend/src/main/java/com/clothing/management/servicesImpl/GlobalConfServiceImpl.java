package com.clothing.management.servicesImpl;


import com.clothing.management.entities.GlobalConf;

import com.clothing.management.repository.IFbPageRepository;
import com.clothing.management.repository.IGlobalConfRepository;
import com.clothing.management.services.GlobalConfService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class GlobalConfServiceImpl implements GlobalConfService {
    public IGlobalConfRepository globalConfRepository;

    @Autowired
    public GlobalConfServiceImpl(IGlobalConfRepository globalConfRepository) {
        this.globalConfRepository = globalConfRepository;
    }
    @Override
    public GlobalConf getGlobalConf() {
        return globalConfRepository.findAll().stream().findFirst().orElse(null);
    }

    @Override
    public void updateGlobalConf(GlobalConf globalConf) {
        globalConfRepository.save(globalConf);
    }
}
