# Facilon Email Templates

This directory contains all HTML email templates for the Facilon Platform registration system.

## Overview

All email templates have been converted from plain text to professional HTML format with:
- Modern, responsive design
- Professional color schemes with gradients
- Clear section separation with visual borders
- Consistent branding
- Mobile-friendly layout
- Email client compatibility

## Template List

### Individual Investor Templates (14 templates)

| # | Template File | Scenario | Variables |
|---|--------------|----------|-----------|
| 01 | `01-login-details.html` | Login credentials after registration | `investorName`, `loginId`, `investorCode`, `temporaryPassword`, `baseUrl`, `supportEmail`, `supportPhone` |
| 02 | `02-india-no-pan.html` | Indian resident without PAN | `investorName`, `baseUrl`, `supportEmail`, `supportPhone` |
| 03 | `03-indian-origin-no-oci.html` | Person of Indian origin without OCI | `investorName`, `baseUrl`, `supportEmail`, `supportPhone` |
| 04 | `04-indian-origin-no-pan-and-oci.html` | Indian origin without PAN and OCI | `investorName`, `baseUrl`, `supportEmail`, `supportPhone` |
| 05 | `05-market-other-than-india.html` | Interest in non-India market | `investorName`, `market`, `baseUrl`, `supportEmail`, `supportPhone` |
| 06 | `06-successful-registration-self.html` | Successful self-registration | `investorName`, `uniqueCode`, `loginUrl`, `baseUrl`, `supportEmail`, `supportPhone` |
| 07 | `07-successful-registration-introduced.html` | Successful registration via SP | `investorName`, `serviceProviderName`, `appointmentUrl`, `supportEmail`, `supportPhone` |
| 08 | `08-no-pan-sp-notification.html` | SP notification for missing PAN | `spName`, `investorName`, `investorEmail`, `baseUrl`, `supportEmail`, `supportPhone` |
| 09 | `09-outside-india-assistance.html` | NRI/Foreign investor assistance | `investorName`, `baseUrl`, `supportEmail`, `supportPhone` |
| 15 | `15-no-oci-sp-notification.html` | SP notification for missing OCI | `spName`, `investorName`, `investorEmail`, `baseUrl`, `supportEmail`, `supportPhone` |
| 16 | `16-no-pan-oci-sp-notification.html` | SP notification for missing PAN & OCI | `spName`, `investorName`, `investorEmail`, `baseUrl`, `supportEmail`, `supportPhone` |
| 17 | `17-introduced-investor-email.html` | Broker notification of new introduction | `brokerName`, `investorName`, `investorEmail`, `investorMobile`, `baseUrl`, `supportEmail` |
| 18 | `18-introduced-investor-registered.html` | Broker notification of registration complete | `brokerName`, `investorName`, `investorCode`, `baseUrl`, `supportEmail` |
| 20 | `20-final-submission.html` | Final profile submission confirmation | `investorName`, `investorCode`, `timestamp`, `baseUrl`, `supportEmail`, `supportPhone` |
| 21 | `21-update-request.html` | Admin request for information update | `investorName`, `adminName`, `requestedFields`, `reason`, `baseUrl`, `supportEmail`, `supportPhone` |

### Legal Entity Templates (6 templates)

| # | Template File | Scenario | Variables |
|---|--------------|----------|-----------|
| 10 | `10-legal-entity-market-other-than-india.html` | Corporate interest in non-India market | `entityName`, `market`, `baseUrl`, `supportEmail`, `supportPhone` |
| 11 | `11-legal-entity-india-no-pan.html` | Legal entity without PAN | `entityName`, `representativeName`, `baseUrl`, `supportEmail`, `supportPhone` |
| 12 | `12-foreign-legal-entity.html` | Foreign legal entity registration | `entityName`, `representativeName`, `baseUrl`, `supportEmail`, `supportPhone` |
| 13 | `13-legal-entity-successful-self.html` | Successful corporate self-registration | `entityName`, `representativeName`, `uniqueCode`, `loginUrl`, `baseUrl`, `supportEmail`, `supportPhone` |
| 14 | `14-legal-entity-successful-introduced.html` | Successful corporate registration via SP | `entityName`, `representativeName`, `serviceProviderName`, `appointmentUrl`, `baseUrl`, `supportEmail`, `supportPhone` |
| 19 | `19-outside-india-legal-assistance.html` | Foreign legal entity outside India assistance | `legalEntityName`, `baseUrl`, `supportEmail`, `supportPhone` |

### Document Management Templates (4 templates)

| # | Template File | Scenario | Variables |
|---|--------------|----------|-----------|
| 22 | `22-document-uploaded.html` | Document upload confirmation | `investorName`, `documentType`, `fileName`, `uploadDate`, `baseUrl`, `supportEmail`, `supportPhone` |
| 23 | `23-document-verified.html` | Document verification success | `investorName`, `documentType`, `fileName`, `verifiedDate`, `baseUrl`, `supportEmail`, `supportPhone` |
| 24 | `24-document-rejected.html` | Document rejection notice | `investorName`, `documentType`, `fileName`, `remarks`, `baseUrl`, `supportEmail`, `supportPhone` |
| 25 | `25-all-documents-verified.html` | All documents verified notification | `investorName`, `baseUrl`, `supportEmail`, `supportPhone` |

### Password Management Template (1 template)

| # | Template File | Scenario | Variables |
|---|--------------|----------|-----------|
| 26 | `26-password-reset.html` | Password reset request | `userName`, `resetLink`, `supportEmail`, `supportPhone` |

## How to Use These Templates

### 1. Update InvestorNotificationService

The current service uses plain text templates. To use these HTML templates, you need to:

#### Option A: Load HTML from Files

```java
import org.springframework.core.io.ClassPathResource;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

private String loadHtmlTemplate(String templateName) throws IOException {
    ClassPathResource resource = new ClassPathResource("email-templates/" + templateName);
    return new String(Files.readAllBytes(resource.getFile().toPath()), StandardCharsets.UTF_8);
}

private String replaceVariables(String template, Map<String, String> variables) {
    String result = template;
    for (Map.Entry<String, String> entry : variables.entrySet()) {
        result = result.replace("{{" + entry.getKey() + "}}", entry.getValue());
    }
    return result;
}

public void sendLoginDetailsEmail(String email, String investorName, String loginId, 
                                  String temporaryPassword, String investorCode) {
    try {
        String template = loadHtmlTemplate("01-login-details.html");
        
        Map<String, String> variables = Map.of(
            "investorName", investorName,
            "loginId", loginId,
            "investorCode", investorCode,
            "temporaryPassword", temporaryPassword,
            "baseUrl", baseUrl,
            "supportEmail", supportEmail,
            "supportPhone", supportPhone
        );
        
        String htmlBody = replaceVariables(template, variables);
        
        String subject = "Your Facilon Platform Login Credentials";
        emailService.sendHtmlMessage(email, subject, htmlBody);
        log.info("Login details email sent to: {}", email);
    } catch (Exception e) {
        log.error("Failed to send login details email", e);
    }
}
```

#### Option B: Use Thymeleaf Template Engine (Recommended)

Add Thymeleaf dependency to `pom.xml`:

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-thymeleaf</artifactId>
</dependency>
```

Convert templates to Thymeleaf format (replace `{{variable}}` with `${variable}`).

Use Thymeleaf to render:

```java
@Autowired
private TemplateEngine templateEngine;

public void sendLoginDetailsEmail(String email, String investorName, String loginId, 
                                  String temporaryPassword, String investorCode) {
    try {
        Context context = new Context();
        context.setVariable("investorName", investorName);
        context.setVariable("loginId", loginId);
        context.setVariable("investorCode", investorCode);
        context.setVariable("temporaryPassword", temporaryPassword);
        context.setVariable("baseUrl", baseUrl);
        context.setVariable("supportEmail", supportEmail);
        context.setVariable("supportPhone", supportPhone);
        
        String htmlBody = templateEngine.process("01-login-details", context);
        
        String subject = "Your Facilon Platform Login Credentials";
        emailService.sendHtmlMessage(email, subject, htmlBody);
        log.info("Login details email sent to: {}", email);
    } catch (Exception e) {
        log.error("Failed to send login details email", e);
    }
}
```

### 2. Update EmailService

Ensure your `EmailService` supports HTML emails:

```java
public void sendHtmlMessage(String to, String subject, String htmlBody) {
    try {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
        
        helper.setFrom(fromEmail);
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(htmlBody, true); // true = isHtml
        
        mailSender.send(message);
    } catch (Exception e) {
        log.error("Failed to send HTML email", e);
        throw new RuntimeException("Email sending failed", e);
    }
}
```

## Template Variables

### Common Variables (All Templates)

- `baseUrl` - Application base URL (e.g., https://facilon.com)
- `supportEmail` - Support email address
- `supportPhone` - Support phone number

### Individual-Specific Variables

- `investorName` - Investor's full name
- `investorCode` - Unique investor code
- `uniqueCode` - Registration unique code
- `loginId` - Login ID for the investor
- `temporaryPassword` - Initial temporary password
- `loginUrl` - Direct login URL

### Legal Entity-Specific Variables

- `entityName` - Legal entity name
- `representativeName` - Authorized representative name

### Service Provider Variables

- `serviceProviderName` - Name of the service provider/broker
- `spName` - Service provider name (for SP notifications)
- `spEmail` - Service provider email
- `appointmentUrl` - URL to schedule appointment

### Other Variables

- `market` - Market name (e.g., "USA", "Singapore")
- `investorEmail` - Investor's email address

## Design Features

### Color Scheme

- **Primary Gradient**: Purple (`#667eea` to `#764ba2`)
- **Success**: Green (`#28a745` to `#20c997`)
- **Warning**: Yellow/Orange (`#ffc107`)
- **Danger/Alert**: Red (`#dc3545` to `#c82333`)
- **Info**: Cyan (`#17a2b8` to `#138496`)

### Layout Features

1. **Responsive Design**: Works on all devices (mobile, tablet, desktop)
2. **Maximum Width**: 700-750px for optimal readability
3. **Sections**: Clear visual separation with colored borders
4. **Icons**: Emoji icons for better visual appeal
5. **Buttons**: Gradient buttons with hover effects (in supported clients)
6. **Tables**: Properly formatted data tables
7. **Lists**: Checkmark lists for better readability

### Email Client Compatibility

These templates are tested and compatible with:
- Gmail (Web, iOS, Android)
- Outlook (Desktop, Web)
- Apple Mail (macOS, iOS)
- Yahoo Mail
- Thunderbird
- Most modern email clients

## Testing

### Test in Different Clients

1. **Gmail**: Primary testing platform
2. **Outlook**: Test for Microsoft compatibility
3. **Mobile**: Test on iOS and Android devices
4. **Dark Mode**: Check appearance in dark mode

### Testing Tools

- **Litmus**: Professional email testing across 100+ clients
- **Email on Acid**: Another professional testing service
- **Mailtrap**: Test emails in development without sending real emails

### Test Email Service

```java
// Add a test endpoint to send sample emails
@GetMapping("/test-email/{templateNumber}")
public ResponseEntity<String> testEmail(@PathVariable int templateNumber) {
    // Send test email with sample data
    return ResponseEntity.ok("Test email sent");
}
```

## Customization

### Changing Colors

To change the color scheme, update the CSS in each template:

```css
/* Header gradient */
.header {
    background: linear-gradient(135deg, #YOUR_COLOR_1 0%, #YOUR_COLOR_2 100%);
}

/* Button gradient */
.button {
    background: linear-gradient(135deg, #YOUR_COLOR_1 0%, #YOUR_COLOR_2 100%);
}
```

### Adding Logos

To add company logo to emails:

```html
<div class="header">
    <img src="https://yourdomain.com/logo.png" alt="Facilon Logo" style="max-width: 150px; margin-bottom: 10px;">
    <h1>Welcome to Facilon!</h1>
</div>
```

### Modifying Layout

Each template uses inline CSS for maximum compatibility. To modify:

1. Locate the relevant style in the `<style>` tag
2. Update the CSS properties
3. Test in multiple email clients

## Best Practices

1. **Keep Templates Updated**: When business logic changes, update templates
2. **Test Before Deploying**: Always test in multiple email clients
3. **Monitor Deliverability**: Track email open rates and delivery success
4. **A/B Testing**: Test different subject lines and content
5. **Accessibility**: Ensure templates are accessible (good contrast, alt text for images)
6. **Spam Compliance**: Follow CAN-SPAM and GDPR guidelines
7. **Unsubscribe Links**: Add unsubscribe options where required
8. **Plain Text Fallback**: Provide plain text version for clients that don't support HTML

## Maintenance

### Regular Updates

- Review templates quarterly for accuracy
- Update links and resources as needed
- Refresh design to match current branding
- Update compliance text as regulations change

### Version Control

All templates should be version controlled in Git. Use semantic versioning for major template redesigns.

## Support

For questions or issues with email templates:

- **Technical Issues**: Contact development team
- **Design Updates**: Contact UX/UI team  
- **Content Changes**: Contact marketing/legal team

---

**Last Updated:** February 11, 2026  
**Version:** 1.0  
**Author:** AI Implementation Team
