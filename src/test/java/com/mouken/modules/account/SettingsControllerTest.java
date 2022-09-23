package com.mouken.modules.account;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mouken.infra.MockMvcTest;
import com.mouken.modules.tag.Tag;
import com.mouken.modules.zone.Zone;
import com.mouken.modules.account.form.TagForm;
import com.mouken.modules.account.form.ZoneForm;
import com.mouken.modules.tag.TagRepository;
import com.mouken.modules.zone.ZoneRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.TestExecutionEvent;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

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
    AccountRepository accountRepository;
    @Autowired
    AccountService accountService;
    @Autowired
    PasswordEncoder passwordEncoder;
    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    TagRepository tagRepository;
    @Autowired
    ZoneRepository zoneRepository;

    private Zone testZone = Zone.builder().city("CITY").province("none").country("COUNTRY").build();

    @BeforeEach
    void beforeEach() {
        SignUpForm signUpForm = new SignUpForm();
        signUpForm.setEmail("test@email.com");
        signUpForm.setUsername("test");
        signUpForm.setPassword("12345678");
        accountService.processNewAccount(signUpForm);
        zoneRepository.save(testZone);
    }

    @AfterEach
    void afterEach() {
        accountRepository.deleteAll();
        zoneRepository.deleteAll();
    }

    @WithUserDetails(value="test", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("Account Zone Update Form")
    @Test
    void updateZonesForm() throws Exception {
        mockMvc.perform(get("/settings/zones"))
                .andExpect(view().name("settings/zones"))
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("whitelist"))
                .andExpect(model().attributeExists("zones"));
    }

    @WithUserDetails(value="test", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("Account Zone ADD")
    @Test
    void addZone() throws Exception {
        ZoneForm zoneForm = new ZoneForm();
        zoneForm.setZoneName(testZone.toString());

        mockMvc.perform(post("/settings/zones/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(zoneForm))
                        .with(csrf()))
                .andExpect(status().isOk());

        Account account = accountRepository.findByUsername("test");
        Zone zone = zoneRepository.findByCityAndCountry(testZone.getCity(), testZone.getCountry());
        assertTrue(account.getZones().contains(zone));
    }

    @WithUserDetails(value="test", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("Account Zone REMOVE")
    @Test
    void removeZone() throws Exception {
        Account account = accountRepository.findByUsername("test");
        Zone zone = zoneRepository.findByCityAndCountry(testZone.getCity(), testZone.getCountry());
        accountService.addZone(account, zone);

        ZoneForm zoneForm = new ZoneForm();
        zoneForm.setZoneName(testZone.toString());

        mockMvc.perform(post("/settings/zones/remove")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(zoneForm))
                        .with(csrf()))
                .andExpect(status().isOk());

        assertFalse(account.getZones().contains(zone));
    }

    @WithUserDetails(value="test", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("Update Tag Form")
    @Test
    void updateTagsForm() throws Exception {
        mockMvc.perform(get("/settings/tags"))
                .andExpect(view().name("settings/tags"))
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("whitelist"))
                .andExpect(model().attributeExists("tags"));
    }

    @WithUserDetails(value="test", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("Add tag in account")
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
        Account account= accountRepository.findByUsername("test");
        assertTrue(account.getTags().contains(newTag));
    }

    @WithUserDetails(value="test", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("Remove tag from account")
    @Test
    void removeTag() throws Exception {
        Account account= accountRepository.findByUsername("test");
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

    @WithUserDetails(value="test", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("Account Update Form")
    @Test
    void updateAccountForm() throws Exception {
        mockMvc.perform(get("/settings/account"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("usernameForm"));
    }

    @WithUserDetails(value="test", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("Username Update Success")
    @Test
    void updateAccount() throws Exception {
        mockMvc.perform(post("/settings/account")
                        .param("newUsername", "new_test")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/settings/account"))
                .andExpect(flash().attributeExists("message"));

        assertNull(accountRepository.findByUsername("test"));
        assertNotNull(accountRepository.findByUsername("new_test"));


    }

    @WithUserDetails(value="test", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("Username Update Failure")
    @Test
    void updateAccount_failure() throws Exception {
        mockMvc.perform(post("/settings/account")
                        .param("newUsername", "¯\\_(ツ)_/¯")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("settings/account"))
                .andExpect(model().hasErrors())
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("usernameForm"));
    }

    @WithUserDetails(value="test", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("Profile Update Form")
    @Test
    void updateProfileForm() throws Exception {
        mockMvc.perform(get("/settings/profile"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("profile"));
    }

    @WithUserDetails(value="test", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("Profile Update (Correct input)")
    @Test
    void updateProfile() throws Exception {
        String bio = "짧은 소개를 수정하는 경우.";
        mockMvc.perform(post("/settings/profile")
                        .param("bio", bio)
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/settings/profile"))
                .andExpect(flash().attributeExists("message"));

        Account account = accountRepository.findByUsername("test");
        assertEquals(bio, account.getBio());
    }

    @WithUserDetails(value="test", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("Profile Update (Input Error)")
    @Test
    void updateProfile_error() throws Exception {
        String bio = "길게 소개를 수정하는 경우. 길게 소개를 수정하는 경우. 길게 소개를 수정하는 경우. 너무나도 길게 소개를 수정하는 경우. ";
        mockMvc.perform(post("/settings/profile")
                        .param("bio", bio)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("settings/profile"))
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("profile"))
                .andExpect(model().hasErrors());

        Account account = accountRepository.findByUsername("test");
        assertNull(account.getBio());
    }

    @WithUserDetails(value="test", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("Password Update Form")
    @Test
    void updatePassword_form() throws Exception {
        mockMvc.perform(get("/settings/password"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("passwordForm"));
    }

    @WithUserDetails(value="test", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("Password Update (Correct Input)")
    @Test
    void updatePassword_success() throws Exception {
        mockMvc.perform(post("/settings/password")
                        .param("newPassword", "12345678")
                        .param("newPasswordConfirm", "12345678")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/settings/password"))
                .andExpect(flash().attributeExists("message"));

        Account account = accountRepository.findByUsername("test");
        assertTrue(passwordEncoder.matches("12345678", account.getPassword()));
    }

    @WithUserDetails(value="test", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("Password Update (Input Error; Incorrect Confirming Password )")
    @Test
    void updatePassword_fail() throws Exception {
        mockMvc.perform(post("/settings/password")
                        .param("newPassword", "12345678")
                        .param("newPasswordConfirm", "WRONG_PASSWORD")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("settings/password"))
                .andExpect(model().hasErrors())
                .andExpect(model().attributeExists("passwordForm"))
                .andExpect(model().attributeExists("account"));
    }
}