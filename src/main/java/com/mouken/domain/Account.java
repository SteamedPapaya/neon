package com.mouken.domain;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Getter
@Setter
@EqualsAndHashCode(of = "id")
@AllArgsConstructor
@NoArgsConstructor
public class Account {

    @Id
    @GeneratedValue
    private Long id;

    @Column(unique = true)
    private String email;

    @Column(unique = true)
    private String username;

    private String password;

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

    @Lob
    @Basic(fetch = FetchType.EAGER)
    private String profileImage;





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

    public boolean isManagerOf(Party party) {
        return party.getManagers().contains(this);
    }
}
