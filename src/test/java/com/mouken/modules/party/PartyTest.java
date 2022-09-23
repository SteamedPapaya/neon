package com.mouken.modules.party;

import com.mouken.modules.account.Account;
import com.mouken.modules.account.UserAccount;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class PartyTest {
    Party party;
    Account account;
    UserAccount userAccount;

    @BeforeEach
    void beforeEach() {
        party = new Party();
        account = new Account();
        account.setUsername("test");
        account.setPassword("12345678");
        userAccount = new UserAccount(account);
    }

    @DisplayName("Joining")
    @Test
    void isJoinable() {
        party.setPublished(true);
        party.setRecruiting(true);

        assertTrue(party.isJoinable(userAccount));
    }

    @DisplayName("You can not re-join. (manager)")
    @Test
    void isJoinable_false_for_manager() {
        party.setPublished(true);
        party.setRecruiting(true);
        party.addManager(account);

        assertFalse(party.isJoinable(userAccount));
    }

    @DisplayName("You can not re-join.")
    @Test
    void isJoinable_false_for_member() {
        party.setPublished(true);
        party.setRecruiting(true);
        party.addMember(account);

        assertFalse(party.isJoinable(userAccount));
    }

    @DisplayName("You can not join, if it is not published or recruiting.")
    @Test
    void isJoinable_false_for_non_recruiting_party() {
        party.setPublished(true);
        party.setRecruiting(false);

        assertFalse(party.isJoinable(userAccount));

        party.setPublished(false);
        party.setRecruiting(true);

        assertFalse(party.isJoinable(userAccount));
    }

    @DisplayName("Are you a manager?")
    @Test
    void isManager() {
        party.addManager(account);
        assertTrue(party.isManager(userAccount));
    }

    @DisplayName("Are you a member?")
    @Test
    void isMember() {
        party.addMember(account);
        assertTrue(party.isMember(userAccount));
    }

}
