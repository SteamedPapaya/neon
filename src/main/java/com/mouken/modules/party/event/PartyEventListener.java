package com.mouken.modules.party.event;

import com.mouken.modules.party.Party;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Async
@Component
@Transactional
public class PartyEventListener {

    @EventListener
    public void handlePartyCreatedEvent(PartyCreatedEvent partyCreatedEvent) {
        Party party = partyCreatedEvent.getParty();
        log.info(party.getTitle() + "is created.");
        // TODO 이메일 보내거나, DB에 Notification 정보를 저장하면 됩니다.
        throw new RuntimeException();
    }

}
