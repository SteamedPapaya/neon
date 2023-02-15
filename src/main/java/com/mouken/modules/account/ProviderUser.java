package com.mouken.modules.account;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.List;
import java.util.Map;

public interface ProviderUser {

    // common
    String getEmail();
    String getPassword();
    String getProvider();
    List<? extends GrantedAuthority> getAuthorities();

    // uncommon
    String getUsername();
    String getNickname();
    String getPicture();

    Map<String, Object> getAttributes();
    OAuth2User getOAuth2User();

}

