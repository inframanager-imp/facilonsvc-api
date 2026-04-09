package com.facilon.app.security;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.ArrayList;
import java.util.List;

@Configuration
@Slf4j
@ConfigurationProperties(prefix = "ignored")
public class WebConfig implements WebMvcConfigurer {
    @Setter
    private List<String> httpUrl = new ArrayList<>();
    @Value("${app.cors_origins_url}")
    private String corsOriginsUrl;


    @Override
    public void addCorsMappings(CorsRegistry registry) {
        log.info("url>>>>>>"+corsOriginsUrl);
        registry.addMapping("/**")
                .allowedOrigins(corsOriginsUrl,"http://localhost:4200")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true);
    }


}
