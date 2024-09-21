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

    @Autowired
    private JwtUserDetailsService jwtUserDetailsService;
    @Autowired
    private JwtTokenUtil jwtTokenUtil;
    @Autowired
    MasterTenantService masterTenantService;

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
            } catch (UserNotAuthenticatedException | ExpiredJwtException ex) {
                logger.error("Authentication failed: {}");
                httpServletResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED, ex.getMessage());
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

            if (null == masterTenant) {
                logger.error("An error during getting tenant name");
                throw new BadCredentialsException("Invalid tenant and user.");
            }

            DBContextHolder.setCurrentDb(masterTenant.getDbName());
            return userName;
        } catch (IllegalArgumentException ex) {
            logger.error("An error during getting username from token", ex);
            throw new IllegalArgumentException(ex.getMessage());
        } catch (ExpiredJwtException ex) {
            logger.error("The token is expired and not valid anymore");
            throw ex;
        } catch (SignatureException ex) {
            logger.error("Authentication Failed. Username or Password not valid.", ex);
            throw new SignatureException(ex.getMessage());
        }
    }

    private void setAuthentication(HttpServletRequest httpServletRequest, String username, String authToken) {
        UserDetails userDetails = jwtUserDetailsService.loadUserByUsername(username);
        if (jwtTokenUtil.validateToken(authToken, userDetails)) {
            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(httpServletRequest));
            //logger.info("authenticated user " + username + ", setting security context");
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }
    }

    private boolean shouldSkipFilter(HttpServletRequest request) {
        // Add your logic to determine whether to skip the filter for this request
        // For example, check the request URI
        String requestUri = request.getRequestURI();
        return requestUri.equalsIgnoreCase("/api/auth/login");
    }
}
