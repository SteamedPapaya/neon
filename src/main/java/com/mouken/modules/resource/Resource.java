package com.mouken.modules.resource;

import com.mouken.modules.role.entity.Role;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Entity
@Data
@EqualsAndHashCode(of = "id")
@ToString(exclude = {"roleSet"})
@EntityListeners(value = { AuditingEntityListener.class })
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Resource implements Serializable {

    @Id
    @GeneratedValue
    @Column(name = "resource_id")
    private Long id;

    @Column(name = "resource_name")
    private String name;

    @Column(name = "http_method")
    private String httpMethod;

    @Column(name = "order_num")
    private int orderNum;

    @Column(name = "resource_type")
    private String type;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "role_resource",
            joinColumns = { @JoinColumn(name = "resource_id") },
            inverseJoinColumns = { @JoinColumn(name = "role_id") })
    private Set<Role> roles = new HashSet<>();

}
