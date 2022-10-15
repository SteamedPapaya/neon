package com.mouken.modules.party;

import com.mouken.infra.AbstractContainerBaseTest;
import com.mouken.infra.MockMvcTest;
import com.mouken.modules.account.*;
import com.mouken.modules.account.db.AccountRepository;
import com.mouken.modules.account.domain.Account;
import com.mouken.modules.account.web.form.SignUpForm;
import com.mouken.modules.account.service.AccountService;
import com.mouken.modules.party.db.PartyRepository;
import com.mouken.modules.party.domain.Party;
import com.mouken.modules.party.service.PartyService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.test.context.support.TestExecutionEvent;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;


@MockMvcTest
public class PartyControllerTest extends AbstractContainerBaseTest {

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
    @Autowired
    AccountFactory accountFactory;
    @Autowired PartyFactory partyFactory;

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
        party.setTitle("TEST_PARTY");
        party.setShortDescription("INTRO");
        party.setFullDescription("<p>CONTENT</p>");

        Account account = accountRepository.findByUsername("test");
        Party newParty = partyService.createNewParty(party, account);

        mockMvc.perform(get("/party/" + newParty.getPath()))
                .andExpect(view().name("party/view"))
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("party"));
    }

    @Test
    @WithUserDetails(value="test", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("Join party")
    void joinParty() throws Exception {
        Account user1 = accountFactory.createAccount("user1");
        Party party = partyFactory.createParty(user1);

        mockMvc.perform(get("/party/" + party.getPath() + "/join"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/party/" + party.getPath() + "/members"));

        Account account = accountRepository.findByUsername("test");
        assertTrue(party.getMembers().contains(account));
    }

    @Test
    @WithUserDetails(value="test", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("Leave party")
    void leaveParty() throws Exception {
        Account user1 = accountFactory.createAccount("user1");
        Party party = partyFactory.createParty(user1);
        
        Account account = accountRepository.findByUsername("test");
        partyService.addMember(party, account);

        mockMvc.perform(get("/party/" + party.getPath() + "/leave"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/party/" + party.getPath() + "/members"));

        assertFalse(party.getMembers().contains(account));
    }
    
}