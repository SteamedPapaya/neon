package com.mouken.account;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;
    private final JavaMailSender javaMailSender;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public void processNewAccount(SignUpForm signUpForm){
        Account savedAccount = saveAccount(signUpForm);
        savedAccount.generateEmailCheckToken();
        sendSignUpConfirmEmail(savedAccount);
    }

    public void sendSignUpConfirmEmail(Account savedAccount) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(savedAccount.getEmail());
        mailMessage.setSubject("Finish signing up for Mouken");
        mailMessage.setText("/check-email-token" +
                "?token=" + savedAccount.getEmailCheckToken() +
                "&email=" + savedAccount.getEmail()); // TODO Why
        javaMailSender.send(mailMessage);
    }

    public Account saveAccount(SignUpForm form) {
        Account account = Account
                .builder()
                .email(form.getEmail())
                .nickname(form.getNickname())
                .password(passwordEncoder.encode(form.getPassword())) // encoding
                .build();

        Account savedAccount = accountRepository.save(account);
        return savedAccount;
    }
}
