package com.mouken.modules.party.event;

import com.mouken.infra.config.AppProperties;
import com.mouken.infra.mail.EmailMessage;
import com.mouken.infra.mail.EmailService;
import com.mouken.modules.account.domain.Account;
import com.mouken.modules.account.AccountPredicates;
import com.mouken.modules.account.db.AccountRepository;
import com.mouken.modules.notification.domain.Notification;
import com.mouken.modules.notification.db.NotificationRepository;
import com.mouken.modules.notification.NotificationType;
import com.mouken.modules.party.domain.Party;
import com.mouken.modules.party.db.PartyRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Slf4j
@Async
@Component
@Transactional
@RequiredArgsConstructor
public class PartyEventListener {

    private final PartyRepository partyRepository;
    private final AccountRepository accountRepository;
    private final EmailService emailService;
    private final TemplateEngine templateEngine;
    private final AppProperties appProperties;
    private final NotificationRepository notificationRepository;

    @EventListener
    public void handlePartyCreatedEvent(PartyCreatedEvent partyCreatedEvent) {
        Party party = partyRepository.findPartyWithTagsAndZonesById(partyCreatedEvent.getParty().getId());
        Iterable<Account> accounts = accountRepository.findAll(AccountPredicates.findByTagsAndZones(party.getTags(), party.getZones()));
        accounts.forEach(account -> {
            if (account.isPartyCreatedByEmail()) {
                sendPartyCreatedEmail(party, account, "New party",
                        "Mouken, '" + party.getTitle() + "' the new party.");
            }

            if (account.isPartyCreatedByWeb()) {
                createNotification(party, account, party.getShortDescription(), NotificationType.STUDY_CREATED);
            }
        });
    }

    @EventListener
    public void handlePartyUpdateEvent(PartyUpdateEvent partyUpdateEvent) {
        Party party = partyRepository.findPartyWithManagersAndMembersById(partyUpdateEvent.getParty().getId());
        Set<Account> accounts = new HashSet<>();
        accounts.addAll(party.getManagers());
        accounts.addAll(party.getMembers());

        accounts.forEach(account -> {
            if (account.isPartyUpdatedByEmail()) {
                sendPartyCreatedEmail(party, account, partyUpdateEvent.getMessage(),
                        "Mouken, there are notifications in '" + party.getTitle() + "' party.");
            }

            if (account.isPartyUpdatedByWeb()) {
                createNotification(party, account, partyUpdateEvent.getMessage(), NotificationType.STUDY_UPDATED);
            }
        });
    }

    private void createNotification(Party party, Account account, String message, NotificationType notificationType) {
        Notification notification = new Notification();
        notification.setTitle(party.getTitle());
        notification.setLink("/party/" + party.getPath());
        notification.setChecked(false);
        notification.setCreatedDateTime(LocalDateTime.now());
        notification.setMessage(message);
        notification.setAccount(account);
        notification.setNotificationType(notificationType);
        notificationRepository.save(notification);
    }

    private void sendPartyCreatedEmail(Party party, Account account, String contextMessage, String emailSubject) {
        Context context = new Context();
        context.setVariable("username", account.getUsername());
        context.setVariable("link", "/party/" + party.getPath());
        context.setVariable("linkName", party.getTitle());
        context.setVariable("message", contextMessage);
        context.setVariable("host", appProperties.getHost());
        String message = templateEngine.process("mail/simple-link", context);

        EmailMessage emailMessage = EmailMessage.builder()
                .subject(emailSubject)
                .to(account.getEmail())
                .message(message)
                .build();

        emailService.sendEmail(emailMessage);
    }

}
