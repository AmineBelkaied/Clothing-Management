package com.clothing.management.auth.util;

import com.clothing.management.entities.User;
import com.clothing.management.exceptions.UserNotAuthenticatedException;
import com.clothing.management.repository.UserRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Component
public class SessionUtils {

    private final UserRepository userRepository;
    private final static String SYSTEM_USER = "SYSTEM";

    public SessionUtils(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User getCurrentUser() {
        if(SecurityContextHolder.getContext().getAuthentication() != null) {
            UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            if (userDetails != null) {
                return userRepository.findByUserName(userDetails.getUsername());
            } else {
                throw new UserNotAuthenticatedException("User details are null. User not authenticated.");
            }
        }
        return userRepository.findByUserName(SYSTEM_USER);
    }
}
