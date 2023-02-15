package com.mouken.modules.account.service;

import com.mouken.modules.account.PrincipalUser;
import com.mouken.modules.account.ProviderUser;
import com.mouken.modules.account.converter.ProviderUserConverter;
import com.mouken.modules.account.converter.ProviderUserRequest;
import com.mouken.modules.account.repository.AccountRepository;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class CustomOAuth2UserService extends AbstractOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    public CustomOAuth2UserService(AccountService accountService, AccountRepository accountRepository, ProviderUserConverter<ProviderUserRequest, ProviderUser> providerUserConverter) {
        super(accountService, accountRepository, providerUserConverter);
    }

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        ClientRegistration clientRegistration = userRequest.getClientRegistration();
        OAuth2UserService<OAuth2UserRequest, OAuth2User> oAuth2UserService = new DefaultOAuth2UserService();
        OAuth2User oAuth2User = oAuth2UserService.loadUser(userRequest);
        ProviderUserRequest providerUserRequest = new ProviderUserRequest(clientRegistration, oAuth2User);
        ProviderUser providerUser = getProviderUser(providerUserRequest);
        register(providerUser);
        return new PrincipalUser(providerUser, getAccountRepository());
    }

}