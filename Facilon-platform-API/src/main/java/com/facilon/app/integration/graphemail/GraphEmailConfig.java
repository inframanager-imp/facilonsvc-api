package com.facilon.app.integration.graphemail;

import lombok.Data;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration properties for Graph Email Service
 * 
 * Configuration example in application.yml:
 * 
 * graph:
 *   email:
 *     enabled: true
 *     tenant-id: your-tenant-id
 *     client-id: your-client-id
 *     client-secret: your-client-secret
 *     sender-email: noreply@yourdomain.com
 *     sender-name: Facilon Platform
 */
@Configuration
@ConfigurationProperties(prefix = "graph.email")
@ConditionalOnProperty(name = "graph.email.enabled", havingValue = "true")
@Data
public class GraphEmailConfig {

    /**
     * Enable/disable Graph Email service
     */
    private boolean enabled = false;

    /**
     * Azure AD Tenant ID
     */
    private String tenantId;

    /**
     * Azure AD Application (Client) ID
     * Required permissions: Mail.Send, Mail.ReadWrite
     */
    private String clientId;

    /**
     * Azure AD Application Client Secret
     */
    private String clientSecret;

    /**
     * Default sender email address
     * Must be a valid mailbox in your Microsoft 365 tenant
     */
    private String senderEmail = "noreply@facilon.com";

    /**
     * Default sender display name
     */
    private String senderName = "Facilon Platform";

    /**
     * Retry configuration
     */
    private int maxRetries = 3;
    private long retryDelayMs = 1000;

    /**
     * Timeout configuration (in milliseconds)
     */
    private int connectionTimeout = 30000; // 30 seconds
    private int readTimeout = 30000; // 30 seconds

    /**
     * Enable detailed logging
     */
    private boolean debugMode = false;
}
