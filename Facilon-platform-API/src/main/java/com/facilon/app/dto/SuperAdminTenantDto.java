package com.facilon.app.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SuperAdminTenantDto {
    private Long tenantId;
    
    @NotBlank(message = "Tenant name is required")
    @Size(min = 2, max = 256, message = "Tenant name must be between 2 and 256 characters")
    private String tenantName;
    
    @NotBlank(message = "Email domain is required")
    @Size(min = 3, max = 256, message = "Email domain must be between 3 and 256 characters")
    private String emailDomain;
    
    @NotBlank(message = "Address is required")
    @Size(max = 256, message = "Address must not exceed 256 characters")
    private String address;
    
    @NotBlank(message = "State is required")
    @Size(max = 256, message = "State must not exceed 256 characters")
    private String state;
    
    @NotBlank(message = "Postal code is required")
    @Size(max = 256, message = "Postal code must not exceed 256 characters")
    private String postalCode;
    
    @NotBlank(message = "Country is required")
    @Size(max = 256, message = "Country must not exceed 256 characters")
    private String country;
    
    private Boolean active;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Long totalUsers;
}
