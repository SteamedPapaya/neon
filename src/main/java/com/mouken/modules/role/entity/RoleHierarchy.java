package com.mouken.modules.role.entity;

import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RoleHierarchy implements Serializable {

    @Id
    @GeneratedValue
    private Long id;

    @Column(name = "role_name")
    private String roleName;

    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.ALL})
    @JoinColumn(name = "parent", referencedColumnName = "role_name")
    private RoleHierarchy parent;

    @OneToMany(mappedBy = "parent", cascade={CascadeType.ALL})
    private Set<RoleHierarchy> children = new HashSet<>();
}
