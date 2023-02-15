package com.mouken.modules.account;

import com.mouken.modules.role.entity.Role;
import com.mouken.modules.tag.domain.Tag;
import com.mouken.modules.zone.domain.Zone;
import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.*;

@Entity
@Getter
@Setter
@Builder
@EqualsAndHashCode(of = "id")
@AllArgsConstructor
@NoArgsConstructor
public class Account implements Serializable {

    @Id
    @GeneratedValue
    @Column(name = "account_id")
    private Long id;

    /**
     * login signUp email
     */

    @Column(unique = true)
    private String email;
    private String password;
    private String provider;

    @Column(unique = true)
    private String username;
    private String nickname;
    @Lob
    @Basic(fetch = FetchType.EAGER)
    private String picture;

    private String emailCheckToken;
    private int emailCheckTokenCount;
    private LocalDateTime emailCheckTokenGeneratedAt;
    private boolean emailVerified;
    private LocalDateTime joinedAt;


    // profile
    private String bio;
    private String url;
    private String occupation;
    private String location;

    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinTable(name = "account_roles",
            joinColumns = { @JoinColumn(name = "account_id") },
            inverseJoinColumns = { @JoinColumn(name = "role_id") })
    private Set<Role> roles = new HashSet<>();

    /**
     * notification
     */

    private boolean partyCreatedByEmail;

    private boolean partyCreatedByWeb = true;

    private boolean partyEnrollmentResultByEmail;

    private boolean partyEnrollmentResultByWeb = true;

    private boolean partyUpdatedByEmail;

    private boolean partyUpdatedByWeb = true;


    @ManyToMany
    private Set<Tag> tags = new HashSet<>();

    @ManyToMany
    private Set<Zone> zones = new HashSet<>();

    /*@OneToMany(mappedBy = "account")
    private List<AccountParty> accountPartyList = new ArrayList<>();*/

    public void generateEmailCheckToken() {
        this.emailCheckToken = UUID.randomUUID().toString();
    }

    public void completeSignUp() {
        this.emailVerified = true;
        this.joinedAt = LocalDateTime.now();
    }

    public boolean isValidToken(String token) {
        return this.getEmailCheckToken().equals(token);
    }

    public boolean canSendConfirmEmail() {
        return this.emailCheckTokenCount < 2 || this.emailCheckTokenGeneratedAt.isBefore(LocalDateTime.now().minusMinutes(15));
    }

    public void addEmailCheckTokenCount() {
        this.emailCheckTokenCount++;
    }

}
