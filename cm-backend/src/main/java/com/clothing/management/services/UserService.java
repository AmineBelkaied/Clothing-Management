package com.clothing.management.services;

import com.clothing.management.entities.User;
import java.util.List;

public interface UserService {

    User addUser(User user);
    List<User> findAllUsers();
    User findByUserName(String userName);
    User updateUser(User user);
    void deleteAllUsersById(List<Integer> usersId);
}
