package com.mouken.modules.security.factory;

import com.mouken.modules.security.service.SecurityResourceService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.List;

@Component
@RequiredArgsConstructor
public class UrlResourcesMapFactoryBean implements FactoryBean<LinkedHashMap<RequestMatcher, List<ConfigAttribute>>> {

    private LinkedHashMap<RequestMatcher, List<ConfigAttribute>> resourceMap;
    private final SecurityResourceService securityResourceService;

    @Override
    public LinkedHashMap<RequestMatcher, List<ConfigAttribute>> getObject() throws Exception {
        
        if (resourceMap == null) {
            init();
        }
        
        return resourceMap;
    }

    public void init() {
        resourceMap = securityResourceService.getResources();
    }

    @Override
    public Class<?> getObjectType() {
        return LinkedHashMap.class;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

}
