package com.mouken.modules.account.service;

import com.mouken.infra.config.AppProperties;
import com.mouken.infra.mail.EmailMessage;
import com.mouken.infra.mail.EmailService;
import com.mouken.modules.account.Account;
import com.mouken.modules.account.PrincipalUser;
import com.mouken.modules.account.ProviderUser;
import com.mouken.modules.account.converter.ProviderUserConverter;
import com.mouken.modules.account.converter.ProviderUserRequest;
import com.mouken.modules.account.dto.AccountCreateForm;
import com.mouken.modules.account.repository.AccountRepository;
import com.mouken.modules.account.web.form.SignUpForm;
import com.mouken.modules.role.service.RoleService;
import com.mouken.modules.tag.domain.Tag;
import com.mouken.modules.zone.domain.Zone;
import org.modelmapper.ModelMapper;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;

@Service
@Transactional
public class CustomUserDetailsService extends AbstractOAuth2UserService implements UserDetailsService {

    private AccountRepository accountRepository;
    private ModelMapper modelMapper;
    private RoleService roleService;
    private EmailService emailService;
    private TemplateEngine templateEngine;
    private AppProperties appProperties;
    private PasswordEncoder passwordEncoder;

    public CustomUserDetailsService(
            AccountService accountService,
            AccountRepository accountRepository,
            ProviderUserConverter<ProviderUserRequest, ProviderUser> providerUserConverter,
            EmailService emailService, TemplateEngine templateEngine, AppProperties appProperties, PasswordEncoder passwordEncoder, ModelMapper modelMapper, RoleService roleService) {
        super(accountService, accountRepository, providerUserConverter);
        this.accountRepository = accountRepository;
        this.emailService = emailService;
        this.templateEngine = templateEngine;
        this.appProperties = appProperties;
        this.passwordEncoder = passwordEncoder;
        this.modelMapper = modelMapper;
        this.roleService = roleService;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        Account account = accountRepository.findByUsername(username);

        if (account == null) {
            throw new UsernameNotFoundException("UsernameNotFoundException: Username=" + username);
        }

        ProviderUserRequest providerUserRequest = new ProviderUserRequest(account);
        ProviderUser providerUser = getProviderUser(providerUserRequest);
        return new PrincipalUser(providerUser, getAccountRepository());

    }

    public void createAccount(AccountCreateForm form) { // todo add @Validated
        form.setPassword(passwordEncoder.encode(form.getPassword()));
        Account account = modelMapper.map(form, Account.class);
        account.generateEmailCheckToken();
        account.setRoles(roleService.getDefaultRoles());
        Account savedAccount = accountRepository.save(account);
        sendSignUpConfirmEmail(savedAccount);
    }

    public void sendSignUpConfirmEmail(Account account) {
        Context context = new Context();
        context.setVariable("link", "/check-email-token" +
                "?token=" + account.getEmailCheckToken() +
                "&email=" + account.getEmail());
        context.setVariable("username", account.getUsername());
        context.setVariable("linkName", "Link");
        context.setVariable("message", "You can finish signing up. please click the link below.");
        context.setVariable("host", appProperties.getHost());
        String message = templateEngine.process("mail/simple-link", context);

        EmailMessage emailMessage = EmailMessage.builder()
                .to(account.getEmail())
                .subject("Mouken Email Check")
                .message(message)
                .build();
        emailService.sendEmail(emailMessage);

        account.addEmailCheckTokenCount();
        account.setEmailCheckTokenGeneratedAt(LocalDateTime.now());
    }

    public void completeSignUp(Account account) {
        account.completeSignUp();
        login(account);
    }

    public void login(Account account) {
        ProviderUserRequest providerUserRequest = new ProviderUserRequest(account);
        ProviderUser providerUser = getProviderUser(providerUserRequest);
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(
                new PrincipalUser(providerUser, getAccountRepository()),
                account.getPassword(),
                providerUser.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(token);
    }



    public Account getAccount(String username, Account account) {
        Account foundAccount = accountRepository.findByUsername(username);
        if (account == null) {
            throw new IllegalArgumentException("This user does not exist");
        }
        return foundAccount;
    }

    public void updatePassword(Account account, String newPassword) {
        account.setPassword(passwordEncoder.encode(newPassword));
        accountRepository.save(account);
    }
    public void updateUsername(Account account, String username) {
        account.setUsername(username);
        accountRepository.save(account);
        login(account);
    }
}