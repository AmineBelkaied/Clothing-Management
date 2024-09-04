package com.clothing.management.services;

import com.clothing.management.entities.GlobalConf;
public interface GlobalConfService {
    GlobalConf getGlobalConf();
    void updateGlobalConf(GlobalConf globalConf);

}

