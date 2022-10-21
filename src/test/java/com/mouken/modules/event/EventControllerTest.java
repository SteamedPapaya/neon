package com.mouken.modules.event;

import com.mouken.infra.AbstractContainerBaseTest;
import com.mouken.infra.MockMvcTest;
import com.mouken.modules.account.*;
import com.mouken.modules.account.db.AccountRepository;
import com.mouken.modules.account.domain.Account;
import com.mouken.modules.account.web.form.SignUpForm;
import com.mouken.modules.account.service.AccountService;
import com.mouken.modules.event.db.EnrollmentRepository;
import com.mouken.modules.event.domain.Event;
import com.mouken.modules.event.service.EventService;
import com.mouken.modules.party.domain.Party;
import com.mouken.modules.party.PartyFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.test.context.support.TestExecutionEvent;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@MockMvcTest
class EventControllerTest extends AbstractContainerBaseTest {

    @Autowired
    MockMvc mockMvc;
    @Autowired
    AccountRepository accountRepository;
    @Autowired
    EventService eventService;
    @Autowired
    EnrollmentRepository enrollmentRepository;
    @Autowired
    AccountFactory accountFactory;
    @Autowired
    AccountService accountService;
    @Autowired
    PartyFactory partyFactory;

    @BeforeEach
    void beforeEach() {
        SignUpForm signUpForm = new SignUpForm();
        signUpForm.setEmail("test@email.com");
        signUpForm.setUsername("test");
        signUpForm.setPassword("12345678");
        accountService.processNewAccount(signUpForm);
    }

    @AfterEach
    void afterEach() {
        accountRepository.deleteAll();
    }



    @Test
    @DisplayName("enroll to FCFS event - Wating (overstaffed)")
    @WithUserDetails(value="test", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void enroll_to_FCFS_event_not_accepted() throws Exception {
        // given
        Account user1 = accountFactory.createAccount("user1");
        Account user2 = accountFactory.createAccount("user2");
        Account user3 = accountFactory.createAccount("user3");
        Party party = partyFactory.createParty(user1);
        Event event = createEvent("test-event", EventType.FCFS, 2, party, user1);
        eventService.newEnrollment(event, user2);
        eventService.newEnrollment(event, user3);

        // when
        mockMvc.perform(post("/party/" + party.getPath() + "/events/" + event.getId() + "/enroll")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/party/" + party.getPath() + "/events/" + event.getId()));

        // then
        Account account = accountRepository.findByUsername("test");
        isNotAccepted(account, event);
    }

    @Test
    @DisplayName("enroll to FCFS event - Accepted")
    @WithUserDetails(value="test", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void enroll_to_FCFS_event_accepted() throws Exception {
        // given
        Account user1 = accountFactory.createAccount("user1");
        Party party = partyFactory.createParty(user1);
        Event event = createEvent("test-event", EventType.FCFS, 2, party, user1);

        // when
        mockMvc.perform(post("/party/" + party.getPath() + "/events/" + event.getId() + "/enroll")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/party/" + party.getPath() + "/events/" + event.getId()));

        // then
        Account account = accountRepository.findByUsername("test");
        isAccepted(account, event);
    }

    @Test
    @DisplayName("Accepted participant cancel enrollment to FCFS event") // 참가신청 확정자가 선착순 모임에 참가 신청을 취소하는 경우, 바로 다음 대기자를 자동으로 신청 확인한다.
    @WithUserDetails(value="test", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void accepted_account_cancelEnrollment_to_FCFS_event_not_accepted() throws Exception {
        Account account = accountRepository.findByUsername("test");
        Account user1 = accountFactory.createAccount("user1");
        Account may = accountFactory.createAccount("may");
        Party party = partyFactory.createParty(user1);
        Event event = createEvent("test-event", EventType.FCFS, 2, party, user1);

        eventService.newEnrollment(event, may);
        eventService.newEnrollment(event, account);
        eventService.newEnrollment(event, user1);

        isAccepted(may, event);
        isAccepted(account, event);
        isNotAccepted(user1, event);

        mockMvc.perform(post("/party/" + party.getPath() + "/events/" + event.getId() + "/disenroll")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/party/" + party.getPath() + "/events/" + event.getId()));

        isAccepted(may, event);
        isAccepted(user1, event);
        assertNull(enrollmentRepository.findByEventAndAccount(event, account));
    }

    @Test
    @DisplayName("not accepted participant cancel enrollment to FCFS event") // 참가신청 비확정자가 선착순 모임에 참가 신청을 취소하는 경우, 기존 확정자를 그대로 유지하고 새로운 확정자는 없다.
    @WithUserDetails(value="test", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void not_accepted_account_cancelEnrollment_to_FCFS_event_not_accepted() throws Exception {
        Account account = accountRepository.findByUsername("test");
        Account user1 = accountFactory.createAccount("user1");
        Account may = accountFactory.createAccount("may");
        Party party = partyFactory.createParty(user1);
        Event event = createEvent("test-event", EventType.FCFS, 2, party, user1);

        eventService.newEnrollment(event, may);
        eventService.newEnrollment(event, user1);
        eventService.newEnrollment(event, account);

        isAccepted(may, event);
        isAccepted(user1, event);
        isNotAccepted(account, event);

        mockMvc.perform(post("/party/" + party.getPath() + "/events/" + event.getId() + "/disenroll")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/party/" + party.getPath() + "/events/" + event.getId()));

        isAccepted(may, event);
        isAccepted(user1, event);
        assertNull(enrollmentRepository.findByEventAndAccount(event, account));
    }

    private void isNotAccepted(Account user1, Event event) {
        assertFalse(enrollmentRepository.findByEventAndAccount(event, user1).isAccepted());
    }

    private void isAccepted(Account account, Event event) {
        assertTrue(enrollmentRepository.findByEventAndAccount(event, account).isAccepted());
    }

    @Test
    @DisplayName("Enrollment to confirmative event - Wating")
    @WithUserDetails(value="test", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void newEnrollment_to_CONFIMATIVE_event_not_accepted() throws Exception {
        Account user1 = accountFactory.createAccount("user1");
        Party party = partyFactory.createParty(user1);
        Event event = createEvent("test-event", EventType.CONFIRMATIVE, 2, party, user1);

        mockMvc.perform(post("/party/" + party.getPath() + "/events/" + event.getId() + "/enroll")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/party/" + party.getPath() + "/events/" + event.getId()));

        Account account = accountRepository.findByUsername("test");
        isNotAccepted(account, event);
    }

    private Event createEvent(String eventTitle, EventType eventType, int limit, Party party, Account account) {
        Event event = new Event();
        event.setEventType(eventType);
        event.setLimitOfEnrollments(limit);
        event.setTitle(eventTitle);
        event.setCreatedDateTime(LocalDateTime.now());
        event.setEndEnrollmentDateTime(LocalDateTime.now().plusDays(1));
        event.setStartDateTime(LocalDateTime.now().plusDays(1).plusHours(5));
        event.setEndDateTime(LocalDateTime.now().plusDays(1).plusHours(7));
        return eventService.createEvent(event, party, account);
    }

}