package com.mouken.modules.party;

import com.mouken.infra.MockMvcTest;
import com.mouken.modules.account.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.TestExecutionEvent;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;

@MockMvcTest
class PartySettingsControllerTest{

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
    AccountService accountService;

    @BeforeEach
    void beforeEach() {
        SignUpForm signUpForm = new SignUpForm();
        signUpForm.setEmail("test@email.com");
        signUpForm.setUsername("test");
        signUpForm.setPassword("12345678");
        accountService.processNewAccount(signUpForm);
    }

    @AfterEach
    void afterEach() {
        accountRepository.deleteAll();
    }

    @Test
    @WithUserDetails(value="test", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("Party intro form - Failure (Not authorized)")
    void updateDescriptionForm_fail() throws Exception {
        Account account = accountFactory.createAccount("unknown");
        Party party = partyFactory.createParty(account);

        mockMvc.perform(get("/party/" + party.getPath() + "/settings/description"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithUserDetails(value="test", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("Party intro form - Success")
    void updateDescriptionForm_success() throws Exception {
        Account account = accountRepository.findByUsername("test");
        Party party = partyFactory.createParty(account);

        mockMvc.perform(get("/party/" + party.getPath() + "/settings/description"))
                .andExpect(status().isOk())
                .andExpect(view().name("party/settings/description"))
                .andExpect(model().attributeExists("partyDescriptionForm"))
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("party"));
    }

    @Test
    @WithUserDetails(value="test", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("Party intro update - Success")
    void updateDescription_success() throws Exception {
        Account account = accountRepository.findByUsername("test");
        Party party = partyFactory.createParty(account);

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
    @WithUserDetails(value="test", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("Party intro update - Faliure")
    void updateDescription_fail() throws Exception {
        Account account = accountRepository.findByUsername("test");
        Party party = partyFactory.createParty(account);

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