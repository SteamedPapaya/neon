package com.mouken.modules.account.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mouken.infra.MockMvcTest;
import com.mouken.modules.account.Account;
import com.mouken.modules.account.dto.AccountCreateForm;
import com.mouken.modules.account.repository.AccountRepository;
import com.mouken.modules.account.service.AccountService;
import com.mouken.modules.account.service.CustomUserDetailsService;
import com.mouken.modules.account.web.form.TagForm;
import com.mouken.modules.tag.db.TagRepository;
import com.mouken.modules.tag.domain.Tag;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.TestExecutionEvent;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;

@MockMvcTest
class SettingsControllerTest {

    @Autowired
    MockMvc mockMvc;
    @Autowired
    CustomUserDetailsService userDetailsService;
    @Autowired
    AccountService accountService;
    @Autowired
    PasswordEncoder passwordEncoder;
    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    AccountRepository accountRepository;
    @Autowired
    TagRepository tagRepository;

    private final static String USERNAME = "test";
    private final static String NEW_USERNAME = "new_test";
    private final static String INVALID_USERNAME = "짱\\_(??_/짱";
    private final static String EMAIL = "test123@gmail.com";
    private final static String PASSWORD = "12345678";
    private final static String NEW_PASSWORD = "123456789";
    private final static String INVALID_PASSWORD = "1";

    @BeforeEach
    void beforeEach() {
        AccountCreateForm form = new AccountCreateForm();
        form.setUsername(USERNAME);
        form.setEmail(EMAIL);
        form.setPassword(PASSWORD);
        userDetailsService.createAccount(form);
    }

    @AfterEach
    void afterEach() {
        accountRepository.deleteAll();
        tagRepository.deleteAll();
    }

    @WithUserDetails(value = USERNAME, setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("Tag-update page")
    @Test
    void updateTagsForm() throws Exception {
        mockMvc.perform(get("/settings/tags"))
                .andExpect(view().name("settings/tags"))
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("whitelist"))
                .andExpect(model().attributeExists("tags"));
    }

    @WithUserDetails(value = USERNAME, setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("Add a tag to an account")
    @Test
    void addTag() throws Exception {
        TagForm tagForm = new TagForm();
        tagForm.setTagTitle("newTag");

        mockMvc.perform(post("/settings/tags/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(tagForm))
                        .with(csrf()))
                .andExpect(status().isOk());

        Tag newTag = tagRepository.findByTitle("newTag");
        assertNotNull(newTag);
        Account account = accountRepository.findByUsername(USERNAME);
        assertTrue(account.getTags().contains(newTag));
    }


    @WithUserDetails(value = USERNAME, setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("Remove a tag from an account")
    @Test
    void removeTag() throws Exception {
        Account account = accountRepository.findByUsername(USERNAME);
        Tag newTag = tagRepository.save(Tag.builder().title("newTag").build());
        accountService.addTag(account, newTag);

        assertTrue(account.getTags().contains(newTag));

        TagForm tagForm = new TagForm();
        tagForm.setTagTitle("newTag");

        mockMvc.perform(post("/settings/tags/remove")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(tagForm))
                        .with(csrf()))
                .andExpect(status().isOk());

        assertFalse(account.getTags().contains(newTag));
    }

    @WithUserDetails(value = USERNAME, setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("Account-update page")
    @Test
    void updateAccountForm() throws Exception {
        mockMvc.perform(get("/settings/account"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("usernameForm"));
    }

    @WithUserDetails(value = USERNAME, setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("Updating a username with a valid input")
    @Test
    void updateAccount() throws Exception {
        mockMvc.perform(post("/settings/account")
                        .param("newUsername", NEW_USERNAME)
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/settings/account"))
                .andExpect(flash().attributeExists("message"));

        assertNull(accountRepository.findByUsername(USERNAME));
        assertNotNull(accountRepository.findByUsername(NEW_USERNAME));


    }

    @WithUserDetails(value = USERNAME, setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("An error should occur when updating a username with an invalid input")
    @Test
    void updateAccount_failure() throws Exception {
        mockMvc.perform(post("/settings/account")
                        .param("newUsername", INVALID_USERNAME)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("settings/account"))
                .andExpect(model().hasErrors())
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("usernameForm"));
    }

    @WithUserDetails(value = USERNAME, setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("Profile-update Form")
    @Test
    void updateProfileForm() throws Exception {
        mockMvc.perform(get("/settings/profile"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("profile"));
    }

    @WithUserDetails(value = USERNAME, setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("Updating a profile with a valid input")
    @Test
    void updateProfile() throws Exception {
        String bio = "Hello World!";
        mockMvc.perform(post("/settings/profile")
                        .param("bio", bio)
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/settings/profile"))
                .andExpect(flash().attributeExists("message"));

        Account account = accountRepository.findByUsername(USERNAME);
        assertEquals(bio, account.getBio());
    }

    @WithUserDetails(value = USERNAME, setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("An error should occur when updating a profile with an invalid input")
    @Test
    void updateProfileWithAnInvalidInput() throws Exception {
        String bio = "Hell World! Hell World! Hell World! Hell World! Hell World!";
        mockMvc.perform(post("/settings/profile")
                        .param("bio", bio)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("settings/profile"))
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("profile"))
                .andExpect(model().hasErrors());

        Account account = accountRepository.findByUsername(USERNAME);
        assertNull(account.getBio());
    }

    @WithUserDetails(value = USERNAME, setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("Password-update Form")
    @Test
    void updatePassword_form() throws Exception {
        mockMvc.perform(get("/settings/password"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("passwordForm"));
    }

    @WithUserDetails(value = USERNAME, setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("Updating a password with a valid input")
    @Test
    void updatePassword_success() throws Exception {
        mockMvc.perform(post("/settings/password")
                        .param("newPassword", NEW_PASSWORD)
                        .param("newPasswordConfirm", NEW_PASSWORD)
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/settings/password"))
                .andExpect(flash().attributeExists("message"));

        Account account = accountRepository.findByUsername(USERNAME);
        assertTrue(passwordEncoder.matches(NEW_PASSWORD, account.getPassword()));
    }

    @WithUserDetails(value = USERNAME, setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("An error should occur when updating a password with an incorrect confirming password")
    @Test
    void updatePassword_fail() throws Exception {
        mockMvc.perform(post("/settings/password")
                        .param("newPassword", NEW_PASSWORD)
                        .param("newPasswordConfirm", NEW_PASSWORD + "!")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("settings/password"))
                .andExpect(model().hasErrors())
                .andExpect(model().attributeExists("passwordForm"))
                .andExpect(model().attributeExists("account"));

    }
}