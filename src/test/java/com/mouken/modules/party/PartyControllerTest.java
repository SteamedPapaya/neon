package com.mouken.modules.party;

import com.mouken.modules.account.AccountRepository;
import com.mouken.modules.account.AccountService;
import com.mouken.modules.account.SignUpForm;
import com.mouken.modules.account.Account;
import com.mouken.modules.party.Party;
import com.mouken.modules.party.PartyRepository;
import com.mouken.modules.party.PartyService;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;


@Transactional
@SpringBootTest
@AutoConfigureMockMvc
public class PartyControllerTest {

    @Autowired
    protected MockMvc mockMvc;
    @Autowired
    protected PartyService partyService;
    @Autowired
    protected PartyRepository partyRepository;
    @Autowired
    protected AccountRepository accountRepository;
    @Autowired
    protected AccountService accountService;

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

    @DisplayName("New Party FORM")
    @WithUserDetails(value="test", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    void createPartyForm() throws Exception {
        mockMvc.perform(get("/new-party"))
                .andExpect(status().isOk())
                .andExpect(view().name("party/form"))
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("partyForm"));
    }

/*    @DisplayName("New Party - Complete")
    @WithUserDetails(value="test", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    void createParty_success() throws Exception {
        mockMvc.perform(post("/new-party")
                        .param("path", "test-path")
                        .param("title", "party title")
                        .param("shortDescription", "short description of a party")
                        .param("fullDescription", "full description of a party")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/party/test-path"));

        Party party = partyRepository.findByPath("test-path");
        assertNotNull(party);
        Account account = accountRepository.findByUsername("test");
        assertTrue(party.getManagers().contains(account));
    }*/

/*    @DisplayName("New Party - Failure")
    @WithUserDetails(value="test", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    void createParty_fail() throws Exception {
        mockMvc.perform(post("/new-party")
                        .param("path", "WRONGPATH")
                        .param("title", "PARTY TITLE")
                        .param("shortDescription", "short description of a party")
                        .param("fullDescription", "full description of a party")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("party/form"))
                .andExpect(model().hasErrors())
                .andExpect(model().attributeExists("partyForm"))
                .andExpect(model().attributeExists("account"));

        Party party = partyRepository.findByPath("test-path");
        assertNull(party);
    }*/

    @DisplayName("Party View")
    @WithUserDetails(value="test", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    void viewParty() throws Exception {
        Party party = new Party();
        party.setPath("test-path");
        party.setTitle("TEST_PARTY");
        party.setShortDescription("INTRO");
        party.setFullDescription("<p>CONTENT</p>");

        Account account = accountRepository.findByUsername("test");
        partyService.createNewParty(party, account);

        mockMvc.perform(get("/party/test-path"))
                .andExpect(view().name("party/view"))
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("party"));
    }

    protected Party createParty(String path, Account manager) {
        Party party = new Party();
        party.setPath(path);
        partyService.createNewParty(party, manager);
        return party;
    }

    protected Account createAccount(String username) {
        Account account = new Account();
        account.setUsername(username);
        account.setEmail(username + "@email.com");
        accountRepository.save(account);
        return account;
    }
    
}