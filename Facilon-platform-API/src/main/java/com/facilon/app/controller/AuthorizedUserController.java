package com.facilon.app.controller;

import com.facilon.app.annotations.CurrentTenant;
import com.facilon.app.dto.AuthorizedUserDto;
import com.facilon.app.dto.PasswordChangeDto;
import com.facilon.app.model.AuthorizedUser;
import com.facilon.app.model.PasswordResetToken;

import com.facilon.app.repository.AuthorizedUserRepository;
import com.facilon.app.repository.PasswordResetTokenRepository;
import com.facilon.app.service.AuthorizedUserService;
import com.facilon.app.service.EmailService;
import com.facilon.app.util.EmailTemplateLoader;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/users")
@SecurityRequirement(name = "Bearer Authentication")
@CurrentTenant
public class AuthorizedUserController {
    @Autowired
    private AuthorizedUserService authorizedUserService;
    @Autowired
    private AuthorizedUserRepository authorizedUserRepository;

    @Autowired
    private PasswordResetTokenRepository passwordResetTokenRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private EmailService emailService;

    @Autowired
    private EmailTemplateLoader templateLoader;

    @Autowired
    private ModelMapper modelMapper;
    
    @Value("${app.support-email:support@facilon.com}")
    private String supportEmail;
    
    @Value("${app.support-phone:+91-123-456-7890}")
    private String supportPhone;

    @Autowired
    public AuthorizedUserController(AuthorizedUserService authorizedUserService, ModelMapper modelMapper) {
        this.authorizedUserService = authorizedUserService;
        this.modelMapper = modelMapper;
    }

    @PostMapping("/register")
    public ResponseEntity<AuthorizedUserDto> registerUser(@Valid @RequestBody AuthorizedUserDto userDto) {
        AuthorizedUserDto registeredUser = authorizedUserService.registerUser(userDto);
        return ResponseEntity.ok(registeredUser);
    }
    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestParam("email") String email) {
        // 1. Verify that the user exists by their email
        Optional<AuthorizedUser> userOptional = authorizedUserRepository.findByEmailId(email);
        if (!userOptional.isPresent()) {
            return ResponseEntity.badRequest().body("User not found.");
        }
        AuthorizedUser user = userOptional.get();

        // 2. Generate a reset token
        String token = UUID.randomUUID().toString();
        // Save the token with an expiration time (e.g., 24 hours from now) in your database
        PasswordResetToken resetToken = new PasswordResetToken(token, user, new Date(System.currentTimeMillis() + 24 * 60 * 60 * 1000));
        passwordResetTokenRepository.save(resetToken);

        // 3. Construct the reset link
        String resetLink = "http://localhost:4200/reset-password?token=" + token;

        // 4. Send the email
        Map<String, String> variables = new HashMap<>();
        variables.put("userName", user.getFirstName() + " " + user.getLastName());
        variables.put("resetLink", resetLink);
        variables.put("supportEmail", supportEmail);
        variables.put("supportPhone", supportPhone);
        
        String htmlBody = templateLoader.processTemplate("26-password-reset.html", variables);
        emailService.sendHtmlMessage(user.getEmailId(), "Password Reset Request", htmlBody);
        
        Map<String, String> response = new HashMap<>();
        response.put("message", "Password reset link sent");
        return ResponseEntity.ok(response);

    }
    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestParam("token") String token, @RequestParam("newPassword") String newPassword) {
        // 1. Validate the token
        Optional<PasswordResetToken> resetTokenOptional = passwordResetTokenRepository.findByToken(token);
        if (!resetTokenOptional.isPresent() || resetTokenOptional.get().isExpired()) {
            return ResponseEntity.badRequest().body("Invalid or expired password reset token.");
        }

        // 2. Update the user's password
        AuthorizedUser user = resetTokenOptional.get().getAuthorizedUser();
        String encodedPassword = passwordEncoder.encode(newPassword);
        user.setPassword(encodedPassword);
        authorizedUserRepository.save(user);

        // 3. Optionally, invalidate the token
        passwordResetTokenRepository.delete(resetTokenOptional.get());
        Map<String, String> response = new HashMap<>();
        response.put("message", "Password has been reset successfully.");
        return ResponseEntity.ok(response);

    }
    @PostMapping("/change-password")
    @Operation(summary = "Change user password", security = @SecurityRequirement(name = "Bearer Authentication"))
    public ResponseEntity<?> changePassword(@RequestBody PasswordChangeDto request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        String username = authentication.getName();
        AuthorizedUser  user = authorizedUserRepository.findByEmailId(username).get(); // Implement this method in your service

        if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            return ResponseEntity.badRequest().body(Map.of("message", "Old password is incorrect"));
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        user.setMustChangePassword(false); // Clear the flag after successful password change
        user.setLastPasswordChange(java.time.LocalDateTime.now());
        authorizedUserRepository.save(user); // Implement this method in your service

        Map<String, String> response = new HashMap<>();
        response.put("message", "Password changed successfully");

        return ResponseEntity.ok(response);
    }
    @GetMapping("/checkEmail")
    public ResponseEntity<Object> checkEmailExists(@RequestParam String email) {
        boolean exists = authorizedUserService.emailExists(email);
        return ResponseEntity.ok(Collections.singletonMap("emailExists", exists));
    }
    @GetMapping("/checkLoginId")
    public ResponseEntity<Object> checkLoginIdExists(@RequestParam String loginId) {
        boolean exists = authorizedUserService.loginIdExists(loginId);
        return ResponseEntity.ok(Collections.singletonMap("loginIdExists", exists));
    }


    @GetMapping("/{userId}/is-forwarder")
    public ResponseEntity<Boolean> isForwarder(@PathVariable Long userId) {
        return ResponseEntity.ok(authorizedUserService.isForwarder(userId));
    }

}
