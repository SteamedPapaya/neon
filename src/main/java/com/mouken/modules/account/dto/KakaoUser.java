package com.mouken.modules.account.dto;

import com.mouken.modules.account.Attributes;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.core.user.OAuth2User;

public class KakaoUser extends AbstractProviderUser {

    public KakaoUser(Attributes attributes, OAuth2User oAuth2User, ClientRegistration clientRegistration) {
        super(attributes.getFirstLayerAttributes(), oAuth2User, clientRegistration);
    }

    @Override
    public String getUsername() {
        return (String) getAttributes().get("sub");
    }

    @Override
    public String getNickname() {
        return (String) getAttributes().get("nickname");
    }

    @Override
    public String getPicture() {
        return (String) getAttributes().get("picture");
    }

}
