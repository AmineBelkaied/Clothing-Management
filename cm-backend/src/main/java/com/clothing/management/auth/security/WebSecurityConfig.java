package com.clothing.management.auth.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.Arrays;

@Configuration
@EnableMethodSecurity(securedEnabled = true, prePostEnabled = true)
// (securedEnabled = true,
// jsr250Enabled = true,
// prePostEnabled = true) // by default
    public class WebSecurityConfig { // extends WebSecurityConfigurerAdapter {
        @Autowired
        JwtUserDetailsService jwtUserDetailsService;

        @Autowired
        JwtAuthenticationFilter jwtAuthenticationFilter;

        @Autowired
        private JwtAuthenticationEntryPoint unauthorizedHandler;

        @Bean
        public JwtAuthenticationFilter authenticationTokenFilterBean() throws Exception {
            return new JwtAuthenticationFilter();
        }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(jwtUserDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

        @Bean
        public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
            return authConfig.getAuthenticationManager();
        }

        @Bean
        public PasswordEncoder passwordEncoder() {
            return new BCryptPasswordEncoder();
        }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(request -> request.requestMatchers("/api/auth/**", "/tenant/**")
                        .permitAll().anyRequest().authenticated())
                .sessionManagement(manager -> manager.sessionCreationPolicy(SessionCreationPolicy.ALWAYS))
                .authenticationProvider(authenticationProvider()).addFilterBefore(
                        jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }


        @Bean
        public FilterRegistrationBean platformCorsFilter() {
            UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
            CorsConfiguration configAutenticacao = new CorsConfiguration();
            configAutenticacao.setAllowCredentials(true);
            configAutenticacao.setAllowedOriginPatterns(Arrays.asList("*"));
            configAutenticacao.addAllowedHeader("Authorization");
            configAutenticacao.addAllowedHeader("Content-Type");
            configAutenticacao.addAllowedHeader("Accept");
            configAutenticacao.addAllowedMethod("POST");
            configAutenticacao.addAllowedMethod("GET");
            configAutenticacao.addAllowedMethod("DELETE");
            configAutenticacao.addAllowedMethod("PUT");
            configAutenticacao.addAllowedMethod("OPTIONS");
            configAutenticacao.setMaxAge(3600L);
            source.registerCorsConfiguration("/**", configAutenticacao);
            FilterRegistrationBean bean = new FilterRegistrationBean(new CorsFilter(source));
            bean.setOrder(-110);
            return bean;
        }
    }
