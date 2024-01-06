package com.clothing.management.auth.mastertenant.service;

import com.clothing.management.auth.mastertenant.entity.MasterUser;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MasterUserService {
    MasterUser save(MasterUser masterUser);
    List<MasterUser> findAllMasterUsers();
    MasterUser authenticate(String userName, String password);
}
