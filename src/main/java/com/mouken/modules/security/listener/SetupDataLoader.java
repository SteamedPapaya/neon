package com.mouken.modules.security.listener;

import com.mouken.modules.accessIp.AccessIp;
import com.mouken.modules.accessIp.repository.AccessIpRepository;
import com.mouken.modules.account.Account;
import com.mouken.modules.account.repository.AccountRepository;
import com.mouken.modules.resource.Resource;
import com.mouken.modules.resource.repository.ResourceRepository;
import com.mouken.modules.role.entity.Role;
import com.mouken.modules.role.entity.RoleHierarchy;
import com.mouken.modules.role.repository.RoleHierarchyRepository;
import com.mouken.modules.role.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

@Component
@Transactional
@RequiredArgsConstructor
public class SetupDataLoader implements ApplicationListener<ContextRefreshedEvent> {

    private boolean isSetUp = false;
    private final PasswordEncoder passwordEncoder;
    private final AccountRepository accountRepository;
    private final RoleRepository roleRepository;
    private final ResourceRepository resourceRepository;
    private final AccessIpRepository accessIpRepository;
    private final RoleHierarchyRepository roleHierarchyRepository;
    private static AtomicInteger count = new AtomicInteger(0);


    @Override
    public void onApplicationEvent(final ContextRefreshedEvent event) {
        if (!isSetUp) {
            setUpAccountAndRoleAndResource();
            setUpIpBlacklist("14.6.230.128");
            isSetUp = true;
        }
    }

    private void setUpAccountAndRoleAndResource() {

        // for admin
        Set<Role> rolesAdmin = createRoleIfNotFound("ROLE_ADMIN");
        createResourceIfNotFound("/admin/**", "", rolesAdmin, "url");
        createUserIfNotFound("admin", "12345678", "admin@email.com", rolesAdmin);

        // for manager
        Set<Role> rolesManager = createRoleIfNotFound("ROLE_MANAGER");
        createResourceIfNotFound("/manager/**", "", rolesManager, "url");
        createUserIfNotFound("manager", "12345678", "manager@email.com", rolesManager);

        // for user
        Set<Role> rolesUser = createRoleIfNotFound("ROLE_USER");
        createResourceIfNotFound("/user/**", "", rolesUser, "url");
        createUserIfNotFound("user", "12345678", "user@email.com", rolesUser);

        // role hierarchy
        createRoleHierarchyIfNotFound("ROLE_MANAGER", "ROLE_ADMIN");
        createRoleHierarchyIfNotFound("ROLE_USER", "ROLE_MANAGER");
    }

    public Set<Role> createRoleIfNotFound(String roleName) {
        
        Role role = roleRepository.findByName(roleName);
        if (role == null) {
            role = roleRepository.save(
                    Role.builder()
                        .name(roleName)
                        .build()
            );
        }
        
        Set<Role> roles = new HashSet<>();
        roles.add(role);
        return roles;
    }

    public void createUserIfNotFound(String username, String password, String email, Set<Role> roleSet) {
        if (!accountRepository.existsByUsername(username)) {
            Account account = Account.builder()
                    .username(username)
                    .nickname(username)
                    .email(email)
                    .password(passwordEncoder.encode(password))
                    .roles(roleSet)
                    .build();
            accountRepository.save(account);
        }
    }

    public void createResourceIfNotFound(String name, String httpMethod, Set<Role> roleSet, String type) {
        if (!resourceRepository.existsByNameAndHttpMethod(name, httpMethod)) {
            Resource resource = Resource.builder()
                    .name(name)
                    .roles(roleSet)
                    .httpMethod(httpMethod)
                    .type(type)
                    .orderNum(count.incrementAndGet())
                    .build();
            resourceRepository.save(resource);
        }
    }

    public void createRoleHierarchyIfNotFound(String childRoleName, String parentRoleName) {

        Role childRole = roleRepository.findByName(childRoleName);
        Role parentRole = roleRepository.findByName(parentRoleName);

        RoleHierarchy roleHierarchy = roleHierarchyRepository.findByRoleName(parentRole.getName());
        if (roleHierarchy == null) {
            roleHierarchy = RoleHierarchy.builder()
                    .roleName(parentRole.getName())
                    .build();
        }
        RoleHierarchy parentRoleHierarchy = roleHierarchyRepository.save(roleHierarchy);

        roleHierarchy = roleHierarchyRepository.findByRoleName(childRole.getName());
        if (roleHierarchy == null) {
            roleHierarchy = RoleHierarchy.builder()
                    .roleName(childRole.getName())
                    .build();
        }
        RoleHierarchy childRoleHierarchy = roleHierarchyRepository.save(roleHierarchy);

        childRoleHierarchy.setParent(parentRoleHierarchy);
    }

    private void setUpIpBlacklist(String ipAddress) {
        if(!accessIpRepository.existsByAddress(ipAddress)) {
            AccessIp ip = AccessIp.builder()
                    .address(ipAddress)
                    .build();
            accessIpRepository.save(ip);
        }
    }

}