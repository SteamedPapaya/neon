package com.mouken.modules.account.converter;

import com.mouken.modules.account.Account;
import com.mouken.modules.account.ProviderUser;
import com.mouken.modules.account.dto.ClientUser;

import static com.mouken.modules.util.RoleUtils.mapRoles;

public class UserDetailsProviderUserConverter implements ProviderUserConverter<ProviderUserRequest, ProviderUser> {

    @Override
    public ProviderUser convert(ProviderUserRequest providerUserRequest) {

        if(providerUserRequest.getAccount() == null){
            return null;
        }

        Account account = providerUserRequest.getAccount();
        return ClientUser.builder()
                .username(account.getUsername())
                .password(account.getPassword())
                .authorities(mapRoles(account.getRoles()))
                .email(account.getEmail())
                .build();
    }



}
