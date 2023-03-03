package com.mouken.modules.account.controller;

import com.mouken.infra.MockMvcTest;
import com.mouken.modules.account.Account;
import com.mouken.modules.account.repository.AccountRepository;
import com.mouken.modules.util.mail.EmailMessage;
import org.junit.Before;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.unauthenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@MockMvcTest
class AccountControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AccountRepository accountRepository;

    private final static String USERNAME = "ryuu123";
    private final static String EMAIL = "ryuu123@gmail.com";
    private final static String PASSWORD = "12345678";
    private final static String INVALID_USERNAME = "@@@";
    private final static String INVALID_EMAIL = "@@@";
    private final static String INVALID_PASSWORD = "@@@";

    @Before
    public void create() {

    }

    final String SIGN_UP = "sign-up";

    @Test
    public void signUpForm() throws Exception {
        mockMvc.perform(get("/sign-up"))
                .andExpect(status().isOk())
                .andExpect(view().name(SIGN_UP))
                .andExpect(model().attributeExists("accountCreateForm"))
                .andExpect(unauthenticated());
    }

    @Test
    void signUpSubmit() throws Exception {
        // given
        mockMvc.perform(post("/sign-up")
                        .param("username", USERNAME)
                        .param("email", EMAIL)
                        .param("password", PASSWORD)
                        .with(csrf()));
                //todo .andExpect(status().is3xxRedirection())
                //todo .andExpect(view().name("redirect:/"));
                //todo .andExpect(authenticated().withUsername(USERNAME));

        // when
        Account account = accountRepository.findByEmail(EMAIL);

        // then
        assertNotNull(account);
        assertNotNull(account.getEmailCheckToken());
        assertNotEquals(account.getPassword(), PASSWORD);
        assertNotNull(account.getEmailCheckToken());
        //then(emailService).should().sendEmail(any(EmailMessage.class));
    }

}