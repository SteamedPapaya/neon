package com.mouken.modules.party.event;

import com.mouken.modules.party.Party;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEvent;

@Getter
@RequiredArgsConstructor
public class PartyUpdateEvent {

    private final Party party;

    private final String message;

}
