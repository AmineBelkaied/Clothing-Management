package com.clothing.management.controllers;

import com.clothing.management.entities.Role;
import com.clothing.management.services.RoleService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("${api.prefix}/roles")
@CrossOrigin
@Secured("ROLE_ADMIN")
public class RoleController {

    private final RoleService roleService;
    private static final Logger LOGGER = LoggerFactory.getLogger(RoleController.class);

    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }

    @GetMapping
    public ResponseEntity<List<Role>> getAllRoles() {
        LOGGER.info("Fetching all roles");
        try {
            List<Role> roles = roleService.findAllRoles();
            LOGGER.info("Successfully fetched {} roles", roles.size());
            return ResponseEntity.ok(roles);
        } catch (Exception e) {
            LOGGER.error("Error fetching roles: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping
    public ResponseEntity<Role> createRole(@RequestBody Role role) {
        LOGGER.info("Creating new role: {}", role);
        try {
            Role createdRole = roleService.addRole(role);
            LOGGER.info("Successfully created role: {}", createdRole);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdRole);
        } catch (Exception e) {
            LOGGER.error("Error creating role: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping
    public ResponseEntity<Role> updateRole(@RequestBody Role role) {
        LOGGER.info("Updating role: {}", role);
        try {
            Role updatedRole = roleService.updateRole(role);
            if (updatedRole != null) {
                LOGGER.info("Successfully updated role: {}", updatedRole);
                return ResponseEntity.ok(updatedRole);
            } else {
                LOGGER.warn("Role not found for update: {}", role);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
        } catch (Exception e) {
            LOGGER.error("Error updating role: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
