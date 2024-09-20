package com.clothing.management.auth.security;

import com.clothing.management.auth.constant.JWTConstants;
import com.clothing.management.auth.mastertenant.config.DBContextHolder;
import com.clothing.management.auth.mastertenant.entity.MasterTenant;
import com.clothing.management.auth.mastertenant.service.MasterTenantService;
import com.clothing.management.auth.util.JwtTokenUtil;
import com.clothing.management.exceptions.custom.others.UserNotAuthenticatedException;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    @Autowired
    private JwtUserDetailsService jwtUserDetailsService;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private MasterTenantService masterTenantService;

    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, FilterChain filterChain) throws ServletException, IOException {
        String header = httpServletRequest.getHeader(JWTConstants.HEADER_STRING);

        if (shouldSkipFilter(httpServletRequest)) {
            filterChain.doFilter(httpServletRequest, httpServletResponse);
            return;
        }

        if (header != null && header.startsWith(JWTConstants.TOKEN_PREFIX)) {
            String authToken = header.replace(JWTConstants.TOKEN_PREFIX, "");
            try {
                String userName = extractUserNameAndTenant(authToken);

                if (userName != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                    setAuthentication(httpServletRequest, userName, authToken);
                }
            } catch (UserNotAuthenticatedException ex) {
                logger.error("User not authenticated: {}", ex.getMessage());
                httpServletResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED, ex.getMessage());
                return;
            } catch (Exception ex) {
                logger.error("Authentication error: {}", ex.getMessage(), ex);
                httpServletResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Authentication failed.");
                return;
            }
        } else {
            logger.warn("Couldn't find bearer string, will ignore the header");
        }

        filterChain.doFilter(httpServletRequest, httpServletResponse);
    }

    private String extractUserNameAndTenant(String token) {
        try {
            String userName = jwtTokenUtil.getUsernameFromToken(token);
            String tenant = jwtTokenUtil.getAudienceFromToken(token);
            MasterTenant masterTenant = masterTenantService.findByTenantName(tenant);

            if (masterTenant == null) {
                logger.error("Invalid tenant: {}", tenant);
                throw new BadCredentialsException("Invalid tenant.");
            }

            DBContextHolder.setCurrentDb(masterTenant.getDbName());
            return userName;
        } catch (IllegalArgumentException ex) {
            logger.error("Error getting username from token: {}", ex.getMessage(), ex);
            throw new IllegalArgumentException("Invalid token: " + ex.getMessage(), ex);
        } catch (ExpiredJwtException ex) {
            logger.error("Token expired: {}", ex.getMessage(), ex);
            throw new ExpiredJwtException(null, null, "Token expired: " + ex.getMessage(), ex);
        } catch (SignatureException ex) {
            logger.error("Token signature validation failed: {}", ex.getMessage(), ex);
            throw new SignatureException("Token signature validation failed: " + ex.getMessage(), ex);
        }
    }

    private void setAuthentication(HttpServletRequest httpServletRequest, String username, String authToken) {
        UserDetails userDetails = jwtUserDetailsService.loadUserByUsername(username);
        if (jwtTokenUtil.validateToken(authToken, userDetails)) {
            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(httpServletRequest));
            logger.info("Authenticated user: {}", username);
            SecurityContextHolder.getContext().setAuthentication(authentication);
        } else {
            logger.warn("Token validation failed for user: {}", username);
        }
    }

    private boolean shouldSkipFilter(HttpServletRequest request) {
        // Example logic to skip filter for login requests
        String requestUri = request.getRequestURI();
        return requestUri.equalsIgnoreCase("/api/auth/login") || requestUri.equalsIgnoreCase("/swagger-ui/index.html");
    }
}
