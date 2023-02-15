package com.mouken.modules.account.converter;


import com.mouken.modules.account.ProviderUser;
import com.mouken.modules.account.dto.NaverUser;
import com.mouken.modules.util.OAuth2Config;
import com.mouken.modules.util.OAuth2Utils;

public final class OAuth2NaverProviderUserConverter implements ProviderUserConverter<ProviderUserRequest, ProviderUser> {
    @Override
    public ProviderUser convert(ProviderUserRequest providerUserRequest) {

        if (!providerUserRequest.getClientRegistration().getRegistrationId().equals(OAuth2Config.SocialType.NAVER.getSocialName())) {
            return null;
        }

        return new NaverUser(OAuth2Utils.getSecondLayerAttributes(
                providerUserRequest.getOAuth2User(), "response"),
                providerUserRequest.getOAuth2User(),
                providerUserRequest.getClientRegistration());
    }
}
