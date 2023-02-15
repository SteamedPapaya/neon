package com.mouken.modules.account.service;

import com.mouken.modules.account.PrincipalUser;
import com.mouken.modules.account.ProviderUser;
import com.mouken.modules.account.converter.ProviderUserConverter;
import com.mouken.modules.account.converter.ProviderUserRequest;
import com.mouken.modules.account.repository.AccountRepository;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class CustomOidcUserService extends AbstractOAuth2UserService implements OAuth2UserService<OidcUserRequest, OidcUser> {

    public CustomOidcUserService(AccountService accountService, AccountRepository accountRepository, ProviderUserConverter<ProviderUserRequest, ProviderUser> providerUserConverter) {
        super(accountService, accountRepository, providerUserConverter);
    }

    @Override
    public OidcUser loadUser(OidcUserRequest userRequest) throws OAuth2AuthenticationException {
        ClientRegistration clientRegistration =
                ClientRegistration.withClientRegistration(userRequest.getClientRegistration())
                        .userNameAttributeName("sub")
                        .build();
        OidcUserRequest oidcUserRequest =
                new OidcUserRequest(clientRegistration, userRequest.getAccessToken(),
                        userRequest.getIdToken(), userRequest.getAdditionalParameters());
        OAuth2UserService<OidcUserRequest, OidcUser> oidcUserService = new OidcUserService();
        OidcUser oidcUser = oidcUserService.loadUser(oidcUserRequest);
        ProviderUserRequest providerUserRequest = new ProviderUserRequest(clientRegistration, oidcUser);
        ProviderUser providerUser = getProviderUser(providerUserRequest);
        register(providerUser);
        return new PrincipalUser(providerUser, getAccountRepository());
    }

}
