package com.mouken.modules.account;

import com.mouken.modules.account.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AccountFactory {

    @Autowired
    AccountRepository accountRepository;

    public Account createAccount(String username) {
        Account account = new Account();
        account.setUsername(username);
        account.setEmail(username + "@email.com");
        account.setPassword("12345678");
        accountRepository.save(account);
        return account;
    }

}
