package com.clothing.management;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Serve static content from the 'static' folder in the classpath
        registry
                .addResourceHandler("/static/**")
                .addResourceLocations("classpath:/static/")
                .resourceChain(true); // Enable resource chain for caching and compression

        // Serve favicon and other static resources directly
        registry
                .addResourceHandler("/favicon.ico")
                .addResourceLocations("classpath:/static/favicon.ico");
    }

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        // Forward all requests that do not match a static resource to Angular's index.html
        registry.addViewController("/**/{path:[^\\.]*}")
                .setViewName("forward:/index.html");
    }
}