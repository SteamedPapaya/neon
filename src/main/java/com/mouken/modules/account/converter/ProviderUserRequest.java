package com.mouken.modules.account.converter;

import com.mouken.modules.account.Account;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.core.user.OAuth2User;

public class ProviderUserRequest {

    private ClientRegistration clientRegistration;
    private OAuth2User oAuth2User;
    private Account account;

    public ProviderUserRequest(ClientRegistration clientRegistration, OAuth2User oAuth2User){
        this.clientRegistration = clientRegistration;
        this.oAuth2User = oAuth2User;
        this.account = null;
    }

    public ProviderUserRequest(Account account){
        this.clientRegistration = null;
        this.oAuth2User = null;
        this.account = account;
    }

    public ClientRegistration getClientRegistration() {
        return clientRegistration;
    }

    public OAuth2User getOAuth2User() {
        return oAuth2User;
    }

    public Account getAccount() {
        return account;
    }
}
