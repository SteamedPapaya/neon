package com.mouken.modules.account.service;

import com.mouken.modules.account.ProviderUser;
import com.mouken.modules.account.converter.ProviderUserConverter;
import com.mouken.modules.account.converter.ProviderUserRequest;
import com.mouken.modules.account.repository.AccountRepository;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@Getter
@RequiredArgsConstructor
public abstract class AbstractOAuth2UserService {

    private final AccountService accountService;
    private final AccountRepository accountRepository;
    private final ProviderUserConverter<ProviderUserRequest, ProviderUser> providerUserConverter;

    protected AbstractOAuth2UserService() {
        this.accountService = null;
        this.accountRepository = null;
        this.providerUserConverter = null;
    }

    public void register(ProviderUser providerUser) {
        if(!accountRepository.existsByUsername(providerUser.getUsername())) {
            accountService.createAccount(providerUser);
            log.info("ACCESS_USERNAME=\'{}\'", providerUser.getUsername());
        } else {
            log.info("ACCESS_USERNAME=\'{}\' : Account already registered", providerUser.getUsername());
        }
    }

    public ProviderUser getProviderUser(ProviderUserRequest providerUserRequest) {
        return providerUserConverter.convert(providerUserRequest);
    }



}
