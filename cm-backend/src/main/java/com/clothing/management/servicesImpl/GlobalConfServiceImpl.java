package com.clothing.management.servicesImpl;

import com.clothing.management.entities.GlobalConf;
import com.clothing.management.repository.IGlobalConfRepository;
import com.clothing.management.services.GlobalConfService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GlobalConfServiceImpl implements GlobalConfService {

    private static final Logger LOGGER = LoggerFactory.getLogger(GlobalConfServiceImpl.class);

    private final IGlobalConfRepository globalConfRepository;

    @Autowired
    public GlobalConfServiceImpl(IGlobalConfRepository globalConfRepository) {
        this.globalConfRepository = globalConfRepository;
    }

    @Override
    public GlobalConf getGlobalConf() {
        GlobalConf globalConf = globalConfRepository.findAll().stream().findFirst().orElse(null);
        return globalConf;
    }

    @Override
    public void updateGlobalConf(GlobalConf globalConf) {
        globalConfRepository.save(globalConf);
        LOGGER.info("Global configuration updated successfully.");
    }
}
