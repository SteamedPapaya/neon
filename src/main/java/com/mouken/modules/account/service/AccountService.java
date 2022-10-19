package com.mouken.modules.account.service;

import com.mouken.infra.config.AppProperties;
import com.mouken.modules.account.db.AccountRepository;
import com.mouken.modules.account.UserAccount;
import com.mouken.modules.account.domain.Account;
import com.mouken.modules.account.web.form.Notifications;
import com.mouken.modules.account.web.form.Profile;
import com.mouken.modules.account.web.form.SignUpForm;
import com.mouken.modules.tag.domain.Tag;
import com.mouken.modules.zone.domain.Zone;
import com.mouken.infra.mail.EmailMessage;
import com.mouken.infra.mail.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class AccountService implements UserDetailsService {

    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;
    private final ModelMapper modelMapper;
    private final EmailService emailService;
    private final TemplateEngine templateEngine;
    private final AppProperties appProperties;

    public Account processNewAccount(SignUpForm signUpForm) {
        Account newAccount = saveAccount(signUpForm);
        sendSignUpConfirmEmail(newAccount);
        return newAccount;
    }

    public Account saveAccount(@Validated SignUpForm signUpForm) {
        signUpForm.setPassword(passwordEncoder.encode(signUpForm.getPassword()));
        Account account = modelMapper.map(signUpForm, Account.class);
        account.generateEmailCheckToken();
        return accountRepository.save(account);
    }

    public void sendSignUpConfirmEmail(Account account) {
        Context context = new Context();
        context.setVariable("link", "/check-email-token" +
                "?token=" + account.getEmailCheckToken() +
                "&email=" + account.getEmail());
        context.setVariable("username", account.getUsername());
        context.setVariable("linkName", "Link");
        context.setVariable("message", "You can finish signing up. please click the link below.");
        context.setVariable("host", appProperties.getHost());
        String message = templateEngine.process("mail/simple-link", context);

        EmailMessage emailMessage = EmailMessage.builder()
                .to(account.getEmail())
                .subject("Mouken Email Check")
                .message(message)
                .build();
        emailService.sendEmail(emailMessage);

        account.addEmailCheckTokenCount();
        account.setEmailCheckTokenGeneratedAt(LocalDateTime.now());
    }

    public void login(Account account) {
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(
                new UserAccount(account),
                account.getPassword(),
                List.of(new SimpleGrantedAuthority("ROLE_USER")));
        SecurityContextHolder.getContext().setAuthentication(token);
    }

    @Transactional(readOnly = true)
    @Override
    public UserDetails loadUserByUsername(String emailOrUsername) throws UsernameNotFoundException {
        Account account = accountRepository.findByEmail(emailOrUsername);
        if (account == null) {
            account = accountRepository.findByUsername(emailOrUsername);
        }
        if (account == null) {
            throw new UsernameNotFoundException(emailOrUsername);
        }
        return new UserAccount(account);
    }

    public void completeSignUp(Account account) {
        account.completeSignUp();
        login(account);
    }

    public void updateProfile(Account account, Profile profile) {
        modelMapper.map(profile, account);
        accountRepository.save(account);
    }

    public void updatePassword(Account account, String newPassword) {
        account.setPassword(passwordEncoder.encode(newPassword));
        accountRepository.save(account);
    }

    public void updateNotifications(Account account, Notifications notifications) {
        modelMapper.map(notifications, account);
        accountRepository.save(account);
    }

    public void updateUsername(Account account, String username) {
        account.setUsername(username);
        accountRepository.save(account);
        login(account);
    }

    public void sendEmailLoginLink(Account account) {
        Context context = new Context();
        context.setVariable("link", "/email-login" +
                "?token=" + account.getEmailCheckToken() +
                "&email=" + account.getEmail());
        context.setVariable("username", account.getUsername());
        context.setVariable("linkName", "Link");
        context.setVariable("message", "you can login with the link.");
        context.setVariable("host", appProperties.getHost());
        String message = templateEngine.process("mail/simple-link", context);

        EmailMessage emailMessage = EmailMessage.builder()
                .to(account.getEmail())
                .subject("Mouken Login Link")
                .message(message)
                .build();
        emailService.sendEmail(emailMessage);

        account.addEmailCheckTokenCount();
        account.setEmailCheckTokenGeneratedAt(LocalDateTime.now());
    }

    public Set<Tag> getTags(Account account) {
        Optional<Account> byId = accountRepository.findById(account.getId());
        return byId.orElseThrow().getTags();
    }

    public void addTag(Account account, Tag tag) {
        Optional<Account> byId = accountRepository.findById(account.getId());
        byId.ifPresent(a -> a.getTags().add(tag));
    }

    public void removeTag(Account account, Tag tag) {
        Optional<Account> byId = accountRepository.findById(account.getId());
        byId.ifPresent(a -> a.getTags().remove(tag));
    }

    public Set<Zone> getZones(Account account) {
        Optional<Account> byId = accountRepository.findById(account.getId());
        return byId.orElseThrow().getZones();
    }

    public void addZone(Account account, Zone zone) {
        Optional<Account> byId = accountRepository.findById(account.getId());
        byId.ifPresent(a -> a.getZones().add(zone));
    }

    public void removeZone(Account account, Zone zone) {
        Optional<Account> byId = accountRepository.findById(account.getId());
        byId.ifPresent(a -> a.getZones().remove(zone));
    }

    public Account getAccount(String username, Account account) {
        Account foundAccount = accountRepository.findByUsername(username);
        if (account == null) {
            throw new IllegalArgumentException("This user does not exist");
        }
        return foundAccount;
    }

}
