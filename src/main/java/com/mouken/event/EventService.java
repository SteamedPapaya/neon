package com.mouken.event;

import com.mouken.domain.Account;
import com.mouken.domain.Event;
import com.mouken.domain.Party;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@Transactional
@RequiredArgsConstructor
public class EventService {

    private final EventRepository eventRepository;

    public Event createEvent(Event event, Party party, Account account) {
        event.setCreatedBy(account);
        event.setCreatedDateTime(LocalDateTime.now());
        event.setParty(party);
        return eventRepository.save(event);
    }
}