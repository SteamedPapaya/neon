package com.mouken.modules.main;

import com.mouken.infra.AbstractContainerBaseTest;
import com.mouken.infra.MockMvcTest;
import com.mouken.modules.account.db.AccountRepository;
import com.mouken.modules.account.service.AccountService;
import com.mouken.modules.account.web.form.SignUpForm;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.test.context.support.TestExecutionEvent;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.unauthenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@MockMvcTest
class MainControllerTest extends AbstractContainerBaseTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private AccountService accountService;

    private final static String USERNAME = "ryuu123";
    private final static String EMAIL = "ryuu123@gmail.com";
    private final static String PASSWORD = "12345678";
    private final static String WRONG_USERNAME = "wrong_username";
    private final static String WRONG_PASSWORD = "87654321";

    @BeforeEach
    void beforeEach() {
        SignUpForm signUpForm = new SignUpForm();
        signUpForm.setUsername(USERNAME);
        signUpForm.setEmail(EMAIL);
        signUpForm.setPassword(PASSWORD);
        accountService.processNewAccount(signUpForm);
    }

    @AfterEach
    void afterEach() {
        accountRepository.deleteAll();
    }


    @DisplayName("Login with email")
    @Test
    void login_with_email() throws Exception {
        mockMvc.perform(post("/login") // Spring Security 에 의해 처리됨 (이때 redirection 발생)
                        .param("username", EMAIL)
                        .param("password", PASSWORD)
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"))
                .andExpect(authenticated().withUsername(USERNAME));
    }

    @DisplayName("Login with username")
    @Test
    void login_with_username() throws Exception {
        mockMvc.perform(post("/login")
                        .param("username", USERNAME)
                        .param("password", PASSWORD)
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"))
                .andExpect(authenticated().withUsername(USERNAME));
    }

    @DisplayName("Login with username (Wrong username)")
    @Test
    void login_with_wrong_username() throws Exception {
        mockMvc.perform(post("/login")
                        .param("username", WRONG_USERNAME)
                        .param("password", PASSWORD)
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login?error"))
                .andExpect(unauthenticated());
    }

    @DisplayName("Login with username (Wrong password)")
    @Test
    void login_with_wrong_password() throws Exception {
        mockMvc.perform(post("/login")
                        .param("username", USERNAME)
                        .param("password", WRONG_PASSWORD)
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login?error"))
                .andExpect(unauthenticated());
    }

    @WithUserDetails(value=USERNAME, setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("Logout")
    @Test
    void logout() throws Exception {
        mockMvc.perform(post("/logout")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/?logout"))
                .andExpect(unauthenticated());
    }
}