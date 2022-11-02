package com.mouken.modules.post.domain;

import com.mouken.modules.account.domain.Account;
import com.mouken.modules.party.domain.Party;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@EqualsAndHashCode(of = "id")
public class Post {

    @Id
    @GeneratedValue
    private Long id;

    @Column(unique = true)
    private String path;

    @ManyToOne
    private Party party;

    @ManyToOne
    private Account createdBy;

    @Column(nullable = false)
    private String title;

    @Lob
    private String description;

    private LocalDateTime createdDateTime;

    private int likes = 0;

    private int dislikes = 0;
}
