package com.mouken.modules.party.event;

import com.mouken.modules.party.domain.Party;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class PartyCreatedEvent {

    private Party party;

    public PartyCreatedEvent(Party party) {
        this.party = party;
    }

}
