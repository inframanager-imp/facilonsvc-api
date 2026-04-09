# Graph Email Microservice Extraction Guide

This guide explains how to extract the Graph Email module as a standalone microservice.

---

## Why Extract as Microservice?

✅ **Independent Scaling** - Scale email service separately from main API  
✅ **Technology Independence** - Update/replace without affecting main app  
✅ **Fault Isolation** - Email failures don't crash main application  
✅ **Team Autonomy** - Different teams can own different services  
✅ **Easier Testing** - Test email functionality in isolation  
✅ **Deployment Flexibility** - Deploy email service independently

---

## Current Architecture (Embedded Module)

```
┌───────────────────────────────────────┐
│     Facilon-platform-API              │
│                                       │
│  ┌─────────────────────────────┐    │
│  │  Graph Email Module         │    │
│  │  - GraphEmailController     │    │
│  │  - GraphEmailService        │    │
│  │  - GraphEmailTokenProvider  │    │
│  └─────────────────────────────┘    │
│                                       │
│  Other Services...                    │
└───────────────────────────────────────┘
```

---

## Target Architecture (Microservice)

```
┌──────────────────────┐      ┌──────────────────────┐
│ Facilon-platform-API │──────│ Graph Email Service  │
│                      │ HTTP │                      │
│ - EmailClient        │─────▶│ - GraphEmailController│
│ - Business Logic     │      │ - GraphEmailService  │
│                      │      │ - TokenProvider      │
└──────────────────────┘      └──────────────────────┘
```

---

## Step-by-Step Extraction

### Step 1: Create New Spring Boot Project

```bash
# Create new project
mkdir facilon-graph-email-service
cd facilon-graph-email-service
```

Create `pom.xml`:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0">
    <modelVersion>4.0.0</modelVersion>
    
    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>3.4.3</version>
    </parent>
    
    <groupId>com.facilon</groupId>
    <artifactId>graph-email-service</artifactId>
    <version>1.0.0</version>
    <name>Facilon Graph Email Service</name>
    <description>Microservice for sending emails via Microsoft Graph API</description>
    
    <properties>
        <java.version>17</java.version>
    </properties>
    
    <dependencies>
        <!-- Spring Boot Web -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
        
        <!-- Spring Boot Actuator (Health checks) -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-actuator</artifactId>
        </dependency>
        
        <!-- Spring Boot Security (Optional) -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-security</artifactId>
        </dependency>
        
        <!-- Lombok -->
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
            <optional>true</optional>
        </dependency>
        
        <!-- Jackson (JSON) -->
        <dependency>
            <groupId>com.fasterxml.jackson.core</groupId>
            <artifactId>jackson-databind</artifactId>
        </dependency>
        
        <!-- Swagger/OpenAPI -->
        <dependency>
            <groupId>org.springdoc</groupId>
            <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
            <version>2.8.4</version>
        </dependency>
        
        <!-- Testing -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>
    </dependencies>
    
    <build>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
            </plugin>
        </plugins>
    </build>
</project>
```

### Step 2: Copy Module Files

Copy these files from `com.facilon.app.integration.graphemail`:

```
src/main/java/com/facilon/graphemail/
├── GraphEmailApplication.java           (NEW - main class)
├── controller/
│   └── GraphEmailController.java        (COPIED)
├── service/
│   ├── GraphEmailService.java           (COPIED)
│   └── GraphEmailTokenProvider.java     (COPIED)
├── config/
│   ├── GraphEmailConfig.java            (COPIED)
│   └── SecurityConfig.java              (NEW)
└── dto/
    ├── GraphEmailRequestDto.java        (COPIED)
    └── GraphEmailResponseDto.java       (COPIED)
```

### Step 3: Create Main Application Class

`GraphEmailApplication.java`:

```java
package com.facilon.graphemail;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class GraphEmailApplication {
    public static void main(String[] args) {
        SpringApplication.run(GraphEmailApplication.class, args);
    }
}
```

### Step 4: Configure Application

`src/main/resources/application.yml`:

```yaml
server:
  port: 8083
  servlet:
    context-path: /graph-email

spring:
  application:
    name: graph-email-service

graph:
  email:
    enabled: true
    tenant-id: ${GRAPH_EMAIL_TENANT_ID}
    client-id: ${GRAPH_EMAIL_CLIENT_ID}
    client-secret: ${GRAPH_EMAIL_CLIENT_SECRET}
    sender-email: ${GRAPH_EMAIL_SENDER:noreply@facilon.com}
    sender-name: Facilon Platform

management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics
  endpoint:
    health:
      show-details: always

logging:
  level:
    com.facilon.graphemail: INFO
    root: INFO
```

### Step 5: Update Main API to Call Microservice

In `Facilon-platform-API`, create a client:

`GraphEmailMicroserviceClient.java`:

```java
package com.facilon.app.integration.graphemail.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@Slf4j
@ConditionalOnProperty(name = "services.graph-email.enabled", havingValue = "true")
public class GraphEmailMicroserviceClient {

    private final RestTemplate restTemplate = new RestTemplate();
    
    @Value("${services.graph-email.url}")
    private String graphEmailServiceUrl;

    public boolean sendEmail(String toEmail, String subject, String htmlContent) {
        try {
            String url = graphEmailServiceUrl + "/api/send";
            
            Map<String, Object> request = new HashMap<>();
            request.put("toEmail", toEmail);
            request.put("subject", subject);
            request.put("htmlContent", htmlContent);
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(request, headers);
            ResponseEntity<String> response = restTemplate.postForEntity(url, entity, String.class);
            
            return response.getStatusCode().is2xxSuccessful();
        } catch (Exception e) {
            log.error("Failed to send email via microservice: {}", e.getMessage());
            return false;
        }
    }
    
    public boolean sendOtpEmail(String toEmail, String firstName, String otp) {
        try {
            String url = graphEmailServiceUrl + "/api/send-otp"
                + "?toEmail=" + toEmail
                + "&firstName=" + firstName
                + "&otp=" + otp;
            
            ResponseEntity<String> response = restTemplate.postForEntity(url, null, String.class);
            return response.getStatusCode().is2xxSuccessful();
        } catch (Exception e) {
            log.error("Failed to send OTP via microservice: {}", e.getMessage());
            return false;
        }
    }
}
```

Add configuration in `application-dev.yml`:

```yaml
services:
  graph-email:
    enabled: false  # Set to true when microservice is deployed
    url: ${GRAPH_EMAIL_SERVICE_URL:http://localhost:8083/graph-email}
```

### Step 6: Build and Run

```bash
# Build microservice
cd facilon-graph-email-service
mvn clean package

# Run microservice
java -jar target/graph-email-service-1.0.0.jar

# Or using Maven
mvn spring-boot:run
```

### Step 7: Docker Containerization

Create `Dockerfile`:

```dockerfile
FROM maven:3.8-openjdk-17 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

FROM openjdk:17-jdk-slim
WORKDIR /app
COPY --from=build /app/target/graph-email-service-1.0.0.jar app.jar
EXPOSE 8083
ENTRYPOINT ["java", "-jar", "app.jar"]
```

Build Docker image:

```bash
docker build -t facilon/graph-email-service:1.0.0 .
```

### Step 8: Docker Compose

`docker-compose.yml`:

```yaml
version: '3.8'

services:
  facilon-api:
    image: facilon/facilon-api:latest
    ports:
      - "8082:8082"
    environment:
      - GRAPH_EMAIL_SERVICE_URL=http://graph-email:8083/graph-email
      - SERVICES_GRAPH_EMAIL_ENABLED=true
    depends_on:
      - graph-email

  graph-email:
    image: facilon/graph-email-service:1.0.0
    ports:
      - "8083:8083"
    environment:
      - GRAPH_EMAIL_TENANT_ID=${GRAPH_EMAIL_TENANT_ID}
      - GRAPH_EMAIL_CLIENT_ID=${GRAPH_EMAIL_CLIENT_ID}
      - GRAPH_EMAIL_CLIENT_SECRET=${GRAPH_EMAIL_CLIENT_SECRET}
      - GRAPH_EMAIL_SENDER=noreply@facilon.com
```

### Step 9: Kubernetes Deployment

`k8s/graph-email-deployment.yaml`:

```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: graph-email-service
spec:
  replicas: 2
  selector:
    matchLabels:
      app: graph-email
  template:
    metadata:
      labels:
        app: graph-email
    spec:
      containers:
      - name: graph-email
        image: facilon/graph-email-service:1.0.0
        ports:
        - containerPort: 8083
        env:
        - name: GRAPH_EMAIL_TENANT_ID
          valueFrom:
            secretKeyRef:
              name: graph-email-secrets
              key: tenant-id
        - name: GRAPH_EMAIL_CLIENT_ID
          valueFrom:
            secretKeyRef:
              name: graph-email-secrets
              key: client-id
        - name: GRAPH_EMAIL_CLIENT_SECRET
          valueFrom:
            secretKeyRef:
              name: graph-email-secrets
              key: client-secret
        livenessProbe:
          httpGet:
            path: /graph-email/actuator/health
            port: 8083
          initialDelaySeconds: 30
          periodSeconds: 10
        readinessProbe:
          httpGet:
            path: /graph-email/actuator/health
            port: 8083
          initialDelaySeconds: 10
          periodSeconds: 5
---
apiVersion: v1
kind: Service
metadata:
  name: graph-email-service
spec:
  selector:
    app: graph-email
  ports:
  - port: 8083
    targetPort: 8083
  type: ClusterIP
```

---

## Testing the Microservice

### Test Health Check

```bash
curl http://localhost:8083/graph-email/actuator/health
```

### Test Email Sending

```bash
curl -X POST http://localhost:8083/graph-email/api/send-otp \
  -d "toEmail=test@example.com" \
  -d "firstName=John" \
  -d "otp=1234"
```

### Test from Main API

```bash
# Main API should now call microservice
curl -X POST http://localhost:8082/facilon/api/clients/onboarding/register/step1 \
  -H "Content-Type: application/json" \
  -d '{"email":"test@example.com", ...}'
```

---

## Migration Strategy

### Phase 1: Parallel Run (Testing)
- Keep embedded module enabled
- Deploy microservice
- Route 10% of traffic to microservice
- Monitor for errors

### Phase 2: Gradual Migration
- Increase traffic to microservice (25%, 50%, 75%)
- Monitor performance and errors
- Keep embedded module as fallback

### Phase 3: Complete Migration
- Route 100% traffic to microservice
- Disable embedded module (set `graph.email.enabled=false`)
- Remove embedded module code (optional)

---

## Monitoring & Observability

### Metrics to Track

```yaml
management:
  metrics:
    tags:
      application: graph-email-service
    export:
      prometheus:
        enabled: true
```

**Key Metrics:**
- Email send success rate
- Email send latency
- Token refresh rate
- API error rate
- Queue depth (if using async)

### Logging

```yaml
logging:
  level:
    com.facilon.graphemail: DEBUG
  pattern:
    console: "%d{yyyy-MM-dd HH:mm:ss} [%thread] %-5level %logger{36} - %msg%n"
```

---

## Security Considerations

✅ Use Kubernetes secrets for credentials  
✅ Enable HTTPS/TLS  
✅ Implement API key authentication  
✅ Rate limiting on endpoints  
✅ Input validation  
✅ Monitor for abuse

---

## Rollback Plan

If microservice fails:

1. **Set** `services.graph-email.enabled=false` in main API
2. **Set** `graph.email.enabled=true` (use embedded module)
3. **Restart** main API
4. **Fix** microservice issue
5. **Redeploy** and retry

---

## Performance Optimization

### Async Email Sending

```java
@Async
public CompletableFuture<Boolean> sendEmailAsync(String toEmail, ...) {
    boolean sent = sendEmail(toEmail, ...);
    return CompletableFuture.completedFuture(sent);
}
```

### Connection Pooling

```java
@Bean
public RestTemplate restTemplate() {
    HttpComponentsClientHttpRequestFactory factory = 
        new HttpComponentsClientHttpRequestFactory();
    factory.setConnectTimeout(5000);
    factory.setReadTimeout(5000);
    return new RestTemplate(factory);
}
```

---

## Cost Comparison

| Aspect | Embedded Module | Microservice |
|--------|----------------|--------------|
| Infrastructure | No additional cost | Additional VM/Container |
| Development | Already done | Minimal (extraction) |
| Maintenance | Part of main app | Separate deployment |
| Scaling | Scale entire app | Scale only email service |
| Complexity | Lower | Higher (network calls) |

---

## Summary

✅ Module is ready for extraction  
✅ All components are self-contained  
✅ REST API already exposed  
✅ Configuration externalized  
✅ Can run independently  
✅ Docker and Kubernetes ready  

**Recommendation:** Start with embedded module, extract to microservice when:
- Email volume increases significantly
- Need independent scaling
- Multiple teams need to use it
- Want better fault isolation

---

**Last Updated:** February 11, 2026  
**Version:** 1.0.0
