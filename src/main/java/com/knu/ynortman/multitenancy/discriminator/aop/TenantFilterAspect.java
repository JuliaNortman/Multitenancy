package com.knu.ynortman.multitenancy.discriminator.aop;

import javax.persistence.EntityManager;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.hibernate.Session;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;

import com.knu.ynortman.multitenancy.util.TenantContext;

import lombok.extern.slf4j.Slf4j;

@Aspect
@ConditionalOnProperty(name = "multitenancy.strategy", havingValue = "discriminator")
@Slf4j
public class TenantFilterAspect {

    /*@Pointcut("execution (* org.hibernate.internal.SessionFactoryImpl.SessionBuilderImpl.openSession(..))")
    public void openSession() {
    }

    @AfterReturning(pointcut = "openSession()", returning = "session")
    public void afterOpenSession(Object session) {
        if (session != null && Session.class.isInstance(session)) {
            final String tenantId = TenantContext.getTenantId();
            if (tenantId != null) {
            	log.info("ENABLE FILTER");
                org.hibernate.Filter filter = ((Session) session).enableFilter("tenantFilter");
                filter.setParameter("tenantId", tenantId);
            }
        }
    }*/
	
	@AfterReturning(
            pointcut = "bean(pmsStayEntityManager) && execution(* createEntityManager(..))",
            returning = "retVal")
    public void getSessionAfter(JoinPoint joinPoint, Object retVal) throws Exception {
        if (retVal != null && EntityManager.class.isInstance(retVal) ) {
            addTenantFilter((EntityManager) retVal);
        }
    }

    public static void addTenantFilter(EntityManager entityManager) {
    	final String tenantId = TenantContext.getTenantId();
        if(tenantId != null) {
            Session session = entityManager.unwrap(Session.class);
            session.enableFilter("tenantFilter").setParameter("tenantId", tenantId).validate();
        }
    }

}


/*@Aspect
@Component
public class EnableFilterAspect {

    @AfterReturning(
            pointcut = "bean(pmsStayEntityManager) && execution(* createEntityManager(..))",
            returning = "retVal")
    public void getSessionAfter(JoinPoint joinPoint, Object retVal) throws Exception {
        if (retVal != null && EntityManager.class.isInstance(retVal) ) {
            addTenantFilter((EntityManager) retVal);
        }
    }

    public static void addTenantFilter(EntityManager entityManager) {
        if(!ObjectUtils.isEmpty(TenantContextHolder.getTenantInfo())
                && !ObjectUtils.isEmpty(TenantContextHolder.getTenantInfo().getTenantId())) {
            Session session = entityManager.unwrap(Session.class);
            session.enableFilter(TENANT_FILTER).setParameter(TENANT_ID_PARAM, TenantContextHolder.getTenantInfo().getTenantId()).validate();
        }
    }

}*/