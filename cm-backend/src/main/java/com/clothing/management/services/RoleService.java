package com.clothing.management.services;

import com.clothing.management.entities.Role;

import java.util.List;

public interface RoleService {

    Role addRole(Role role);
    List<Role> findAllRoles();
    Role updateRole(Role role);
}
