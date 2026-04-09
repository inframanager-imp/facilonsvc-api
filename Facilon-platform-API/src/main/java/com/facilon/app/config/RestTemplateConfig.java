package com.facilon.app.config;

import org.apache.hc.client5.http.classic.HttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
import org.apache.hc.core5.http.io.SocketConfig;
import org.apache.hc.core5.util.Timeout;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;

/**
 * RestTemplate configuration with Apache HttpClient5 support.
 * 
 * This configuration resolves the "Invalid HTTP method: PATCH" error
 * by using Apache HttpClient instead of Java's default HttpURLConnection,
 * which doesn't support PATCH requests properly.
 * 
 * Required for Dynamics 365 Web API PATCH operations.
 */
@Configuration
public class RestTemplateConfig {

    /**
     * Creates a RestTemplate bean with Apache HttpClient5 support.
     * 
     * Features:
     * - Supports PATCH HTTP method (required for Dynamics 365)
     * - Connection pooling for better performance
     * - Configurable timeouts
     * 
     * @return RestTemplate configured with Apache HttpClient
     */
    @Bean(name = "restTemplate")
    public RestTemplate restTemplate() {
        // Configure connection manager with connection pooling
        PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager();
        connectionManager.setMaxTotal(100); // Max total connections
        connectionManager.setDefaultMaxPerRoute(20); // Max connections per route
        
        // Configure socket timeouts
        SocketConfig socketConfig = SocketConfig.custom()
                .setSoTimeout(Timeout.ofSeconds(30))
                .build();
        connectionManager.setDefaultSocketConfig(socketConfig);

        // Build HttpClient with connection manager
        HttpClient httpClient = HttpClients.custom()
                .setConnectionManager(connectionManager)
                .build();

        // Create request factory with timeouts
        HttpComponentsClientHttpRequestFactory requestFactory = 
                new HttpComponentsClientHttpRequestFactory(httpClient);
        requestFactory.setConnectTimeout(Duration.ofSeconds(30));
        
        return new RestTemplate(requestFactory);
    }
}
