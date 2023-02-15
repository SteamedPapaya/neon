package com.mouken.modules.party.event.service;

import com.mouken.modules.account.Account;
import com.mouken.modules.party.event.EnrollmentAcceptedEvent;
import com.mouken.modules.party.event.EnrollmentRejectedEvent;
import com.mouken.modules.party.event.db.EnrollmentRepository;
import com.mouken.modules.party.event.db.EventRepository;
import com.mouken.modules.party.event.domain.Enrollment;
import com.mouken.modules.party.event.domain.Event;
import com.mouken.modules.party.event.web.form.EventForm;
import com.mouken.modules.party.domain.Party;
import com.mouken.modules.party.event.PartyUpdateEvent;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@Transactional
@RequiredArgsConstructor
public class EventService {

    private final ModelMapper modelMapper;
    private final ApplicationEventPublisher eventPublisher;
    private final EventRepository eventRepository;
    private final EnrollmentRepository enrollmentRepository;

    public Event createEvent(Event event, Party party, Account account) {
        event.setCreatedBy(account);
        event.setCreatedDateTime(LocalDateTime.now());
        event.setParty(party);
        eventPublisher.publishEvent(new PartyUpdateEvent(event.getParty(), "new event : " + event.getId()));
        return eventRepository.save(event);
    }

    public void updateEvent(Event event, EventForm eventForm) {
        modelMapper.map(eventForm, event);
        event.acceptWaitingList();
        eventPublisher.publishEvent(new PartyUpdateEvent(event.getParty(),
                "'" + event.getTitle() + "' event intro has been updated."));
    }

    public void deleteEvent(Event event) {
        eventRepository.delete(event);
        eventPublisher.publishEvent(new PartyUpdateEvent(event.getParty(),
                "'" + event.getTitle() + "' event has been deleted."));
    }

    public void newEnrollment(Event event, Account account) {
        if (!enrollmentRepository.existsByEventAndAccount(event, account)) {
            Enrollment enrollment = new Enrollment();
            enrollment.setEnrolledAt(LocalDateTime.now());
            enrollment.setAccepted(event.isAbleToAcceptWaitingEnrollment());
            enrollment.setAccount(account);
            event.addEnrollment(enrollment);
            enrollmentRepository.save(enrollment);
        }
    }

    public void cancelEnrollment(Event event, Account account) {
        Enrollment enrollment = enrollmentRepository.findByEventAndAccount(event, account);
        if (!enrollment.isAttended()) {
            event.removeEnrollment(enrollment);
            enrollmentRepository.delete(enrollment);
            event.acceptNextWaitingEnrollment();
        }
    }

    public void acceptEnrollment(Event event, Enrollment enrollment) {
        event.accept(enrollment);
        eventPublisher.publishEvent(new EnrollmentAcceptedEvent(enrollment));
    }

    public void rejectEnrollment(Event event, Enrollment enrollment) {
        event.reject(enrollment);
        eventPublisher.publishEvent(new EnrollmentRejectedEvent(enrollment));
    }

    public void checkInEnrollment(Enrollment enrollment) {
        enrollment.setAttended(true);
    }

    public void cancelCheckInEnrollment(Enrollment enrollment) {
        enrollment.setAttended(false);
    }
}