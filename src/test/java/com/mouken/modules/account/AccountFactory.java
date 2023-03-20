package com.mouken.modules.account;

import com.mouken.modules.account.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.swing.plaf.SpinnerUI;

@Component
public class AccountFactory {

    @Autowired
    private final AccountRepository accountRepository;

    public AccountFactory(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    public Account createAccount(String nickname) {
        Account account = new Account();
        account.setNickname(nickname);
        account.setEmail(nickname + "@email.com");
        account.setPassword("12345678");
        accountRepository.save(account);
        return account;
    }

}
