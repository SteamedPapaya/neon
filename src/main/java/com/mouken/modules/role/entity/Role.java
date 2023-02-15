package com.mouken.modules.role.entity;

import com.mouken.modules.account.Account;
import com.mouken.modules.resource.Resource;
import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@Builder
@ToString(exclude = {"accounts", "resources"})
@EqualsAndHashCode(of = "id")
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Role implements Serializable {

    @Id
    @GeneratedValue
    @Column(name = "role_id")
    private Long id;

    @Column(unique = true)
    private String name;

    @Column(unique = true)
    private String description;

    @ManyToMany(mappedBy = "roles", fetch = FetchType.LAZY)
    private Set<Account> accounts = new HashSet<>();

    @ManyToMany(mappedBy = "roles", fetch = FetchType.LAZY)
    @OrderBy("ordernum desc")
    private Set<Resource> resources = new LinkedHashSet<>();

}


