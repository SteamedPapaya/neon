package com.mouken.modules.event.domain;

import com.mouken.modules.account.Account;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@NamedEntityGraph(
        name = "Enrollment.withEventAndParty",
        attributeNodes = {
                @NamedAttributeNode(value = "event", subgraph = "party")
        },
        subgraphs = @NamedSubgraph(
                name = "party",
                attributeNodes = @NamedAttributeNode("party")
        )
)
@Entity
@Getter
@Setter
@EqualsAndHashCode(of = "id")
public class Enrollment {

    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    private Event event;

    @ManyToOne
    private Account account;

    private LocalDateTime enrolledAt;

    private boolean accepted;

    private boolean attended;

}