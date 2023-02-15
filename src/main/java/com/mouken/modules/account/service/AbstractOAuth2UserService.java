package com.mouken.modules.account.service;

import com.mouken.modules.account.ProviderUser;
import com.mouken.modules.account.converter.ProviderUserConverter;
import com.mouken.modules.account.converter.ProviderUserRequest;
import com.mouken.modules.account.repository.AccountRepository;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
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
