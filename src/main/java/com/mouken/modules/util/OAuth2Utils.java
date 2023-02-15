package com.mouken.modules.util;

import com.mouken.modules.account.Attributes;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Map;

public class OAuth2Utils {

    public static Attributes getFirstLayerAttributes(OAuth2User oAuth2User) {

        return Attributes.builder()
                .firstLayerAttributes(oAuth2User.getAttributes())
                .build();
    }

    public static Attributes getSecondLayerAttributes(OAuth2User oAuth2User, String subAttributesKey) {

        Map<String, Object> secondLayerAttributes = (Map<String, Object>) oAuth2User.getAttributes().get(subAttributesKey);
        return Attributes.builder()
                .secondLayerAttributes(secondLayerAttributes)
                .build();
    }

    public static Attributes getThirdLayerAttributes(OAuth2User oAuth2User, String secondLayerAttributesKey, String thirdLayerAttributesKey) {

        Map<String, Object> secondLayerAttributes = (Map<String, Object>) oAuth2User.getAttributes().get(secondLayerAttributesKey);
        Map<String, Object> thirdLayerAttributes = (Map<String, Object>) secondLayerAttributes.get(thirdLayerAttributesKey);

        return Attributes.builder()
                .secondLayerAttributes(secondLayerAttributes)
                .thirdLayerAttributes(thirdLayerAttributes)
                .build();
    }
}
