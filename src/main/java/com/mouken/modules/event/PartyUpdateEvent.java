package com.mouken.modules.event;

import com.mouken.modules.party.domain.Party;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class PartyUpdateEvent {

    private final Party party;

    private final String message;

}
