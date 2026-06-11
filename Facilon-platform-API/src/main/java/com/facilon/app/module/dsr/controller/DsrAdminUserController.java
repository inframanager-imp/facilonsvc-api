package com.facilon.app.module.dsr.controller;

import com.facilon.app.module.dsr.dto.DsrAdminRegisterRequestDto;
import com.facilon.app.module.dsr.dto.DsrAdminUserDto;
import com.facilon.app.module.dsr.service.DsrAdminUserService;
import com.facilon.app.security.UserPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

/**
 * Registration and management of DSR Admin (Privacy Ops) users. Restricted to
 * platform admins - DSR Admins themselves cannot register further admins.
 */
@RestController
@RequestMapping("/api/admin/dsr/admins")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "DSR Admin Users", description = "Register and manage DSR Admin (Privacy Ops) users")
@PreAuthorize("hasAnyAuthority('ADMIN','PLATFORM_SUPER_ADMIN')")
public class DsrAdminUserController {

    private final DsrAdminUserService dsrAdminUserService;

    @GetMapping
    @Operation(summary = "List registered DSR Admin users")
    public ResponseEntity<List<DsrAdminUserDto>> list() {
        return ResponseEntity.ok(dsrAdminUserService.list());
    }

    @GetMapping("/me")
    @Operation(summary = "Profile of the logged-in admin (dashboard welcome banner)")
    @PreAuthorize("hasAnyAuthority('ADMIN','PLATFORM_SUPER_ADMIN','DSR_ADMIN')")
    public ResponseEntity<DsrAdminUserDto> me(Authentication authentication) {
        if (authentication == null || !(authentication.getPrincipal() instanceof UserPrincipal principal)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        return ResponseEntity.ok(dsrAdminUserService.me(principal.getId()));
    }

    @PostMapping
    @Operation(summary = "Register a DSR Admin user (DSR_ADMIN role assigned automatically)")
    public ResponseEntity<DsrAdminUserDto> register(@RequestBody DsrAdminRegisterRequestDto dto) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(dsrAdminUserService.register(dto));
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @DeleteMapping("/{userId}")
    @Operation(summary = "Deactivate a DSR Admin user (soft delete)")
    public ResponseEntity<DsrAdminUserDto> deactivate(@PathVariable Long userId) {
        try {
            return ResponseEntity.ok(dsrAdminUserService.deactivate(userId));
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }
}
