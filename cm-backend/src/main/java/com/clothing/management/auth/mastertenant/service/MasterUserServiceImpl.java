package com.clothing.management.auth.mastertenant.service;

import com.clothing.management.auth.mastertenant.entity.MasterUser;
import com.clothing.management.auth.mastertenant.repository.MasterUserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MasterUserServiceImpl implements MasterUserService{

    private static final Logger LOG = LoggerFactory.getLogger(MasterUserServiceImpl.class);

    @Autowired
    MasterUserRepository masterUserRepository;

    @Override
    public MasterUser save(MasterUser masterUser) {
        return masterUserRepository.save(masterUser);
    }

    @Override
    public List<MasterUser> findAllMasterUsers() {
        return masterUserRepository.findAll();
    }

    @Override
    public MasterUser authenticate(String userName, String password) {
        return masterUserRepository.authenticate(userName, password);
    }
}
