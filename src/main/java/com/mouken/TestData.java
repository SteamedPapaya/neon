package com.mouken;

import com.mouken.account.AccountRepository;
import com.mouken.account.AccountService;
import com.mouken.account.SignUpForm;
import com.mouken.domain.Account;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;

@Component
@RequiredArgsConstructor
public class TestData {

/*    private final AccountService accountService;

    @Transactional
    @PostConstruct
    public void init() {
        SignUpForm signUpForm = new SignUpForm();
        signUpForm.setEmail("admin@email.com");
        signUpForm.setUsername("admin");
        signUpForm.setPassword("admin123");
        Account account = accountService.processNewAccount(signUpForm);
        account.completeSignUp();
        //accountService.login(account);
    }*/
}
