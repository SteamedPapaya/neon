package com.mouken.modules.account;

import com.mouken.infra.AbstractContainerBaseTest;
import com.mouken.infra.MockMvcTest;
import com.mouken.infra.mail.EmailMessage;
import com.mouken.infra.mail.EmailService;
import com.mouken.modules.account.repository.AccountRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.then;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.unauthenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@MockMvcTest
class AccountControllerTest extends AbstractContainerBaseTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AccountRepository accountRepository;

    @MockBean
    EmailService emailService;

    private final static String RANDOM_INPUT = UUID.randomUUID().toString();
    private final static String USERNAME = "ryuu123";
    private final static String EMAIL = "ryuu123@gmail.com";
    private final static String PASSWORD = "12345678";
    private final static String INVALID_USERNAME = "@@@";
    private final static String INVALID_EMAIL = "@@@";
    private final static String INVALID_PASSWORD = "@@@";

    @DisplayName("Sign Up - Form")
    @Test
    void signUpForm() throws Exception {
        mockMvc.perform(get("/sign-up"))
                .andExpect(status().isOk())
                .andExpect(view().name("account/sign-up"))
                .andExpect(model().attributeExists("signUpForm"))
                .andExpect(unauthenticated());

    }

    @DisplayName("Sign Up")
    @Test
    void signUpSubmit() throws Exception {
        mockMvc.perform(post("/sign-up")
                        .param("username", USERNAME)
                        .param("email", EMAIL)
                        .param("password", PASSWORD)
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/check-email"))
                .andExpect(authenticated().withUsername(USERNAME));

        Account account = accountRepository.findByEmail(EMAIL);

        // then
        assertNotNull(account);
        assertNotNull(account.getEmailCheckToken());
        assertNotEquals(account.getPassword(), PASSWORD);
        assertNotNull(account.getEmailCheckToken());
        then(emailService).should().sendEmail(any(EmailMessage.class));
    }

    @DisplayName("Sign Up (Invalid username)")
    @Test
    void signUpSubmit_with_invalid_username() throws Exception {
        mockMvc.perform(post("/sign-up")
                        .param("username", INVALID_USERNAME) // invalid
                        .param("email", EMAIL)
                        .param("password", PASSWORD)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("account/sign-up"))
                .andExpect(unauthenticated());
    }

    @DisplayName("Sign Up (Invalid email)")
    @Test
    void signUpSubmit_with_invalid_email() throws Exception {
        mockMvc.perform(post("/sign-up")
                        .param("username", USERNAME)
                        .param("email", INVALID_EMAIL) // invalid
                        .param("password", PASSWORD)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("account/sign-up"))
                .andExpect(unauthenticated());
    }

    @DisplayName("Sign Up (Invalid password)")
    @Test
    void signUpSubmit_with_invalid_password() throws Exception {
        mockMvc.perform(post("/sign-up")
                        .param("username", USERNAME)
                        .param("email", EMAIL)
                        .param("password", INVALID_PASSWORD) // invalid
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("account/sign-up"))
                .andExpect(unauthenticated());
    }


    @DisplayName("Email Checking")
    @Test
    void checkEmailToken() throws Exception {
        // todo refactor: using SignUpForm and AccountService.saveAccount(SignUpForm)
        Account account = Account.builder() 
                .username(USERNAME)
                .email(EMAIL)
                .password(PASSWORD)
                .build();
        Account newAccount = accountRepository.save(account);
        newAccount.generateEmailCheckToken();
        
        mockMvc.perform(get("/check-email-token")
                        .param("token", newAccount.getEmailCheckToken())
                        .param("email", newAccount.getEmail()))
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attributeExists("info"))
                .andExpect(view().name("redirect:/check-email"))
                .andExpect(authenticated().withUsername(USERNAME));
    }

    @DisplayName("Email Checking (Invalid token)")
    @Test
    void checkEmailToken_with_invalid_token() throws Exception {
        // todo refactor: using SignUpForm and AccountService.saveAccount(SignUpForm)
        Account account = Account.builder()
                .username(USERNAME)
                .email(EMAIL)
                .password(PASSWORD)
                .build();
        Account newAccount = accountRepository.save(account);
        newAccount.generateEmailCheckToken();

        mockMvc.perform(get("/check-email-token")
                        .param("token", RANDOM_INPUT) // invalid
                        .param("email", newAccount.getEmail()))
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attributeExists("error"))
                .andExpect(view().name("redirect:/check-email"))
                .andExpect(unauthenticated());
    }

    @DisplayName("Email Checking (Invalid email)")
    @Test
    void checkEmailToken_with_invalid_email() throws Exception {
        // todo refactor: using SignUpForm and AccountService.saveAccount(SignUpForm)
        Account account = Account.builder()
                .username(USERNAME)
                .email(EMAIL)
                .password(PASSWORD)
                .build();
        Account newAccount = accountRepository.save(account);
        newAccount.generateEmailCheckToken();

        mockMvc.perform(get("/check-email-token")
                        .param("token", newAccount.getEmailCheckToken())
                        .param("email", RANDOM_INPUT)) // invalid
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attributeExists("error"))
                .andExpect(view().name("redirect:/check-email"))
                .andExpect(unauthenticated());
    }

}