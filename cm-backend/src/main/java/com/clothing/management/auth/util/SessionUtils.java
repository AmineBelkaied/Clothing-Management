package com.clothing.management.auth.util;

import com.clothing.management.entities.User;
import com.clothing.management.exceptions.custom.others.UserNotAuthenticatedException;
import com.clothing.management.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.naming.Context;

@Component
public class SessionUtils {

    private final UserRepository userRepository;
    private final static String SYSTEM_USER = "SYSTEM";

    public SessionUtils(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            Object principal = authentication.getPrincipal();
            if (principal instanceof UserDetails) {
                UserDetails userDetails = (UserDetails) principal;
                return userRepository.findByUserName(userDetails.getUsername());
            } else {
                throw new UserNotAuthenticatedException("Principal is not an instance of UserDetails.");
            }
        }
        return userRepository.findByUserName(SYSTEM_USER);
   }
}
