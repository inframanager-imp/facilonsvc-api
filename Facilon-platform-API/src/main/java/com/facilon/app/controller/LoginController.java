package com.facilon.app.controller;

import com.facilon.app.exception.InvalidPasswordException;
import com.facilon.app.exception.UserNotFoundException;
import com.facilon.app.model.AuthorizedUser;
import com.facilon.app.security.JwtAuthenticationResponse;
import com.facilon.app.security.JwtTokenProvider;
import com.facilon.app.service.UserService;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.security.NoSuchAlgorithmException;

@RestController
@SecurityRequirement(name = "Bearer Authentication")
public class LoginController {
    public static final String TIMESTAMP = "timestamp";
    public static final String MESSAGE = "message";


    @Autowired
    private UserService userService;

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    JwtTokenProvider tokenProvider;

    @PostMapping("/api/login")
    public ResponseEntity<?> authenticateUser(@RequestBody AuthorizedUser authorizedUser)
            throws NoSuchAlgorithmException {

        return userService.findByEmailOrLoginId(authorizedUser.getLoginId())
                .map(existingUser -> {
                    if (!matchPassword(authorizedUser.getPassword(), existingUser.getPassword())) {
                        throw new InvalidPasswordException("Invalid password!");
                    }
                    Authentication authentication = authenticationManager.authenticate(
                            new UsernamePasswordAuthenticationToken(authorizedUser.getLoginId(), authorizedUser.getPassword()));
                    String jwt = tokenProvider.generateToken(authentication);
                    return ResponseEntity.ok(new JwtAuthenticationResponse(jwt, existingUser.isMustChangePassword()));
                })
                .orElseThrow(() -> new UserNotFoundException("User does not exist!"));
    }


    public  Boolean matchPassword(String rawPassword, String encodedPassword) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

        return encoder.matches(rawPassword, encodedPassword);

    }


}
