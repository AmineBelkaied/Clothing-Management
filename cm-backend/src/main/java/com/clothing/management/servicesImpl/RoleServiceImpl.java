package com.clothing.management.servicesImpl;

import com.clothing.management.entities.Role;
import com.clothing.management.repository.IRoleRepository;
import com.clothing.management.services.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RoleServiceImpl implements RoleService {

    @Autowired
    IRoleRepository roleRepository;

    @Override
    public List<Role> findAllRoles() {
        return roleRepository.findAll();
    }

    @Override
    public Role addRole(Role Role) {
        return roleRepository.save(Role);
    }

    @Override
    public Role updateRole(Role Role) {
        return roleRepository.save(Role);
    }
}
