package com.mouken.modules.account.converter;


import com.mouken.modules.account.ProviderUser;
import com.mouken.modules.account.dto.GoogleUser;
import com.mouken.modules.util.OAuth2Config;
import com.mouken.modules.util.OAuth2Utils;

public final class OAuth2GoogleProviderUserConverter implements ProviderUserConverter<ProviderUserRequest, ProviderUser> {

    @Override
    public ProviderUser convert(ProviderUserRequest providerUserRequest) {

        if (!providerUserRequest.getClientRegistration().getRegistrationId().equals(OAuth2Config.SocialType.GOOGLE.getSocialName())) {
            return null;
        }

        return new GoogleUser(OAuth2Utils.getFirstLayerAttributes(
                providerUserRequest.getOAuth2User()),
                providerUserRequest.getOAuth2User(),
                providerUserRequest.getClientRegistration());

    }
}
