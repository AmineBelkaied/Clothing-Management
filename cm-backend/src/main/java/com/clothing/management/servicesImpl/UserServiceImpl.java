package com.clothing.management.servicesImpl;

import com.clothing.management.auth.controller.AuthenticationController;
import com.clothing.management.auth.mastertenant.config.DBContextHolder;
import com.clothing.management.entities.User;
import com.clothing.management.repository.UserRepository;
import com.clothing.management.services.UserService;
import org.hibernate.context.spi.CurrentTenantIdentifierResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    private static final Logger LOGGER = LoggerFactory.getLogger(AuthenticationController.class);
    @Autowired
    UserRepository userRepository;

    @Autowired
    CurrentTenantIdentifierResolver tenantResolver;

    @Override
    public User addUser(User user) {
        LOGGER.info("UserServiceImpl - DBContextHolder.getCurrentDb() " + DBContextHolder.getCurrentDb());
        if(user.getPassword() != null) {
            LOGGER.info("user.getPassword() " + user.getPassword());
            BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
            String encodedPassword = bCryptPasswordEncoder.encode(user.getPassword());
            LOGGER.info("encodedPassword " + encodedPassword);
            user.setPassword(encodedPassword);
            user.setEnabled(true);
        }
        return userRepository.save(user);
    }

    @Override
    public List<User> findAllUsers() {
        LOGGER.info("UserServiceImpl - DBContextHolder.getCurrentDb() " + DBContextHolder.getCurrentDb());
        return userRepository.findAll();
    }

    @Override
    public User findByUserName(String userName) {
        return userRepository.findByUserName(userName);
    }

    @Override
    public User updateUser(User user) {
        LOGGER.info("UserServiceImpl - DBContextHolder.getCurrentDb() " + DBContextHolder.getCurrentDb());
        LOGGER.info("UserServiceImpl - updateUser " + user.toString());
        if(user.getPassword() != null) {
            LOGGER.info("user.getPassword() " + user.getPassword());
            BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
            String encodedPassword = bCryptPasswordEncoder.encode(user.getPassword());
            LOGGER.info("encodedPassword " + encodedPassword);
            user.setPassword(encodedPassword);
        }
        return userRepository.save(user);
    }

    @Override
    public void deleteAllUsersById(List<Integer> usersId) {
        LOGGER.info("UserServiceImpl - DBContextHolder.getCurrentDb() " + DBContextHolder.getCurrentDb());
        usersId.forEach(userId -> {
            userRepository.deleteById(userId);
        });
    }
}
