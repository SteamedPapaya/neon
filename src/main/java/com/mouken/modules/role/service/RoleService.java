package com.mouken.modules.role.service;

import com.mouken.modules.role.dto.RoleCreateForm;
import com.mouken.modules.role.dto.RoleModifyForm;
import com.mouken.modules.role.entity.Role;
import com.mouken.modules.role.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@Transactional
@RequiredArgsConstructor
public class RoleService {

    private final ModelMapper modelMapper;
    private final RoleRepository roleRepository;

    // todo other create methods to return that object
    // todo hierarchy logic
    public Role createRole(RoleCreateForm form) {
        Role role = modelMapper.map(form, Role.class);
        return roleRepository.save(role);
    }

    // todo hierarchy logic
    public void modifyRole(Long id, RoleModifyForm form){
        Role role = roleRepository.findById(id).orElseThrow();
        modelMapper.map(form, role);
        roleRepository.save(role);
    }

    public void deleteRole(long id) {
        roleRepository.deleteById(id);
    }

    public Role getRole(long id) {
        return roleRepository.findById(id).get();
    } // todo EH

    public RoleModifyForm getRoleForm(long id) {
        return modelMapper.map(roleRepository.findById(id).get(), RoleModifyForm.class);
    }

    public List<Role> getRoleList() {
        return roleRepository.findAll(Sort.by(Sort.Order.asc("id")));
    }

    public Set<Role> getRolesByName(List<String> nameList) { // todo EH
        Set<Role> roles = new HashSet<>();
        for(String name : nameList) {
            roles.add(roleRepository.findByName(name));
        }
        return roles;
    }

    public Set<Role> getDefaultRoles() {
        Set<Role> roles = new HashSet<>();
        roles.add(roleRepository.findByName("ROLE_USER")); // todo exception handling
        return roles;
    }

}
