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

    @Autowired
    private JwtUserDetailsService jwtUserDetailsService;
    @Autowired
    private JwtTokenUtil jwtTokenUtil;
    @Autowired
    MasterTenantService masterTenantService;

    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, FilterChain filterChain) throws ServletException, IOException {
            String header = httpServletRequest.getHeader(JWTConstants.HEADER_STRING);
            String username = null;
            String audience = null; //tenantOrClientId
            String authToken = null;
            if (header != null && header.startsWith(JWTConstants.TOKEN_PREFIX)) {
                authToken = header.replace(JWTConstants.TOKEN_PREFIX,"");
                try {
                    username = jwtTokenUtil.getUsernameFromToken(authToken);
                    audience = jwtTokenUtil.getAudienceFromToken(authToken);
                    MasterTenant masterTenant = masterTenantService.findByTenantName(audience);
                    if(null == masterTenant){
                        logger.error("An error during getting tenant name");
                        throw new BadCredentialsException("Invalid tenant and user.");
                    }
                    DBContextHolder.setCurrentDb(masterTenant.getDbName());
                } catch (IllegalArgumentException ex) {
                    logger.error("An error during getting username from token", ex);
                } catch (ExpiredJwtException ex) {
                    logger.warn("The token is expired and not valid anymore");
                } catch(SignatureException ex){
                    logger.error("Authentication Failed. Username or Password not valid.",ex);
                }
            } else {
                logger.warn("Couldn't find bearer string, will ignore the header");
            }
            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = jwtUserDetailsService.loadUserByUsername(username);
                if (jwtTokenUtil.validateToken(authToken, userDetails)) {
                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(httpServletRequest));
                    //logger.info("authenticated user " + username + ", setting security context");
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            }
        filterChain.doFilter(httpServletRequest, httpServletResponse);
    }

    private boolean shouldSkipFilter(HttpServletRequest request) {
        // Add your logic to determine whether to skip the filter for this request
        // For example, check the request URI
        String requestUri = request.getRequestURI();
        return requestUri.startsWith("/tenant");
    }
}
