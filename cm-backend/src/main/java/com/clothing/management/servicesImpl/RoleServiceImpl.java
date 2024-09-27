package com.clothing.management.servicesImpl;

import com.clothing.management.entities.Role;
import com.clothing.management.repository.IRoleRepository;
import com.clothing.management.services.RoleService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RoleServiceImpl implements RoleService {

    private static final Logger LOGGER = LoggerFactory.getLogger(RoleServiceImpl.class);

    private final IRoleRepository roleRepository;

    public RoleServiceImpl(IRoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Override
    public List<Role> findAllRoles() {
        List<Role> roles = roleRepository.findAll();
        LOGGER.info("Fetched {} roles", roles.size());
        return roles;
    }

    @Override
    public Role addRole(Role role) {
        Role savedRole = roleRepository.save(role);
        LOGGER.info("Role added with ID: {}", savedRole.getId());
        return savedRole;
    }

    @Override
    public Role updateRole(Role role) {
        Role updatedRole = roleRepository.save(role);
        LOGGER.info("Role updated with ID: {}", updatedRole.getId());
        return updatedRole;
    }
}
