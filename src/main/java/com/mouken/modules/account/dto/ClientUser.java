package com.mouken.modules.account.dto;

import com.mouken.modules.account.ProviderUser;
import lombok.Builder;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.List;
import java.util.Map;

@Data
@Builder
public class ClientUser implements ProviderUser {

    private String email;
    private String password;
    private String provider;
    private List<? extends GrantedAuthority> authorities;
    private String username;
    private String nickname;
    private String picture;

    @Override
    public String getEmail() {
        return email;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getProvider() {
        return provider;
    }

    @Override
    public List<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public String getNickname() {
        return nickname;
    }

    @Override
    public String getPicture() {
        return picture;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return null;
    }

    @Override
    public OAuth2User getOAuth2User() {
        return null;
    }
}
