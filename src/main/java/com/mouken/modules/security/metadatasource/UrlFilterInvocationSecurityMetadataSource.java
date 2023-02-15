package com.mouken.modules.security.metadatasource;

import com.mouken.modules.security.service.SecurityResourceService;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.web.FilterInvocation;
import org.springframework.security.web.access.intercept.FilterInvocationSecurityMetadataSource;
import org.springframework.security.web.util.matcher.RequestMatcher;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

public class UrlFilterInvocationSecurityMetadataSource implements FilterInvocationSecurityMetadataSource {

    private LinkedHashMap<RequestMatcher, List<ConfigAttribute>> requestMap;
    private SecurityResourceService securityResourceService;

    public UrlFilterInvocationSecurityMetadataSource(LinkedHashMap<RequestMatcher, List<ConfigAttribute>> requestMap, SecurityResourceService securityResourceService) {
        this.requestMap = requestMap;
        this.securityResourceService = securityResourceService;
    }

    @Override
    public Collection<ConfigAttribute> getAttributes(Object object) throws IllegalArgumentException {

        HttpServletRequest request = ((FilterInvocation) object).getRequest();

        if(requestMap != null){
            for(Map.Entry<RequestMatcher, List<ConfigAttribute>> entry : requestMap.entrySet()){
                RequestMatcher matcher = entry.getKey();
                if(matcher.matches(request)){
                    return entry.getValue(); // Authority
                }
            }
        }

        return null;
    }

    @Override
    public Collection<ConfigAttribute> getAllConfigAttributes() {
        Set<ConfigAttribute> allAttributes = new HashSet<>();

        for (Map.Entry<RequestMatcher, List<ConfigAttribute>> entry : requestMap
                .entrySet()) {
            allAttributes.addAll(entry.getValue());
        }

        return allAttributes;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return FilterInvocation.class.isAssignableFrom(clazz);
    }

    public void reload() {
        LinkedHashMap<RequestMatcher, List<ConfigAttribute>> reloadMap = securityResourceService.getResources();
        Iterator<Map.Entry<RequestMatcher, List<ConfigAttribute>>> iterator = reloadMap.entrySet().iterator();
        requestMap.clear();

        while(iterator.hasNext()) {
            Map.Entry<RequestMatcher, List<ConfigAttribute>> entry = iterator.next();
            requestMap.put(entry.getKey(), entry.getValue());
        }
    }

}