package com.mouken.modules.role.service;

import com.mouken.modules.role.entity.RoleHierarchy;
import com.mouken.modules.role.repository.RoleHierarchyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Iterator;

@Service
@Transactional
@RequiredArgsConstructor
public class RoleHierarchyService {

    private final RoleHierarchyRepository roleHierarchyRepository;

    public String findAllHierarchy() {
        Iterator<RoleHierarchy> iter = roleHierarchyRepository.findAll().iterator();
        StringBuffer concatenatedRoles = new StringBuffer();
        while (iter.hasNext()) {
            RoleHierarchy model = iter.next();
            if (model.getParent() != null) {
                concatenatedRoles.append(model.getParent().getRoleName());
                concatenatedRoles.append(" > ");
                concatenatedRoles.append(model.getRoleName());
                concatenatedRoles.append("\n");
            }
        }
        return concatenatedRoles.toString();
    }

}