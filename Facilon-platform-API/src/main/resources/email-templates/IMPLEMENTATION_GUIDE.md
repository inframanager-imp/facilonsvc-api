# HTML Email Templates - Implementation Guide

**Date:** February 11, 2026  
**Status:** Complete implementation instructions for integrating HTML email templates

---

## Table of Contents

1. [Overview](#overview)
2. [Implementation Options](#implementation-options)
3. [Step-by-Step Integration](#step-by-step-integration)
4. [Code Examples](#code-examples)
5. [Testing](#testing)
6. [Deployment](#deployment)

---

## Overview

All 26 email templates have been converted from plain text to professional HTML format. This guide provides complete instructions for integrating these templates into the existing Facilon Platform API.

### Files to Update

1. `InvestorNotificationService.java` - 21 email methods
2. `DocumentNotificationService.java` - 4 email methods
3. `AuthorizedUserController.java` - 1 password reset email
4. `EmailService.java` - Add HTML email support

---

## Implementation Options

### Option 1: Simple File-Based Template Loading (Recommended for Quick Start)

**Pros:**
- Simple implementation
- No additional dependencies
- Works with current code structure
- Quick to deploy

**Cons:**
- Manual variable replacement
- Less feature-rich than template engines

### Option 2: Thymeleaf Template Engine (Recommended for Production)

**Pros:**
- Professional template engine
- Built-in variable handling
- Spring Boot integration
- Caching support
- Better maintainability

**Cons:**
- Requires dependency addition
- Template syntax change needed
- Slightly more complex setup

---

## Step-by-Step Integration

### Step 1: Add HTML Email Support to EmailService

**File:** `src/main/java/com/facilon/app/service/EmailService.java`

Add the following method:

```java
import org.springframework.mail.javamail.MimeMessageHelper;
import javax.mail.internet.MimeMessage;

public void sendHtmlMessage(String to, String subject, String htmlBody) {
    try {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
        
        helper.setFrom(fromEmail);
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(htmlBody, true); // true = isHtml
        
        mailSender.send(message);
        log.info("HTML email sent to: {}", to);
    } catch (Exception e) {
        log.error("Failed to send HTML email to: {}", to, e);
        throw new RuntimeException("Email sending failed", e);
    }
}
```

### Step 2: Create Template Loading Utility

**Create New File:** `src/main/java/com/facilon/app/util/EmailTemplateLoader.java`

```java
package com.facilon.app.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Map;

@Component
@Slf4j
public class EmailTemplateLoader {

    /**
     * Load HTML template from resources/email-templates/
     */
    public String loadTemplate(String templateName) {
        try {
            ClassPathResource resource = new ClassPathResource("email-templates/" + templateName);
            byte[] bytes = Files.readAllBytes(resource.getFile().toPath());
            return new String(bytes, StandardCharsets.UTF_8);
        } catch (IOException e) {
            log.error("Failed to load email template: {}", templateName, e);
            throw new RuntimeException("Failed to load email template: " + templateName, e);
        }
    }

    /**
     * Replace template variables with actual values
     */
    public String replaceVariables(String template, Map<String, String> variables) {
        String result = template;
        for (Map.Entry<String, String> entry : variables.entrySet()) {
            String placeholder = "{{" + entry.getKey() + "}}";
            String value = entry.getValue() != null ? entry.getValue() : "";
            result = result.replace(placeholder, value);
        }
        return result;
    }

    /**
     * Load template and replace variables in one call
     */
    public String processTemplate(String templateName, Map<String, String> variables) {
        String template = loadTemplate(templateName);
        return replaceVariables(template, variables);
    }
}
```

### Step 3: Update InvestorNotificationService

**File:** `src/main/java/com/facilon/app/module/client/service/InvestorNotificationService.java`

Add the template loader to the service:

```java
@Service
@RequiredArgsConstructor
@Slf4j
public class InvestorNotificationService {

    private final EmailService emailService;
    private final EmailTemplateLoader templateLoader; // ADD THIS
    
    @Value("${app.base-url:http://localhost:3000}")
    private String baseUrl;
    
    @Value("${app.support-email:support@facilon.com}")
    private String supportEmail;
    
    @Value("${app.support-phone:+91-123-456-7890}")
    private String supportPhone;
```

Update each email method. Example for `sendIndiaNoPanEmail()`:

**BEFORE:**
```java
public void sendIndiaNoPanEmail(String email, String investorName) {
    try {
        String subject = "PAN Required for Registration - Facilon Platform";
        String body = buildIndiaNoPanEmail(investorName);
        emailService.sendSimpleMessage(email, subject, body);
        log.info("India no PAN email sent to: {}", email);
    } catch (Exception e) {
        log.error("Failed to send India no PAN email", e);
    }
}
```

**AFTER:**
```java
public void sendIndiaNoPanEmail(String email, String investorName) {
    try {
        String subject = "PAN Required for Registration - Facilon Platform";
        
        Map<String, String> variables = Map.of(
            "investorName", investorName,
            "baseUrl", baseUrl,
            "supportEmail", supportEmail,
            "supportPhone", supportPhone
        );
        
        String htmlBody = templateLoader.processTemplate("02-india-no-pan.html", variables);
        emailService.sendHtmlMessage(email, subject, htmlBody);
        
        log.info("India no PAN email sent to: {}", email);
    } catch (Exception e) {
        log.error("Failed to send India no PAN email", e);
    }
}
```

### Step 4: Update DocumentNotificationService

**File:** `src/main/java/com/facilon/app/module/client/service/DocumentNotificationService.java`

Add template loader and update methods. Example for `sendDocumentUploadedNotification()`:

```java
@Service
@RequiredArgsConstructor
@Slf4j
public class DocumentNotificationService {

    private final EmailService emailService;
    private final EmailTemplateLoader templateLoader; // ADD THIS
    
    @Value("${app.base-url:http://localhost:3000}")
    private String baseUrl;
    
    @Value("${app.support-email:support@facilon.com}")
    private String supportEmail;

    public void sendDocumentUploadedNotification(String investorEmail, String investorName, DocumentDto document) {
        try {
            String subject = "Document Uploaded Successfully - Facilon Platform";
            
            Map<String, String> variables = Map.of(
                "investorName", investorName,
                "documentType", getDocumentTypeLabel(document.getDocumentType()),
                "fileName", document.getOriginalFileName(),
                "uploadDate", document.getUploadedAt().toString(),
                "baseUrl", baseUrl,
                "supportEmail", supportEmail,
                "supportPhone", "+91 22 1234 5678"
            );
            
            String htmlBody = templateLoader.processTemplate("22-document-uploaded.html", variables);
            emailService.sendHtmlMessage(investorEmail, subject, htmlBody);
            
            log.info("Upload notification sent to: {}", investorEmail);
        } catch (Exception e) {
            log.error("Failed to send upload notification", e);
        }
    }
}
```

### Step 5: Update Password Reset in AuthorizedUserController

**File:** `src/main/java/com/facilon/app/controller/AuthorizedUserController.java`

**BEFORE:**
```java
emailService.sendSimpleMessage(user.getEmailId(), "Password Reset Request", 
    "To reset your password, click the link below:\n" + resetLink);
```

**AFTER:**
```java
@Autowired
private EmailTemplateLoader templateLoader;

@Value("${app.support-email:support@facilon.com}")
private String supportEmail;

@Value("${app.support-phone:+91-123-456-7890}")
private String supportPhone;

// In the forgotPassword method:
Map<String, String> variables = Map.of(
    "userName", user.getFirstName() + " " + user.getLastName(),
    "resetLink", resetLink,
    "supportEmail", supportEmail,
    "supportPhone", supportPhone
);

String htmlBody = templateLoader.processTemplate("26-password-reset.html", variables);
emailService.sendHtmlMessage(user.getEmailId(), "Password Reset Request", htmlBody);
```

---

## Complete Code Examples

### Example 1: Login Details Email

```java
public void sendLoginDetailsEmail(String email, String investorName, String loginId, 
                                  String temporaryPassword, String investorCode) {
    try {
        String subject = "Your Facilon Platform Login Credentials";
        
        Map<String, String> variables = Map.of(
            "investorName", investorName,
            "loginId", loginId,
            "investorCode", investorCode,
            "temporaryPassword", temporaryPassword,
            "baseUrl", baseUrl,
            "supportEmail", supportEmail,
            "supportPhone", supportPhone
        );
        
        String htmlBody = templateLoader.processTemplate("01-login-details.html", variables);
        emailService.sendHtmlMessage(email, subject, htmlBody);
        
        log.info("Login details email sent to: {}", email);
    } catch (Exception e) {
        log.error("Failed to send login details email", e);
    }
}
```

### Example 2: Legal Entity Success Email

```java
public void sendLegalEntitySuccessfulSelfRegistrationEmail(String email, String entityName, 
                                                           String representativeName, String uniqueCode, 
                                                           String loginUrl) {
    try {
        String subject = "Registration Successful - Welcome to Facilon";
        
        Map<String, String> variables = Map.of(
            "entityName", entityName,
            "representativeName", representativeName,
            "uniqueCode", uniqueCode,
            "loginUrl", loginUrl,
            "baseUrl", baseUrl,
            "supportEmail", supportEmail,
            "supportPhone", supportPhone
        );
        
        String htmlBody = templateLoader.processTemplate("13-legal-entity-successful-self.html", variables);
        emailService.sendHtmlMessage(email, subject, htmlBody);
        
        log.info("Legal entity successful self-registration email sent to: {}", email);
    } catch (Exception e) {
        log.error("Failed to send legal entity successful self-registration email", e);
    }
}
```

### Example 3: Document Rejected Email

```java
public void sendDocumentRejectedNotification(String investorEmail, String investorName, DocumentDto document) {
    try {
        String subject = "Document Requires Attention - Facilon Platform";
        
        Map<String, String> variables = Map.of(
            "investorName", investorName,
            "documentType", getDocumentTypeLabel(document.getDocumentType()),
            "fileName", document.getOriginalFileName(),
            "remarks", document.getRemarks() != null ? document.getRemarks() : "Please contact support for details",
            "baseUrl", baseUrl,
            "supportEmail", supportEmail,
            "supportPhone", "+91 22 1234 5678"
        );
        
        String htmlBody = templateLoader.processTemplate("24-document-rejected.html", variables);
        emailService.sendHtmlMessage(investorEmail, subject, htmlBody);
        
        log.info("Rejection notification sent to: {}", investorEmail);
    } catch (Exception e) {
        log.error("Failed to send rejection notification", e);
    }
}
```

---

## Template Mapping Reference

### InvestorNotificationService Methods

| Method Name | Template File | Variables |
|-------------|---------------|-----------|
| `sendLoginDetailsEmail()` | `01-login-details.html` | investorName, loginId, investorCode, temporaryPassword, baseUrl, supportEmail, supportPhone |
| `sendIndiaNoPanEmail()` | `02-india-no-pan.html` | investorName, baseUrl, supportEmail, supportPhone |
| `sendIndianOriginNoOciEmail()` | `03-indian-origin-no-oci.html` | investorName, baseUrl, supportEmail, supportPhone |
| `sendIndianOriginNoPanAndOciEmail()` | `04-indian-origin-no-pan-and-oci.html` | investorName, baseUrl, supportEmail, supportPhone |
| `sendMarketOtherThanIndiaEmail()` | `05-market-other-than-india.html` | investorName, market, baseUrl, supportEmail, supportPhone |
| `sendSuccessfulRegistrationSelfEmail()` | `06-successful-registration-self.html` | investorName, uniqueCode, loginUrl, baseUrl, supportEmail, supportPhone |
| `sendSuccessfulRegistrationIntroducedEmail()` | `07-successful-registration-introduced.html` | investorName, serviceProviderName, appointmentUrl, supportEmail, supportPhone |
| `sendNoPanIntroducedNotificationToSP()` | `08-no-pan-sp-notification.html` | spName, investorName, investorEmail, baseUrl, supportEmail, supportPhone |
| `sendOutsideIndiaAssistanceSelfEmail()` | `09-outside-india-assistance.html` | investorName, baseUrl, supportEmail, supportPhone |
| `sendLegalEntityMarketOtherThanIndiaEmail()` | `10-legal-entity-market-other-than-india.html` | entityName, market, baseUrl, supportEmail, supportPhone |
| `sendLegalEntityIndiaNoPanEmail()` | `11-legal-entity-india-no-pan.html` | entityName, representativeName, baseUrl, supportEmail, supportPhone |
| `sendForeignLegalEntityEmail()` | `12-foreign-legal-entity.html` | entityName, representativeName, baseUrl, supportEmail, supportPhone |
| `sendLegalEntitySuccessfulSelfRegistrationEmail()` | `13-legal-entity-successful-self.html` | entityName, representativeName, uniqueCode, loginUrl, baseUrl, supportEmail, supportPhone |
| `sendLegalEntitySuccessfulIntroducedEmail()` | `14-legal-entity-successful-introduced.html` | entityName, representativeName, serviceProviderName, appointmentUrl, baseUrl, supportEmail, supportPhone |
| `sendNoOciIntroducedNotificationToSP()` | `15-no-oci-sp-notification.html` | spName, investorName, investorEmail, baseUrl, supportEmail, supportPhone |
| `sendNoPanAndOciIntroducedNotificationToSP()` | `16-no-pan-oci-sp-notification.html` | spName, investorName, investorEmail, baseUrl, supportEmail, supportPhone |
| `sendIntroducedInvestorEmail()` | `17-introduced-investor-email.html` | brokerName, investorName, investorEmail, investorMobile, baseUrl, supportEmail |
| `sendIntroducedInvestorRegisteredEmail()` | `18-introduced-investor-registered.html` | brokerName, investorName, investorCode, baseUrl, supportEmail |
| `sendOutsideIndiaAssistanceLegalEmail()` | `19-outside-india-legal-assistance.html` | legalEntityName, baseUrl, supportEmail, supportPhone |
| `sendFinalSubmissionEmail()` | `20-final-submission.html` | investorName, investorCode, timestamp, baseUrl, supportEmail, supportPhone |
| `sendUpdateRequestEmail()` | `21-update-request.html` | investorName, adminName, requestedFields, reason, baseUrl, supportEmail, supportPhone |

### DocumentNotificationService Methods

| Method Name | Template File | Variables |
|-------------|---------------|-----------|
| `sendDocumentUploadedNotification()` | `22-document-uploaded.html` | investorName, documentType, fileName, uploadDate, baseUrl, supportEmail, supportPhone |
| `sendDocumentVerifiedNotification()` | `23-document-verified.html` | investorName, documentType, fileName, verifiedDate, baseUrl, supportEmail, supportPhone |
| `sendDocumentRejectedNotification()` | `24-document-rejected.html` | investorName, documentType, fileName, remarks, baseUrl, supportEmail, supportPhone |
| `sendAllDocumentsVerifiedNotification()` | `25-all-documents-verified.html` | investorName, baseUrl, supportEmail, supportPhone |

### AuthorizedUserController

| Location | Template File | Variables |
|----------|---------------|-----------|
| `forgotPassword()` method | `26-password-reset.html` | userName, resetLink, supportEmail, supportPhone |

---

## Testing

### Unit Testing

Create test class: `EmailTemplateLoaderTest.java`

```java
@SpringBootTest
class EmailTemplateLoaderTest {

    @Autowired
    private EmailTemplateLoader templateLoader;

    @Test
    void testLoadLoginDetailsTemplate() {
        String template = templateLoader.loadTemplate("01-login-details.html");
        assertNotNull(template);
        assertTrue(template.contains("{{investorName}}"));
    }

    @Test
    void testReplaceVariables() {
        String template = "Hello {{name}}, your code is {{code}}";
        Map<String, String> vars = Map.of("name", "John", "code", "123");
        String result = templateLoader.replaceVariables(template, vars);
        assertEquals("Hello John, your code is 123", result);
    }

    @Test
    void testProcessTemplate() {
        Map<String, String> variables = Map.of(
            "investorName", "Test User",
            "baseUrl", "http://localhost:3000",
            "supportEmail", "support@test.com",
            "supportPhone", "+91-123-456-7890"
        );
        
        String result = templateLoader.processTemplate("02-india-no-pan.html", variables);
        assertNotNull(result);
        assertFalse(result.contains("{{investorName}}"));
        assertTrue(result.contains("Test User"));
    }
}
```

### Integration Testing

Test actual email sending:

```java
@SpringBootTest
class EmailIntegrationTest {

    @Autowired
    private InvestorNotificationService notificationService;

    @Test
    void testSendIndiaNoPanEmail() {
        // Use a test email
        String testEmail = "test@example.com";
        notificationService.sendIndiaNoPanEmail(testEmail, "Test Investor");
        // Verify email was sent (check logs or use email testing service)
    }
}
```

### Manual Testing

1. **Use Mailtrap.io** (Development email testing):
   ```yaml
   spring:
     mail:
       host: smtp.mailtrap.io
       port: 2525
       username: your-mailtrap-username
       password: your-mailtrap-password
   ```

2. **Create Test Endpoint:**
   ```java
   @RestController
   @RequestMapping("/api/test")
   public class EmailTestController {
       
       @Autowired
       private InvestorNotificationService notificationService;
       
       @GetMapping("/email/{templateNumber}")
       public ResponseEntity<String> testEmail(@PathVariable int templateNumber) {
           String testEmail = "test@example.com";
           
           switch(templateNumber) {
               case 1 -> notificationService.sendLoginDetailsEmail(testEmail, "Test User", "TEST001", "Pass123", "CODE001");
               case 2 -> notificationService.sendIndiaNoPanEmail(testEmail, "Test User");
               // ... add all templates
           }
           
           return ResponseEntity.ok("Test email sent");
       }
   }
   ```

---

## Migration Strategy

### Phase 1: Parallel Running (Recommended)

Keep old text emails working while testing HTML:

```java
public void sendIndiaNoPanEmail(String email, String investorName) {
    try {
        String subject = "PAN Required for Registration - Facilon Platform";
        
        // Try HTML first
        try {
            Map<String, String> variables = Map.of(
                "investorName", investorName,
                "baseUrl", baseUrl,
                "supportEmail", supportEmail,
                "supportPhone", supportPhone
            );
            String htmlBody = templateLoader.processTemplate("02-india-no-pan.html", variables);
            emailService.sendHtmlMessage(email, subject, htmlBody);
            log.info("HTML email sent to: {}", email);
        } catch (Exception htmlError) {
            // Fallback to plain text
            log.warn("HTML email failed, falling back to plain text", htmlError);
            String body = buildIndiaNoPanEmail(investorName);
            emailService.sendSimpleMessage(email, subject, body);
        }
        
    } catch (Exception e) {
        log.error("Failed to send India no PAN email", e);
    }
}
```

### Phase 2: Full Migration

Once HTML templates are tested and confirmed working:

1. Remove all `buildXXXEmail()` methods from services
2. Remove plain text fallback logic
3. Update all method calls to use HTML only
4. Clean up unused code

---

## Deployment Checklist

### Pre-Deployment

- [ ] All 26 HTML templates created and validated
- [ ] EmailTemplateLoader utility class created
- [ ] EmailService.sendHtmlMessage() method added
- [ ] All services updated to use HTML templates
- [ ] Unit tests passing
- [ ] Integration tests passing
- [ ] Manual testing completed

### Deployment Steps

1. **Build Application:**
   ```bash
   mvn clean install
   ```

2. **Verify Templates in JAR:**
   ```bash
   jar tf target/facilon-api.jar | grep email-templates
   ```

3. **Deploy to Test Environment:**
   - Deploy JAR to test server
   - Test email sending for all 26 templates
   - Verify email client compatibility

4. **Monitor Logs:**
   ```bash
   tail -f logs/application.log | grep "email sent"
   ```

5. **Production Deployment:**
   - Deploy to production
   - Monitor email delivery rates
   - Check for any errors in logs

### Post-Deployment Monitoring

Monitor these metrics:

1. **Email Delivery Rate:**
   - Success rate should be > 98%
   - Monitor bounce rates

2. **Email Open Rates:**
   - Track open rates per template
   - Typical rates: 15-25%

3. **Error Rates:**
   - Template loading errors
   - Variable replacement errors
   - SMTP errors

4. **User Feedback:**
   - Email readability
   - Link functionality
   - Display issues in different email clients

---

## Troubleshooting

### Issue 1: Template Not Found

**Error:** `Failed to load email template: XX-template.html`

**Solutions:**
- Verify template file exists in `src/main/resources/email-templates/`
- Check file name matches exactly (case-sensitive)
- Ensure template is included in JAR build
- Verify classpath resource loading

### Issue 2: Variables Not Replaced

**Error:** Email contains `{{variableName}}` in content

**Solutions:**
- Check variable name matches template placeholder exactly
- Ensure all required variables are provided in Map
- Check for null values in variables
- Verify replaceVariables() method is called

### Issue 3: Email Display Issues

**Error:** Email looks broken in certain email clients

**Solutions:**
- Test in multiple email clients (Gmail, Outlook, Apple Mail)
- Verify inline CSS is used (not external stylesheets)
- Check table-based layouts for complex structures
- Test on mobile devices

### Issue 4: SMTP Errors

**Error:** Email sending fails

**Solutions:**
- Verify SMTP configuration in application.yml
- Check email service credentials
- Verify HTML email size < 100KB
- Test with plain text fallback

---

## Performance Optimization

### 1. Template Caching

Cache loaded templates to avoid repeated file reads:

```java
@Component
@Slf4j
public class EmailTemplateLoader {

    private final Map<String, String> templateCache = new ConcurrentHashMap<>();

    public String loadTemplate(String templateName) {
        // Check cache first
        if (templateCache.containsKey(templateName)) {
            return templateCache.get(templateName);
        }
        
        // Load from file
        try {
            ClassPathResource resource = new ClassPathResource("email-templates/" + templateName);
            byte[] bytes = Files.readAllBytes(resource.getFile().toPath());
            String template = new String(bytes, StandardCharsets.UTF_8);
            
            // Cache for future use
            templateCache.put(templateName, template);
            return template;
        } catch (IOException e) {
            log.error("Failed to load email template: {}", templateName, e);
            throw new RuntimeException("Failed to load email template: " + templateName, e);
        }
    }
}
```

### 2. Async Email Sending

Send emails asynchronously to avoid blocking:

```java
@Async
public void sendIndiaNoPanEmail(String email, String investorName) {
    // Email sending logic
}
```

Enable async in configuration:

```java
@Configuration
@EnableAsync
public class AsyncConfig {
    // Configuration
}
```

---

## Security Considerations

### 1. Template Injection Prevention

Always sanitize user input before inserting into templates:

```java
private String sanitize(String input) {
    if (input == null) return "";
    return input.replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;");
}

// Use when building variables map:
Map<String, String> variables = Map.of(
    "investorName", sanitize(investorName),
    // ... other variables
);
```

### 2. Email Rate Limiting

Implement rate limiting to prevent abuse:

```java
@RateLimiter(name = "emailService")
public void sendHtmlMessage(String to, String subject, String htmlBody) {
    // Email sending logic
}
```

### 3. Email Content Validation

Validate email size and content:

```java
public void sendHtmlMessage(String to, String subject, String htmlBody) {
    // Check email size
    if (htmlBody.length() > 100_000) {
        throw new RuntimeException("Email content too large");
    }
    
    // Send email
}
```

---

## Alternative: Thymeleaf Integration

### Step 1: Add Dependency

Add to `pom.xml`:

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-thymeleaf</artifactId>
</dependency>
```

### Step 2: Convert Templates

Change variable syntax from `{{variable}}` to `th:text="${variable}"` or `[(${variable})]`

Example:

**Current (Simple Replacement):**
```html
<p>Dear <strong>{{investorName}}</strong>,</p>
```

**Thymeleaf:**
```html
<p>Dear <strong th:text="${investorName}">Investor Name</strong>,</p>
```

### Step 3: Use TemplateEngine

```java
@Service
@RequiredArgsConstructor
public class InvestorNotificationService {

    private final EmailService emailService;
    private final TemplateEngine templateEngine;

    public void sendIndiaNoPanEmail(String email, String investorName) {
        try {
            String subject = "PAN Required for Registration - Facilon Platform";
            
            Context context = new Context();
            context.setVariable("investorName", investorName);
            context.setVariable("baseUrl", baseUrl);
            context.setVariable("supportEmail", supportEmail);
            context.setVariable("supportPhone", supportPhone);
            
            String htmlBody = templateEngine.process("02-india-no-pan", context);
            emailService.sendHtmlMessage(email, subject, htmlBody);
            
            log.info("India no PAN email sent to: {}", email);
        } catch (Exception e) {
            log.error("Failed to send India no PAN email", e);
        }
    }
}
```

---

## Best Practices

### 1. Variable Handling

Always provide default values:

```java
Map<String, String> variables = new HashMap<>();
variables.put("investorName", investorName != null ? investorName : "Valued Investor");
variables.put("baseUrl", baseUrl);
variables.put("supportEmail", supportEmail);
variables.put("supportPhone", supportPhone);
```

### 2. Error Handling

Never let email errors break the main flow:

```java
try {
    // Send email
} catch (Exception e) {
    log.error("Email sending failed, but continuing...", e);
    // Don't rethrow - log and continue
}
```

### 3. Logging

Log all email activities:

```java
log.info("Sending {} email to: {}", templateName, email);
// ... send email
log.info("{} email sent successfully to: {}", templateName, email);
```

### 4. Testing

Always test in development before production:

```java
@Profile("dev")
@Component
public class EmailTestService {
    // Test methods to send sample emails
}
```

---

## Support Resources

### Email Client Testing Tools

1. **Litmus** - https://litmus.com
   - Test across 100+ email clients
   - Professional testing service

2. **Email on Acid** - https://www.emailonacid.com
   - Comprehensive email testing

3. **Mailtrap** - https://mailtrap.io
   - Development email testing
   - No real emails sent

### Email Template Best Practices

1. **Can I Email** - https://www.caniemail.com
   - Check CSS support across email clients

2. **Really Good Emails** - https://reallygoodemails.com
   - Email design inspiration

3. **Email Template Guide** - https://www.campaignmonitor.com/css/
   - CSS support reference

---

## Rollback Plan

If issues occur after deployment:

### Option 1: Quick Rollback

Revert to plain text emails by changing one line:

```java
// Change from:
emailService.sendHtmlMessage(email, subject, htmlBody);

// Back to:
emailService.sendSimpleMessage(email, subject, plainTextBody);
```

### Option 2: Feature Flag

Add feature flag for HTML emails:

```java
@Value("${email.use-html:true}")
private boolean useHtmlEmails;

public void sendEmail() {
    if (useHtmlEmails) {
        // Send HTML
    } else {
        // Send plain text
    }
}
```

---

## Maintenance

### Regular Updates

1. **Quarterly Review:**
   - Review all templates for accuracy
   - Update links and resources
   - Check for broken images or links

2. **Content Updates:**
   - Keep regulatory information current
   - Update contact information
   - Refresh branding as needed

3. **Performance Monitoring:**
   - Track email delivery rates
   - Monitor template loading times
   - Check for errors in logs

---

## Conclusion

This implementation guide provides complete instructions for integrating all 26 HTML email templates into the Facilon Platform API. Follow the steps in order, test thoroughly, and monitor after deployment.

---

**Last Updated:** February 11, 2026  
**Version:** 1.0  
**Status:** Ready for Implementation  
**Total Templates:** 26 (All complete ✅)
