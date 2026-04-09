package com.facilon.app.config;


import com.facilon.app.model.TenantEntity;
import org.hibernate.Interceptor;
import org.hibernate.type.Type;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MultiTenantInterceptor implements Interceptor {
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Override
    public boolean onPersist(
            Object entity,
            Object id,
            Object[] state,
            String[] propertyNames,
            Type[] types) {

        if (entity instanceof TenantEntity) {
            log.debug("[persist] Setting tenant on entity {}: {}", id, TenantContextHolder.getContext().getTenant());
            ((TenantEntity) entity).setTenant(TenantContextHolder.getContext().getTenant());

            int index = findIndex(propertyNames);
            if (index != -1) {
                state[index] = TenantContextHolder.getContext().getTenant();
            }
        }
        return true;
    }

    @Override
    public void onRemove(
            Object entity,
            Object id,
            Object[] state,
            String[] propertyNames,
            Type[] types) {

        if (entity instanceof TenantEntity) {
            log.debug("[remove] Processing tenant entity removal: {}", id);
        }
    }

    @Override
    public boolean onFlushDirty(
            Object entity,
            Object id,
            Object[] currentState,
            Object[] previousState,
            String[] propertyNames,
            Type[] types) {

        if (entity instanceof TenantEntity) {
            log.debug("[flush-dirty] Updating tenant on entity {}: {}", id, TenantContextHolder.getContext().getTenant());
            ((TenantEntity) entity).setTenant(TenantContextHolder.getContext().getTenant());

            int index = findIndex(propertyNames);
            if (index != -1) {
                currentState[index] = TenantContextHolder.getContext().getTenant();
            }
        }
        return true;
    }

    private int findIndex(String[] array) {
        for (int i = 0; i < array.length; i++) {
            if (array[i].equals("tenant")) return i;
        }
        return -1;
    }
}
