package com.mouken.modules.party.web;

import com.mouken.infra.MockMvcTest;
import com.mouken.modules.account.*;
import com.mouken.modules.account.dto.AccountCreateForm;
import com.mouken.modules.account.repository.AccountRepository;
import com.mouken.modules.account.Account;
import com.mouken.modules.account.service.CustomUserDetailsService;
import com.mouken.modules.account.web.form.SignUpForm;
import com.mouken.modules.account.service.AccountService;
import com.mouken.modules.party.PartyFactory;
import com.mouken.modules.party.db.PartyRepository;
import com.mouken.modules.party.domain.Party;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.test.context.support.TestExecutionEvent;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;

@MockMvcTest
class PartySettingsControllerTest {

    @Autowired
    MockMvc mockMvc;
    @Autowired
    AccountFactory accountFactory;
    @Autowired
    AccountRepository accountRepository;
    @Autowired
    PartyFactory partyFactory;
    @Autowired
    PartyRepository partyRepository;
    @Autowired
    CustomUserDetailsService accountService;

    private final static String PATH = "path1";
    private final static String USERNAME = "test123";
    private final static String EMAIL = "test123@email.com";
    private final static String PASSWORD = "12345678";
    private final static String UNKNOWN_USERNAME = "unknown123";

    @BeforeEach
    void beforeEach() {
        AccountCreateForm signUpForm = new AccountCreateForm();
        signUpForm.setEmail(EMAIL);
        signUpForm.setUsername(USERNAME);
        signUpForm.setPassword(PASSWORD);
        accountService.createAccount(signUpForm);
    }

    @AfterEach
    void afterEach() {
        accountRepository.deleteAll();
    }

    @Test
    @WithUserDetails(value=USERNAME, setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("Party intro form - Failure (Not authorized)")
    void updateDescriptionForm_fail() throws Exception {
        Account account = accountFactory.createAccount(UNKNOWN_USERNAME);
        Party party = partyFactory.createParty(PATH, account);

        mockMvc.perform(get("/party/" + party.getPath() + "/settings/description"))
                .andExpect(status().isOk())
                .andExpect(view().name("error"));
    }

    @Test
    @WithUserDetails(value=USERNAME, setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("Party intro form - Success")
    void updateDescriptionForm_success() throws Exception {
        Account account = accountRepository.findByUsername(USERNAME);
        Party party = partyFactory.createParty(PATH, account);

        mockMvc.perform(get("/party/" + party.getPath() + "/settings/description"))
                .andExpect(status().isOk())
                .andExpect(view().name("party/settings/description"))
                .andExpect(model().attributeExists("partyDescriptionForm"))
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("party"));
    }

    @Test
    @WithUserDetails(value=USERNAME, setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("Party intro update - Success")
    void updateDescription_success() throws Exception {
        Account account = accountRepository.findByUsername(USERNAME);
        Party party = partyFactory.createParty(PATH, account);

        String settingsDescriptionUrl = "/party/" + party.getPath() + "/settings/description";
        mockMvc.perform(post(settingsDescriptionUrl)
                        .param("shortDescription", "short description")
                        .param("fullDescription", "full description")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(settingsDescriptionUrl))
                .andExpect(flash().attributeExists("message"));
    }

    @Test
    @WithUserDetails(value=USERNAME, setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("Party intro update - Faliure")
    void updateDescription_fail() throws Exception {
        Account account = accountRepository.findByUsername(USERNAME);
        Party party = partyFactory.createParty(PATH, account);

        String settingsDescriptionUrl = "/party/" + party.getPath() + "/settings/description";
        mockMvc.perform(post(settingsDescriptionUrl)
                        .param("shortDescription", "")
                        .param("fullDescription", "full description")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(model().hasErrors())
                .andExpect(model().attributeExists("partyDescriptionForm"))
                .andExpect(model().attributeExists("party"))
                .andExpect(model().attributeExists("account"));
    }
}
