package com.facilon.app.config;


import com.facilon.app.annotations.CurrentTenant;
import com.facilon.app.annotations.Tenant;
import com.facilon.app.annotations.WithoutTenant;
import jakarta.persistence.EntityManager;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;

@Aspect
@Component
public class MultiTenantAspect {

    private static final Logger logger = LoggerFactory.getLogger(MultiTenantAspect.class);

    private final EntityManager entityManager;

    public MultiTenantAspect(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Pointcut(value = "@within(com.prismx.ai.annotations.CurrentTenant) || @annotation(com.prismx.ai.annotations.CurrentTenant)")
    void hasCurrentTenantAnnotation() {
        logger.debug("Checking for @CurrentTenant annotation...");
    }

    @Pointcut(value = "@within(com.prismx.ai.annotations.Tenant) || @annotation(com.prismx.ai.annotations.Tenant)")
    void hasTenantAnnotation() {
        logger.debug("Checking for @Tenant annotation...");
    }

    @Pointcut(value = "@within(com.prismx.ai.annotations.WithoutTenant) || @annotation(com.prismx.ai.annotations.WithoutTenant)")
    void hasWithoutTenantAnnotation() {
        logger.debug("Checking for @WithoutTenant annotation...");
    }

    @Pointcut(value = "hasCurrentTenantAnnotation() || hasTenantAnnotation() || hasWithoutTenantAnnotation()")
    void hasMultiTenantAnnotation() {
        logger.debug("Composed multi-tenant pointcut active...");
    }

    @Around("execution(public * *(..)) && hasMultiTenantAnnotation()")
    public Object aroundExecution(ProceedingJoinPoint pjp) throws Throwable {
        logger.debug("🔁 Entering AOP around advice for multi-tenancy");

        final String methodName = pjp.getSignature().getName();
        final MethodSignature methodSignature = (MethodSignature) pjp.getSignature();
        Method method = methodSignature.getMethod();
        logger.debug("Method intercepted: {}", methodName);

        if (method.getDeclaringClass().isInterface()) {
            logger.debug("Resolving method from target class due to interface declaration...");
            method = pjp.getTarget().getClass().getDeclaredMethod(methodName, method.getParameterTypes());
        }

        Annotation multiTenantAnnotation = getMultiTenantAnnotation(method);
        if (multiTenantAnnotation == null) {
            logger.debug("Annotation not found on method. Checking class-level annotation...");
            multiTenantAnnotation = getMultiTenantAnnotation(method.getDeclaringClass());
        }

        if (multiTenantAnnotation != null) {
            logger.debug("Annotation found: {}", multiTenantAnnotation.annotationType().getSimpleName());

            if (!(multiTenantAnnotation instanceof WithoutTenant)) {
                Serializable tenantId = TenantContextHolder.getContext().getTenantId();
                logger.debug("Resolved tenant ID from context: {}", tenantId);

                if (multiTenantAnnotation instanceof Tenant) {
                    tenantId = Long.parseLong(((Tenant) multiTenantAnnotation).value());
                    logger.debug("Overriding tenant ID with static value: {}", tenantId);
                }

                logger.debug("Enabling Hibernate tenantFilter with tenantId={}", tenantId);
                org.hibernate.Filter filter = entityManager.unwrap(Session.class)
                        .enableFilter("tenantFilter");
                filter.setParameter("tenantId", tenantId);
                filter.validate();
                logger.debug("Hibernate filter validated.");
            } else {
                logger.debug("@WithoutTenant present. Skipping tenant filter.");
            }
        } else {
            logger.debug("No multi-tenant annotation found. Proceeding without filter.");
        }

        Object result = pjp.proceed();
        logger.debug("✅ Exiting AOP around advice for method: {}", methodName);
        return result;
    }

    private Annotation getMultiTenantAnnotation(AnnotatedElement element) {
        logger.debug("Checking for annotations on: {}", element);
        Annotation annotation = element.getAnnotation(CurrentTenant.class);
        if (annotation != null) {
            logger.debug("Found @CurrentTenant");
            return annotation;
        }
        annotation = element.getAnnotation(Tenant.class);
        if (annotation != null) {
            logger.debug("Found @Tenant");
            return annotation;
        }
        annotation = element.getAnnotation(WithoutTenant.class);
        if (annotation != null) {
            logger.debug("Found @WithoutTenant");
            return annotation;
        }
        logger.debug("No tenant-related annotation found.");
        return null;
    }
}
