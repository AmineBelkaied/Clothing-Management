package com.clothing.management.controllers;

import com.clothing.management.entities.User;
import com.clothing.management.services.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("${api.prefix}/users")
@CrossOrigin
@Secured("ROLE_ADMIN")
public class UserController {

    private final UserService userService;
    private static final Logger LOGGER = LoggerFactory.getLogger(UserController.class);

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        LOGGER.info("Fetching all users");
        try {
            List<User> users = userService.findAllUsers();
            LOGGER.info("Successfully fetched all users");
            return ResponseEntity.ok(users);
        } catch (Exception e) {
            LOGGER.error("Error fetching all users: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping
    public ResponseEntity<User> createUser(@RequestBody User user) {
        LOGGER.info("Creating user: {}", user);
        try {
            User createdUser = userService.addUser(user);
            LOGGER.info("Successfully created user with ID: {}", createdUser.getUserId());
            return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
        } catch (Exception e) {
            LOGGER.error("Error creating user: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping
    public ResponseEntity<User> updateUser(@RequestBody User user) {
        LOGGER.info("Updating user with ID: {}", user.getUserId());
        try {
            User updatedUser = userService.updateUser(user);
            LOGGER.info("Successfully updated user with ID: {}", updatedUser.getUserId());
            return ResponseEntity.ok(updatedUser);
        } catch (Exception e) {
            LOGGER.error("Error updating user with ID {}: ", user.getUserId(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/batch-delete")
    public ResponseEntity<Void> deleteUsersByIds(@RequestParam List<Integer> usersId) {
        LOGGER.info("Deleting users with IDs: {}", usersId);
        try {
            userService.deleteAllUsersById(usersId);
            LOGGER.info("Successfully deleted users with IDs: {}", usersId);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            LOGGER.error("Error deleting users with IDs {}: ", usersId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
