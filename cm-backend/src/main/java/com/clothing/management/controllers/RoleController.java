package com.clothing.management.controllers;

import com.clothing.management.entities.Role;
import com.clothing.management.entities.User;
import com.clothing.management.services.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("role")
@CrossOrigin
@Secured("ROLE_ADMIN")
public class RoleController {

    @Autowired
    RoleService roleService;

    @GetMapping(path = "/findAll")
    public List<Role> findAllRoles() {
        return roleService.findAllRoles();
    }

    @PostMapping(path = "/add")
    public Role addRole(@RequestBody Role role) {
        return roleService.addRole(role);
    }

    @PutMapping(path = "/update")
    public Role updateRole(@RequestBody Role role) {
        return roleService.updateRole(role);
    }

}
