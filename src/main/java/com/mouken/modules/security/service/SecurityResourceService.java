package com.mouken.modules.security.service;

import com.mouken.modules.accessIp.repository.AccessIpRepository;
import com.mouken.modules.resource.Resource;
import com.mouken.modules.resource.repository.ResourceRepository;
import com.mouken.modules.role.service.RoleHierarchyService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.access.SecurityConfig;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SecurityResourceService {

    private final ResourceRepository resourceRepository;
    private final AccessIpRepository accessIpRepository;
    private final RoleHierarchyImpl roleHierarchy;
    private final RoleHierarchyService roleHierarchyService;

    public LinkedHashMap<RequestMatcher, List<ConfigAttribute>> getResources() {
        LinkedHashMap<RequestMatcher, List<ConfigAttribute>> result = new LinkedHashMap<>();
        List<Resource> ResourceList = resourceRepository.findAllResource();
        ResourceList.forEach(resource -> {
            List<ConfigAttribute> configAttributeList = new ArrayList<>();
            resource.getRoles().forEach(role -> {
                configAttributeList.add(new SecurityConfig(role.getName()));
                result.put(new AntPathRequestMatcher(resource.getName()), configAttributeList);
            });
        });
        return result;
    }

    public void setRoleHierarchy() {
        String allHierarchy = roleHierarchyService.findAllHierarchy();
        roleHierarchy.setHierarchy(allHierarchy);
    }

    public List<String> getAccessIpList() {
        return accessIpRepository.findAll().stream().map(accessIp -> accessIp.getAddress()).collect(Collectors.toList());
    }

}
