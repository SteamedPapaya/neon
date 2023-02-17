package com.mouken.modules.account.service;

import com.mouken.modules.account.Account;
import com.mouken.modules.account.ProviderUser;
import com.mouken.modules.account.dto.AccountModifyForm;
import com.mouken.modules.account.dto.Profile;
import com.mouken.modules.account.repository.AccountRepository;
import com.mouken.modules.account.web.form.Notifications;
import com.mouken.modules.role.service.RoleService;
import com.mouken.infra.config.AppProperties;
import com.mouken.modules.tag.domain.Tag;
import com.mouken.modules.util.AuthorityMapper;
import com.mouken.modules.util.mail.EmailService;
import com.mouken.modules.util.mail.HtmlEmailService;
import com.mouken.modules.zone.domain.Zone;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.TemplateEngine;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class AccountService {

    private final ModelMapper modelMapper;
    private final AuthorityMapper authorityMapper;
    private final AccountRepository accountRepository;
    private final RoleService roleService;
//    private final EmailService emailService;
//    private final TemplateEngine templateEngine;
//    private final AppProperties appProperties;
//    private final PasswordEncoder passwordEncoder;


    public void createAccount(ProviderUser providerUser) {
        Account account = Account.builder()
                .email(providerUser.getEmail())
                .provider(providerUser.getProvider())
                .roles(authorityMapper.mapAuthorities(providerUser.getAuthorities()))
                .username(providerUser.getUsername())
                .nickname(providerUser.getNickname())
                .picture(providerUser.getPicture())
                .build();
        accountRepository.save(account);
    }

    public void modifyAccount(Long id, AccountModifyForm accountForm) { // todo EH
        // todo move to modifyAccountPassword() accountForm.setPassword(passwordEncoder.encode(accountForm.getPassword()));
        Account account = accountRepository.findById(id).orElseThrow();
        modelMapper.map(accountForm, account);
        account.setRoles(roleService.getRolesByName(accountForm.getRoleNameList()));
        if (account.getRoles() == null) {
            account.setRoles(roleService.getDefaultRoles());
        }
        accountRepository.save(account);
    }

    public Account getAccountById(Long id) {
        return accountRepository.findById(id).orElseThrow();
    } // todo EH

    public Account getAccountByEmail(String email) {
        return accountRepository.findByEmail(email);
    }

    public Account getAccountByUsername(String username) {
        return accountRepository.findByUsername(username);
    } // todo EH

    public List<Account> getAccounts() {
        return accountRepository.findAll(Sort.by(Sort.Order.desc("id")));
    }

    public Account getAccount(String username) {
        return accountRepository.findByUsername(username);
    } // todo EH

    public AccountModifyForm getAccountForm(Long id) {
        Account account = accountRepository.findById(id).orElseThrow();
        AccountModifyForm accountModifyForm = modelMapper.map(account, AccountModifyForm.class);
        accountModifyForm.setRoleNameList(
                account.getRoles().stream().map(role ->
                        role.getName()).collect(Collectors.toList()));
        return accountModifyForm;
    }

    public void deleteAccount(Long id) {
        accountRepository.deleteById(id);
    }

    /* todo delete
    private final String PREFIX_ROLE = "ROLE_";
    private final String PREFIX_SCOPE = "SCOPE_";
    */

    public void updateProfile(Account account, Profile profile) {
        modelMapper.map(profile, account);
        accountRepository.save(account);
    }

    public void updateNotifications(Account account, Notifications notifications) {
        modelMapper.map(notifications, account);
        accountRepository.save(account);
    }

    public Set<Tag> getTags(Account account) {
        Optional<Account> byId = accountRepository.findById(account.getId());
        return byId.orElseThrow().getTags();
    }

    public void addTag(Account account, Tag tag) {
        Optional<Account> byId = accountRepository.findById(account.getId());
        byId.ifPresent(a -> a.getTags().add(tag));
    }

    public void removeTag(Account account, Tag tag) {
        Optional<Account> byId = accountRepository.findById(account.getId());
        byId.ifPresent(a -> a.getTags().remove(tag));
    }

    public Set<Zone> getZones(Account account) {
        Optional<Account> byId = accountRepository.findById(account.getId());
        return byId.orElseThrow().getZones();
    }

    public void addZone(Account account, Zone zone) {
        Optional<Account> byId = accountRepository.findById(account.getId());
        byId.ifPresent(a -> a.getZones().add(zone));
    }

    public void removeZone(Account account, Zone zone) {
        Optional<Account> byId = accountRepository.findById(account.getId());
        byId.ifPresent(a -> a.getZones().remove(zone));
    }

}
