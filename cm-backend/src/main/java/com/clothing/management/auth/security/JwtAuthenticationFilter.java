package com.clothing.management.auth.security;

import com.clothing.management.auth.constant.JWTConstants;
import com.clothing.management.auth.mastertenant.config.DBContextHolder;
import com.clothing.management.auth.mastertenant.entity.MasterTenant;
import com.clothing.management.auth.mastertenant.service.MasterTenantService;
import com.clothing.management.auth.util.JwtTokenUtil;
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

    private final JwtUserDetailsService jwtUserDetailsService;
    private final JwtTokenUtil jwtTokenUtil;
    private final MasterTenantService masterTenantService;

    @Autowired
    public JwtAuthenticationFilter(JwtUserDetailsService jwtUserDetailsService,
                                   JwtTokenUtil jwtTokenUtil,
                                   MasterTenantService masterTenantService) {
        this.jwtUserDetailsService = jwtUserDetailsService;
        this.jwtTokenUtil = jwtTokenUtil;
        this.masterTenantService = masterTenantService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        if (shouldSkipFilter(request)) {
            chain.doFilter(request, response);
            return;
        }

        String header = request.getHeader(JWTConstants.HEADER_STRING);
        if (header == null || !header.startsWith(JWTConstants.TOKEN_PREFIX)) {
            logger.warn("JWT Token is missing or does not start with Bearer String");
            chain.doFilter(request, response);
            return;
        }

        String authToken = header.replace(JWTConstants.TOKEN_PREFIX, "");
        try {
            processToken(authToken, request);
        } catch (BadCredentialsException ex) {
            logger.error(ex.getMessage());
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid credentials");
            return;
        } catch (ExpiredJwtException ex) {
            logger.warn("Expired JWT token");
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Expired JWT token");
            return;
        } catch (SignatureException ex) {
            logger.error("Invalid JWT signature");
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid JWT signature");
            return;
        } catch (Exception ex) {
            logger.error(ex.getMessage());
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Error processing JWT");
            return;
        }

        chain.doFilter(request, response);
    }

    private void processToken(String token, HttpServletRequest request) {
        String username = jwtTokenUtil.getUsernameFromToken(token);
        String audience = jwtTokenUtil.getAudienceFromToken(token); // tenant or client ID
        MasterTenant masterTenant = masterTenantService.findByTenantName(audience);

        if (masterTenant == null) {
            logger.error(audience);
            throw new BadCredentialsException("Invalid tenant and user.");
        }

        DBContextHolder.setCurrentDb(masterTenant.getDbName());

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = jwtUserDetailsService.loadUserByUsername(username);
            if (jwtTokenUtil.validateToken(token, userDetails)) {
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                logger.info("authenticated user " + username + ", setting security context");
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }
    }

    private boolean shouldSkipFilter(HttpServletRequest request) {
        String requestUri = request.getRequestURI();
        return requestUri.startsWith("/tenant");
    }
}
