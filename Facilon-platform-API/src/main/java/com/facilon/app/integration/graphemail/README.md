# Graph Email Module

## Overview

This module provides email sending functionality using **Microsoft Graph API** (Office 365) instead of traditional SMTP. It's designed as a standalone, modular component that can be easily extracted as a microservice later.

## Features

✅ Send emails via Microsoft Graph API  
✅ HTML email support  
✅ CC/BCC support  
✅ Email attachments support  
✅ Reply-to configuration  
✅ OAuth2 token caching and auto-refresh  
✅ OTP email templates  
✅ Welcome email templates  
✅ REST API endpoints for microservice extraction  
✅ Swagger/OpenAPI documentation  
✅ Health check endpoint  
✅ Conditional loading (can be disabled)

---

## Architecture

```
┌─────────────────────────────────────────────────┐
│         Graph Email Module                      │
│         (Standalone & Microservice-Ready)       │
├─────────────────────────────────────────────────┤
│                                                 │
│  GraphEmailController (REST API)               │
│         ↓                                       │
│  GraphEmailService (Business Logic)            │
│         ↓                                       │
│  GraphEmailTokenProvider (Auth)                │
│         ↓                                       │
│  Microsoft Graph API                            │
│  https://graph.microsoft.com/v1.0              │
│  /users/{email}/sendMail                       │
│                                                 │
└─────────────────────────────────────────────────┘
```

---

## Setup Instructions

### Step 1: Azure App Registration

1. **Go to Azure Portal** → Azure Active Directory → App Registrations
2. **Create new registration** (or use existing)
3. **Add API Permissions:**
   ```
   Microsoft Graph (Application Permissions):
   ✓ Mail.Send
   ✓ Mail.ReadWrite (optional, for advanced features)
   ```
4. **Grant admin consent** for the permissions
5. **Create client secret:**
   - Certificates & secrets → New client secret
   - Copy the secret value (you won't see it again!)

### Step 2: Configure Application

Add to `application-dev.yml` (or appropriate environment):

```yaml
graph:
  email:
    enabled: true  # Set to false to disable Graph Email
    tenant-id: ${GRAPH_EMAIL_TENANT_ID:your-tenant-id}
    client-id: ${GRAPH_EMAIL_CLIENT_ID:your-client-id}
    client-secret: ${GRAPH_EMAIL_CLIENT_SECRET:your-client-secret}
    sender-email: noreply@yourdomain.com  # Must be valid M365 mailbox
    sender-name: Facilon Platform
    max-retries: 3
    retry-delay-ms: 1000
    connection-timeout: 30000
    read-timeout: 30000
    debug-mode: false
```

### Step 3: Environment Variables (Recommended for Production)

```bash
# .env file or system environment
GRAPH_EMAIL_TENANT_ID=12345678-1234-1234-1234-123456789abc
GRAPH_EMAIL_CLIENT_ID=87654321-4321-4321-4321-cba987654321
GRAPH_EMAIL_CLIENT_SECRET=your-secret-value-here
```

### Step 4: Update Security Configuration

Add Graph Email endpoints to whitelist in `JwtAuthenticationAndTenantFilter.java`:

```java
private static final List<String> WHITELIST_URLS = Arrays.asList(
    // ... existing URLs
    "/api/graph-email/**"  // Add this
);
```

---

## Usage Examples

### Programmatic Usage (Within Application)

```java
@Service
@RequiredArgsConstructor
public class YourService {
    
    private final GraphEmailService graphEmailService;
    
    public void sendOtp(String email, String firstName, String otp) {
        boolean sent = graphEmailService.sendOtpEmail(email, firstName, otp);
        if (sent) {
            log.info("OTP sent successfully");
        }
    }
    
    public void sendWelcome(String email, String firstName, String lastName) {
        graphEmailService.sendWelcomeEmail(email, firstName, lastName);
    }
    
    public void sendCustomEmail(String toEmail, String subject, String htmlBody) {
        graphEmailService.sendEmail(toEmail, subject, htmlBody, null, null);
    }
}
```

### REST API Usage (For Microservice)

#### 1. Send Simple Email
```bash
POST /api/graph-email/send
Content-Type: application/json

{
  "toEmail": "user@example.com",
  "subject": "Test Email",
  "htmlContent": "<h1>Hello World</h1><p>This is a test.</p>",
  "fromEmail": "noreply@yourdomain.com",
  "replyToEmail": "support@yourdomain.com"
}
```

#### 2. Send OTP Email
```bash
POST /api/graph-email/send-otp
?toEmail=user@example.com
&firstName=John
&otp=1234
```

#### 3. Send Welcome Email
```bash
POST /api/graph-email/send-welcome
?toEmail=user@example.com
&firstName=John
&lastName=Doe
```

#### 4. Send Email with Attachments
```bash
POST /api/graph-email/send-with-attachments
Content-Type: multipart/form-data

toEmail: user@example.com
subject: Invoice
htmlContent: <p>Please find your invoice attached.</p>
files: invoice.pdf
files: receipt.pdf
```

#### 5. Health Check
```bash
GET /api/graph-email/health

Response:
{
  "success": true,
  "message": "Graph Email service is operational (token cached)"
}
```

---

## Integration with Existing Email Flow

### Option 1: Use Graph Email Instead of SMTP

Update `EmailServiceApiClient.java` to use Graph Email when enabled:

```java
@Component
@RequiredArgsConstructor
public class EmailServiceApiClient {
    
    private final ObjectProvider<GraphEmailService> graphEmailServiceProvider;
    private final RestTemplate restTemplate;
    
    public void sendEmail(String toEmail, String subject, String htmlContent) {
        // Try Graph Email first (if enabled)
        GraphEmailService graphService = graphEmailServiceProvider.getIfAvailable();
        if (graphService != null) {
            boolean sent = graphService.sendEmail(toEmail, subject, htmlContent, null, null);
            if (sent) {
                log.info("Email sent via Graph API");
                return;
            }
            log.warn("Graph API failed, falling back to SMTP");
        }
        
        // Fallback to SMTP (existing logic)
        // ... existing SMTP code ...
    }
}
```

### Option 2: Configurable Strategy

```yaml
email:
  strategy: graph  # Options: smtp, graph, fallback (graph first, then smtp)
```

---

## Extracting as Microservice

This module is designed to be easily extracted as a standalone microservice:

### Step 1: Create New Spring Boot Project

```xml
<!-- pom.xml -->
<artifactId>facilon-graph-email-service</artifactId>
<version>1.0.0</version>
```

### Step 2: Copy Module Files

```
facilon-graph-email-service/
├── src/main/java/com/facilon/graphemail/
│   ├── GraphEmailController.java
│   ├── GraphEmailService.java
│   ├── GraphEmailTokenProvider.java
│   ├── GraphEmailConfig.java
│   └── dto/
│       ├── GraphEmailRequestDto.java
│       └── GraphEmailResponseDto.java
├── src/main/resources/
│   └── application.yml
└── pom.xml
```

### Step 3: Update Main API to Call Microservice

```java
@Service
public class GraphEmailApiClient {
    
    @Value("${services.graph-email.url}")
    private String graphEmailServiceUrl;
    
    public boolean sendEmail(String toEmail, String subject, String htmlContent) {
        String url = graphEmailServiceUrl + "/api/graph-email/send";
        // REST call to microservice
    }
}
```

---

## API Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/graph-email/send` | Send email with full options |
| POST | `/api/graph-email/send-otp` | Send OTP email |
| POST | `/api/graph-email/send-welcome` | Send welcome email |
| POST | `/api/graph-email/send-with-attachments` | Send email with files |
| GET | `/api/graph-email/health` | Health check |
| POST | `/api/graph-email/invalidate-token` | Force token refresh |

---

## Permissions Required

| Permission | Type | Purpose |
|------------|------|---------|
| `Mail.Send` | Application | Send emails as any user |
| `Mail.ReadWrite` | Application | (Optional) Advanced features |

**Note:** These are **Application permissions** (not delegated), which means the app can send emails without a signed-in user.

---

## Token Management

- **OAuth2 Client Credentials Flow**
- **Token cached** in memory
- **Auto-refresh** when expired (60-second buffer)
- **Manual invalidation** via `/invalidate-token` endpoint

---

## Error Handling

The service includes comprehensive error handling:

```java
try {
    boolean sent = graphEmailService.sendEmail(...);
    if (sent) {
        // Success
    } else {
        // Failed (non-exception error)
    }
} catch (Exception e) {
    // Exception occurred
    log.error("Email send failed: {}", e.getMessage());
}
```

---

## Monitoring & Logging

```java
// Logs include:
- Token acquisition/refresh
- Email send attempts
- Success/failure status
- Error details
- Performance metrics
```

View logs:
```bash
grep "Graph email" logs/facilon-api.log
```

---

## Testing

### Test Token Acquisition
```bash
GET /api/graph-email/health
```

### Test Email Sending
```bash
POST /api/graph-email/send-otp?toEmail=test@example.com&firstName=Test&otp=1234
```

### Swagger UI
Navigate to: `http://localhost:8082/facilon/swagger-ui.html`  
Look for **"Graph Email"** tag

---

## Comparison: SMTP vs Graph API

| Feature | SMTP | Graph API |
|---------|------|-----------|
| Protocol | SMTP (587/465) | HTTPS REST |
| Authentication | Username/Password | OAuth2 Token |
| Rate Limits | Provider-specific | Microsoft throttling |
| Deliverability | Depends on provider | High (Microsoft 365) |
| Tracking | Limited | Rich metadata |
| Attachments | Yes | Yes (Base64 encoded) |
| HTML Support | Yes | Yes |
| Cost | SendGrid pricing | Included with M365 |

---

## Troubleshooting

### Issue: "Failed to obtain Graph token"
- Check client ID, secret, and tenant ID
- Verify app registration exists in Azure
- Ensure admin consent granted

### Issue: "401 Unauthorized"
- Check if Mail.Send permission is granted
- Verify admin consent
- Try invalidating token: `POST /api/graph-email/invalidate-token`

### Issue: "404 Not Found"
- Verify sender email is a valid mailbox in your M365 tenant
- Check if mailbox is enabled

### Issue: Module Not Loading
- Check `graph.email.enabled=true` in config
- Verify Spring Boot picks up the configuration
- Check logs for conditional bean creation

---

## Security Considerations

✅ Client secret stored in environment variables (not code)  
✅ Token cached securely in memory  
✅ HTTPS only for Graph API calls  
✅ No email content logged (for privacy)  
✅ Rate limiting handled by Microsoft  
✅ Admin consent required for permissions

---

## Future Enhancements

- [ ] Email templates from database
- [ ] Scheduled email sending
- [ ] Email tracking (read receipts)
- [ ] Bulk email sending
- [ ] Email queue with retry logic
- [ ] Analytics dashboard
- [ ] Multi-tenant support
- [ ] Template versioning

---

## Support

For issues or questions about this module:
- Check logs: `logs/facilon-api.log`
- Health check: `GET /api/graph-email/health`
- Swagger docs: `http://localhost:8082/facilon/swagger-ui.html`

---

**Module Version:** 1.0.0  
**Last Updated:** February 11, 2026  
**Maintainer:** Facilon Platform Team
