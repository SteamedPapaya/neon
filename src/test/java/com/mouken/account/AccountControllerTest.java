package com.mouken.account;

import com.mouken.account.AccountRepository;
import com.mouken.account.AccountService;
import com.mouken.account.SignUpForm;
import com.mouken.domain.Account;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.then;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.unauthenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Transactional
@SpringBootTest
@AutoConfigureMockMvc
class AccountControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private AccountService accountService;

    @MockBean
    JavaMailSender javaMailSender;

    @BeforeEach
    void beforeEach() {
        SignUpForm signUpForm = new SignUpForm();
        signUpForm.setUsername("test");
        signUpForm.setEmail("test@email.com");
        signUpForm.setPassword("12345678");
        accountService.processNewAccount(signUpForm);
    }

    @AfterEach
    void afterEach() {
        accountRepository.deleteAll();
    }

    @DisplayName("Login with Email - correct input")
    @Test
    void login_with_email() throws Exception {
        mockMvc.perform(post("/login") // SS 에 의해 처리됨 (이때 redirection 발생)
                        .param("username", "test@email.com")
                        .param("password", "12345678")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"))
                .andExpect(authenticated().withUsername("test"));
    }

    @DisplayName("Login with Username - correct input")
    @Test
    void login_with_username() throws Exception {
        mockMvc.perform(post("/login")
                        .param("username", "test")
                        .param("password", "12345678")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"))
                .andExpect(authenticated().withUsername("test"));
    }

    @DisplayName("Login - wrong input")
    @Test
    void login_fail() throws Exception {
        mockMvc.perform(post("/login")
                        .param("username", "WRONG_USERNAME")
                        .param("password", "WRONG_PASSWORD")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login?error"))
                .andExpect(unauthenticated());
    }

    @DisplayName("Sign Up - Form")
    @Test
    void signUpForm() throws Exception {
        mockMvc.perform(get("/sign-up"))
                .andExpect(status().isOk())
                .andExpect(view().name("account/sign-up"))
                .andExpect(model().attributeExists("signUpForm"))
                .andExpect(unauthenticated());

    }

    @DisplayName("Sign Up - Incorrect Input")
    @Test
    void signUpSubmit_with_wrong_input() throws Exception {
        mockMvc.perform(post("/sign-up")
                        .param("username", "ryuu123")
                        .param("email", "email..")
                        .param("password", "12345")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("account/sign-up"))
                .andExpect(unauthenticated());
    }

    @DisplayName("Sign Up - Correct Input")
    @Test
    void signUpSubmit_with_correct_input() throws Exception {
        mockMvc.perform(post("/sign-up")
                        .param("username", "ryuu123")
                        .param("email", "ryuu123@gmail.com")
                        .param("password", "12345678")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/check-email"))
                .andExpect(authenticated().withUsername("ryuu123"));

        // then
        Account account = accountRepository.findByEmail("ryuu123@gmail.com");
        assertNotNull(account);
        assertNotNull(account.getEmailCheckToken());
        // then; Is the password encoded?
        assertNotEquals(account.getPassword(), "12345678");
        assertNotNull(account.getEmailCheckToken());

    }

    @DisplayName("Email Checking - Incorrect Input")
    @Test
    void checkEmailToken_with_wrong_input() throws Exception {
        mockMvc.perform(get("/check-email-token")
                        .param("token", "sdfjslwfwef")
                        .param("email", "email@email.com"))
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attributeExists("error"))
                .andExpect(view().name("redirect:/check-email"))
                .andExpect(unauthenticated());
    }

    @DisplayName("Email Checking - Correct Input")
    @Test
    void checkEmailToken() throws Exception {
        Account account = Account.builder()
                .email("ryuu123@email.com")
                .password("12345678")
                .username("ryuu123")
                .build();
        Account newAccount = accountRepository.save(account);
        newAccount.generateEmailCheckToken();

        mockMvc.perform(get("/check-email-token")
                        .param("token", newAccount.getEmailCheckToken())
                        .param("email", newAccount.getEmail()))
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attributeExists("info"))
                .andExpect(view().name("redirect:/check-email"))
                .andExpect(authenticated().withUsername("ryuu123"));
    }

}