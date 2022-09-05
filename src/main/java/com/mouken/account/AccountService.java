package com.mouken.account;

import com.mouken.domain.Account;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
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

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AccountService  implements UserDetailsService {

    private final AccountRepository accountRepository;
    private final JavaMailSender javaMailSender;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public Account processNewAccount(SignUpForm signUpForm){
        log.info("processNewAccount");
        Account savedAccount = saveAccount(signUpForm);

        savedAccount.generateEmailCheckToken();
        // TODO Delete sendSignUpConfirmEmail(savedAccount);
        return savedAccount;
    }

    public void sendSignUpConfirmEmail(Account savedAccount) {
        log.info("sendSignUpConfirmEmail");

        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(savedAccount.getEmail());
        mailMessage.setSubject("Finish signing up for Mouken");
        mailMessage.setText("/check-email-token" +
                "?token=" + savedAccount.getEmailCheckToken() +
                "&email=" + savedAccount.getEmail()); // TODO Why
        javaMailSender.send(mailMessage);

        savedAccount.addEmailCheckTokenCount();
        savedAccount.setEmailCheckTokenGeneratedAt(LocalDateTime.now());

    }

    public Account saveAccount(@Validated SignUpForm form) {
        log.info("saveAccount");

        Account account = Account.builder()
                .email(form.getEmail())
                .username(form.getUsername())
                .password(passwordEncoder.encode(form.getPassword())) // encoding
                .emailCheckTokenCount(0)
                .build();

        return accountRepository.save(account);
    }

    public void login(Account account) {
        log.info("login");

        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(
                new UserAccount(account),
                account.getPassword(),
                List.of(new SimpleGrantedAuthority("ROLE_USER")));
        SecurityContextHolder.getContext().setAuthentication(token);
    }


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
}
