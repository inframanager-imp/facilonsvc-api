package com.facilon.app.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import com.facilon.app.config.TenantContextHolder;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.FilterDef;
import org.hibernate.annotations.ParamDef;




@Setter
@Getter
@MappedSuperclass
@FilterDef(name = "tenantFilter", parameters = {@ParamDef(name = "tenantId", type = Long.class)})
@Filter(name = "tenantFilter", condition = "tenant_id = :tenantId")
@NoArgsConstructor
@AllArgsConstructor
public abstract class TenantEntity extends Auditable{

    public static final String TENANT_COLUMN = "tenant_id";

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = TENANT_COLUMN)
    @JsonIgnore
    private Tenant tenant;

     @PrePersist
     @PreUpdate
     @PreRemove
     public void onPrePersist() {
         // Only set tenant if context is available and tenant is not already set
         if (this.tenant == null) {
             try {
                 var context = TenantContextHolder.getContext();
                 if (context != null && context.getTenant() != null) {
                     this.tenant = context.getTenant();
                 }
             } catch (Exception e) {
                 // Log warning but don't fail - this might be a pre-login operation
                 // where tenant context is not available
                 System.err.println("Warning: Could not set tenant context: " + e.getMessage());
             }
         }
    }

}
