package com.azure.user_management.application.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user_message")
public class MessageController {
    @GetMapping("/get_message")
    public String getMessage() {
        return "Hello Hiteshkumar Desai";
    }
}
