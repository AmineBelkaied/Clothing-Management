package com.clothing.management.controllers;

import com.clothing.management.entities.User;
import com.clothing.management.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("user")
@CrossOrigin
@Secured("ROLE_ADMIN")
public class UserController {

    @Autowired
    UserService userService;

    @GetMapping(path = "/findAll")
    public List<User> findAllUsers() {
        return userService.findAllUsers();
    }

    @PostMapping(path = "/add")
    public User addUser(@RequestBody User user) {
        return userService.addUser(user);
    }

    @PutMapping(path = "/update")
    public User updateUser(@RequestBody User user) {
        return userService.updateUser(user);
    }

    @DeleteMapping(path = "/deleteAllById/{usersId}")
    public void deleteAllCustomersById(@PathVariable List<Integer> usersId) {
        userService.deleteAllUsersById(usersId);
    }

/*    @DeleteMapping(path = "/delete")
    public void deleteUser(@RequestBody User user) {
        userService.deleteUser(user);
    }

    @DeleteMapping(path = "/deleteById/{userId}")
    public void deleteUserById(@PathVariable Integer userId) {
        userService.deleteUserById(userId);
    }*/

}
