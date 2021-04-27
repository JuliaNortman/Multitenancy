package com.knu.ynortman.multitenancy.interceptor;

import com.knu.ynortman.multitenancy.util.TenantContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.ui.ModelMap;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.context.request.WebRequestInterceptor;

@Component
@Slf4j
public class TenantInterceptor implements WebRequestInterceptor {

    private final String HEADER = "X-TENANT-ID";
    /*private final String defaultTenant;

    @Autowired
    public TenantInterceptor(
            @Value("${multitenancy.tenant.default-tenant:#{null}}") String defaultTenant) {
        this.defaultTenant = defaultTenant;
        log.info("Default tenant: " + defaultTenant);
    }*/

    @Override
    public void preHandle(WebRequest request) {
        /*String tenantId;
        if (request.getHeader(HEADER) != null) {
            tenantId = request.getHeader(HEADER);
        } else if (this.defaultTenant != null) {
            tenantId = this.defaultTenant;
        } else {
            tenantId = ((ServletWebRequest)request).getRequest().getServerName().split("\\.")[0];
        }*/
        //TenantContext.setTenantId(tenantId);
        TenantContext.setTenantId(request.getHeader(HEADER));
        log.info("SELECTED TENANT ID IS " + TenantContext.getTenantId());
        //log.info("SELECTED TENANT ID IS " + TenantContext.getTenantId());
        //log.info("HERE");
    }

    @Override
    public void postHandle(@NonNull WebRequest request, ModelMap model) {
        TenantContext.clear();
    }

    @Override
    public void afterCompletion(@NonNull WebRequest request, Exception ex) {
        // NOOP
    }
}