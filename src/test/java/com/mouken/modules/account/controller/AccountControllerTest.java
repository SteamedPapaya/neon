package com.mouken.modules.account.controller;

import com.mouken.infra.MockMvcTest;
import com.mouken.modules.account.Account;
import com.mouken.modules.account.dto.AccountCreateForm;
import com.mouken.modules.account.repository.AccountRepository;
import com.mouken.modules.account.service.CustomUserDetailsService;
import com.mouken.modules.util.mail.EmailMessage;
import com.mouken.modules.util.mail.EmailService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.TestExecutionEvent;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.unauthenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.then;

@MockMvcTest
class AccountControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AccountRepository accountRepository;

    @MockBean
    EmailService emailService;
    @Autowired
    CustomUserDetailsService accountService;


    private final static String RANDOM_INPUT = UUID.randomUUID().toString();
    private final static String USERNAME = "test123";
    private final static String EMAIL = "test123@gmail.com";
    private final static String PASSWORD = "12345678";
    private final static String INVALID_USERNAME = "@@@";
    private final static String INVALID_EMAIL = "@@@";
    private final static String INVALID_PASSWORD = "@@@";

    final String SIGN_UP = "sign-up";

    @BeforeEach
    void beforeEach() {
        AccountCreateForm signUpForm = new AccountCreateForm();
        signUpForm.setEmail("test123@email.com");
        signUpForm.setUsername("test123");
        signUpForm.setPassword("12345678");
        accountService.createAccount(signUpForm);
    }

    @AfterEach
    void afterEach() {
        accountRepository.deleteAll();
    }

    @DisplayName("Sign-up page")
    @Test
    public void signUpForm() throws Exception {
        mockMvc.perform(get("/sign-up"))
                .andExpect(status().isOk())
                .andExpect(view().name(SIGN_UP))
                .andExpect(model().attributeExists("accountCreateForm"))
                .andExpect(unauthenticated());
    }

    @DisplayName("Profile view")
    @WithUserDetails(value=USERNAME, setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    void profileView() throws Exception {
        mockMvc.perform(get("/profile/" + USERNAME)
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("account/profile"));
    }

    @DisplayName("Check email view")
    @WithUserDetails(value=USERNAME, setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    void checkEmailView() throws Exception {
        mockMvc.perform(get("/check-email")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("account/check-email"));
    }

    @Disabled
    @DisplayName("Send email.")
    @WithUserDetails(value=USERNAME, setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    void sendEmail() throws Exception {
        // account.canSendConfirmEmail() = false
    }

    @Disabled
    @DisplayName("Send email when email is already verified.")
    @WithUserDetails(value=USERNAME, setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    void sendEmailWhenItIsAlreadyVerified() throws Exception {
        // account.isEmailVerified = true
    }

    @Disabled
    @DisplayName("Send email when it can not be sent now.")
    @WithUserDetails(value=USERNAME, setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    void sendEmailWhenItCanNotBeSent() throws Exception {
        // account.canSendConfirmEmail() = false
    }

    @DisplayName("After signing up, an email should be sent")
    @Test
    void AfterSigningUpEmailShouldBeSent() throws Exception {
        mockMvc.perform(post("/sign-up")
                        .param("username", USERNAME)
                        .param("email", EMAIL)
                        .param("password", PASSWORD)
                        .with(csrf()))
                        .andExpect(status().is3xxRedirection())
                        .andExpect(view().name("redirect:/"));

        Account account = accountRepository.findByEmail(EMAIL);

        assertNotNull(account);
        assertNotNull(account.getEmailCheckToken());
        assertNotEquals(account.getPassword(), PASSWORD);
        assertNotNull(account.getEmailCheckToken());
        then(emailService).should().sendEmail(any(EmailMessage.class));
    }

    @DisplayName("Signing up with an invalid username")
    @Test
    void signUpWithInvalidUsername() throws Exception {
        mockMvc.perform(post("/sign-up")
                        .param("username", INVALID_USERNAME) // invalid
                        .param("email", EMAIL)
                        .param("password", PASSWORD)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name(SIGN_UP))
                .andExpect(unauthenticated());
    }

    @DisplayName("Signing up with an invalid email")
    @Test
    void signUpWithInvalidEmail() throws Exception {
        mockMvc.perform(post("/sign-up")
                        .param("username", USERNAME)
                        .param("email", INVALID_EMAIL) // invalid
                        .param("password", PASSWORD)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name(SIGN_UP))
                .andExpect(unauthenticated());
    }

    @DisplayName("Signing up with an invalid password")
    @Test
    void signUpWithInvalidPassword() throws Exception {
        mockMvc.perform(post("/sign-up")
                        .param("username", USERNAME)
                        .param("email", EMAIL)
                        .param("password", INVALID_PASSWORD) // invalid
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name(SIGN_UP))
                .andExpect(unauthenticated());
    }

    @DisplayName("Email checking with a valid token")
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
                .andExpect(view().name("redirect:/check-email"));
    }

    @DisplayName("Email checking with an invalid token")
    @Test
    void checkEmailTokenWithAnInvalidToken() throws Exception {
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

    @DisplayName("Email checking with an invalid email")
    @Test
    void checkEmailTokenWithAnInvalidEmail() throws Exception {
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