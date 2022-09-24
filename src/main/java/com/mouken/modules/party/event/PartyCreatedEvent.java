package com.mouken.modules.party.event;

import com.mouken.modules.party.Party;
import lombok.Getter;

@Getter
public class PartyCreatedEvent {

    private Party party;

    public PartyCreatedEvent(Party party) {
        this.party = party;
    }

}
