package com.azure.user_management.application.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthController {
    @GetMapping("/")
    public String rootHealthCheck() {
        return "API is healthy and running";
    }
}
