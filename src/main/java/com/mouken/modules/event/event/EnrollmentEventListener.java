package com.mouken.modules.event.event;

import com.mouken.infra.config.AppProperties;
import com.mouken.infra.mail.EmailMessage;
import com.mouken.infra.mail.EmailService;
import com.mouken.modules.account.Account;
import com.mouken.modules.event.Enrollment;
import com.mouken.modules.event.Event;
import com.mouken.modules.notification.Notification;
import com.mouken.modules.notification.NotificationRepository;
import com.mouken.modules.notification.NotificationType;
import com.mouken.modules.party.Party;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.time.LocalDateTime;

@Slf4j
@Async
@Component
@Transactional
@RequiredArgsConstructor
public class EnrollmentEventListener {

    private final NotificationRepository notificationRepository;
    private final AppProperties appProperties;
    private final TemplateEngine templateEngine;
    private final EmailService emailService;

    @EventListener
    public void handleEnrollmentEvent(EnrollmentEvent enrollmentEvent) {
        Enrollment enrollment = enrollmentEvent.getEnrollment();
        Account account = enrollment.getAccount();
        Event event = enrollment.getEvent();
        Party party = event.getParty();

        if (account.isPartyEnrollmentResultByEmail()) {
            sendEmail(enrollmentEvent, account, event, party);
        }

        if (account.isPartyEnrollmentResultByWeb()) {
            createNotification(enrollmentEvent, account, event, party);
        }
    }

    private void sendEmail(EnrollmentEvent enrollmentEvent, Account account, Event event, Party party) {
        Context context = new Context();
        context.setVariable("nickname", account.getUsername());
        context.setVariable("link", "/party/" + party.getPath() + "/events/" + event.getId());
        context.setVariable("linkName", party.getTitle());
        context.setVariable("message", enrollmentEvent.getMessage());
        context.setVariable("host", appProperties.getHost());
        String message = templateEngine.process("mail/simple-link", context);

        EmailMessage emailMessage = EmailMessage.builder()
                .subject("Mouken, " + event.getTitle() + " event enrollment result.")
                .to(account.getEmail())
                .message(message)
                .build();

        emailService.sendEmail(emailMessage);
    }

    private void createNotification(EnrollmentEvent enrollmentEvent, Account account, Event event, Party party) {
        Notification notification = new Notification();
        notification.setTitle(party.getTitle() + " / " + event.getTitle());
        notification.setLink("/party/" + party.getPath() + "/events/" + event.getId());
        notification.setChecked(false);
        notification.setCreatedDateTime(LocalDateTime.now());
        notification.setMessage(enrollmentEvent.getMessage());
        notification.setAccount(account);
        notification.setNotificationType(NotificationType.EVENT_ENROLLMENT);
        notificationRepository.save(notification);
    }

}