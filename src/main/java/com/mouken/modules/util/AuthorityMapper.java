package com.mouken.modules.util;

import com.mouken.modules.role.entity.Role;
import com.mouken.modules.role.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class AuthorityMapper {

    private final RoleRepository roleRepository;

    public Set<Role> mapAuthorities(List<? extends GrantedAuthority> authorities) {
        Set<Role> mapped = new HashSet<>(authorities.size());
        for (GrantedAuthority authority : authorities) {
            mapped.add(roleRepository.findByName(authority.getAuthority())); // todo exception handling
        }
        return mapped;
    }
}
