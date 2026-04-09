package com.facilon.app.config;




import com.facilon.app.model.Tenant;

import java.util.HashMap;
import java.util.Map;

;

public class TenantContext {

    private static final String TENANT = "tenant";

    private Map<String, Object> context = new HashMap<>();

    public TenantContext(Tenant tenant) {
        context.put(TENANT, tenant);
    }

    public TenantContext(Long tenantId) {
        this(getTenant(tenantId));
    }

    private static Tenant getTenant(Long tenantId) {
        Tenant tenant = new Tenant();
        tenant.setTenantId(tenantId);
        return tenant;
    }

    public Tenant getTenant() {
        return (Tenant) context.get(TENANT);
    }

    public Long getTenantId() {
        return getTenant().getTenantId();
    }
}
