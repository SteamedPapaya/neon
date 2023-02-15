package com.mouken.modules.account.converter;

import com.mouken.modules.account.ProviderUser;
import com.mouken.modules.account.dto.KakaoUser;
import com.mouken.modules.util.OAuth2Config;
import com.mouken.modules.util.OAuth2Utils;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;

public final class OAuth2KakaoProviderUserConverter implements ProviderUserConverter<ProviderUserRequest, ProviderUser> {

    @Override
    public ProviderUser convert(ProviderUserRequest providerUserRequest) {

        if (!providerUserRequest.getClientRegistration().getRegistrationId().equals(OAuth2Config.SocialType.KAKAO.getSocialName())) {
            return null;
        }

        if (!(providerUserRequest.getOAuth2User() instanceof OidcUser)) {
            return null;
        }

        return new KakaoUser(OAuth2Utils.getFirstLayerAttributes(
                providerUserRequest.getOAuth2User()),
                providerUserRequest.getOAuth2User(),
                providerUserRequest.getClientRegistration());
    }
}
