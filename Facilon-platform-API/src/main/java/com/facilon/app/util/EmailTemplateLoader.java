package com.facilon.app.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;

/**
 * Utility class for loading and processing email templates
 * Templates are stored in src/main/resources/email-templates/
 * 
 * Supports simple variable replacement using {{variableName}} syntax
 */
@Component
@Slf4j
public class EmailTemplateLoader {

    private static final String TEMPLATE_BASE_PATH = "email-templates/";

    /**
     * Load a template file and replace variables
     * 
     * @param templateFileName Name of the template file (e.g., "28-otp-verification.html")
     * @param variables Map of variable names to values
     * @return Processed HTML content
     */
    public String processTemplate(String templateFileName, Map<String, String> variables) {
        try {
            String content = loadTemplate(templateFileName);
            return replaceVariables(content, variables);
        } catch (Exception e) {
            log.error("Error processing template {}: {}", templateFileName, e.getMessage(), e);
            return generateFallbackContent(variables);
        }
    }

    /**
     * Load template content from classpath
     */
    private String loadTemplate(String templateFileName) throws IOException {
        ClassPathResource resource = new ClassPathResource(TEMPLATE_BASE_PATH + templateFileName);
        if (!resource.exists()) {
            throw new IOException("Template not found: " + templateFileName);
        }
        
        try (var inputStream = resource.getInputStream()) {
            return new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        }
    }

    /**
     * Replace variables in template content
     * Supports {{variableName}} syntax
     */
    private String replaceVariables(String content, Map<String, String> variables) {
        if (variables == null || variables.isEmpty()) {
            return content;
        }

        String result = content;
        for (Map.Entry<String, String> entry : variables.entrySet()) {
            String placeholder = "{{" + entry.getKey() + "}}";
            String value = entry.getValue() != null ? entry.getValue() : "";
            result = result.replace(placeholder, value);
        }
        return result;
    }

    /**
     * Generate fallback content if template loading fails
     */
    private String generateFallbackContent(Map<String, String> variables) {
        StringBuilder sb = new StringBuilder();
        sb.append("<!DOCTYPE html><html><head><meta charset='UTF-8'></head><body>");
        sb.append("<div style='font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto; padding: 20px;'>");
        sb.append("<p>Dear User,</p>");
        sb.append("<p>This is an automated message from Facilon Platform.</p>");
        
        if (variables != null) {
            for (Map.Entry<String, String> entry : variables.entrySet()) {
                sb.append("<p><strong>").append(entry.getKey()).append(":</strong> ")
                  .append(entry.getValue()).append("</p>");
            }
        }
        
        sb.append("<p>Warm regards,<br>Team Facilon</p>");
        sb.append("</div></body></html>");
        return sb.toString();
    }
}
