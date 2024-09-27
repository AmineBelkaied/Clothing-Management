package com.clothing.management.servicesImpl;

import com.clothing.management.auth.mastertenant.config.DBContextHolder;
import com.clothing.management.entities.User;
import com.clothing.management.repository.UserRepository;
import com.clothing.management.services.UserService;
import org.hibernate.context.spi.CurrentTenantIdentifierResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserServiceImpl.class);

    private final UserRepository userRepository;
    private final CurrentTenantIdentifierResolver tenantResolver;

    public UserServiceImpl(UserRepository userRepository, CurrentTenantIdentifierResolver tenantResolver) {
        this.userRepository = userRepository;
        this.tenantResolver = tenantResolver;
    }

    @Override
    public User addUser(User user) {
        if (user.getPassword() != null) {
            LOGGER.debug("Encoding password for user: {}", user.getUserName());
            BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
            String encodedPassword = bCryptPasswordEncoder.encode(user.getPassword());
            LOGGER.debug("Encoded password for user: {}", encodedPassword);
            user.setPassword(encodedPassword);
            user.setEnabled(true);
        }
        User savedUser = userRepository.save(user);
        LOGGER.info("User added with ID: {}", savedUser.getUserId());
        return savedUser;
    }

    @Override
    public List<User> findAllUsers() {
        List<User> users = userRepository.findAll();
        LOGGER.info("Fetched {} users", users.size());
        return users;
    }

    @Override
    public User findByUserName(String userName) {
        User user = userRepository.findByUserName(userName);
        if (user != null) {
            LOGGER.info("Found user with username: {}", userName);
        } else {
            LOGGER.warn("No user found with username: {}", userName);
        }
        return user;
    }

    @Override
    public User updateUser(User user) {
        LOGGER.debug("Updating user details: {}", user);
        if (user.getPassword() != null) {
            LOGGER.debug("Encoding new password for user: {}", user.getUserName());
            BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
            String encodedPassword = bCryptPasswordEncoder.encode(user.getPassword());
            LOGGER.debug("Encoded password for user: {}", encodedPassword);
            user.setPassword(encodedPassword);
        }
        User updatedUser = userRepository.save(user);
        LOGGER.info("User updated with ID: {}", updatedUser.getUserId());
        return updatedUser;
    }

    @Override
    public void deleteAllUsersById(List<Integer> usersId) {
        usersId.forEach(userId -> {
            LOGGER.info("Deleting user with ID: {}", userId);
            try {
                userRepository.deleteById(userId);
                LOGGER.info("Successfully deleted user with ID: {}", userId);
            } catch (Exception e) {
                LOGGER.error("Error deleting user with ID: {}", userId, e);
            }
        });
    }
}
