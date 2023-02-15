package com.mouken.modules.account.dto;

import com.mouken.modules.account.Attributes;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.core.user.OAuth2User;

public class NaverUser extends AbstractProviderUser {

    public NaverUser(Attributes attributes, OAuth2User oAuth2User, ClientRegistration clientRegistration) {
        super(attributes.getSecondLayerAttributes(), oAuth2User, clientRegistration);
    }

    @Override
    public String getUsername() {
        return (String) getAttributes().get("id");
    }

    @Override
    public String getNickname() {
        return (String) getAttributes().get("name");
    }

    @Override
    public String getPicture() {
        return (String) getAttributes().get("profile_image");
    }

}
