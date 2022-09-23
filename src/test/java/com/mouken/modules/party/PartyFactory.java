package com.mouken.modules.party;

import com.mouken.modules.account.Account;
import com.mouken.modules.account.SignUpForm;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PartyFactory {

    @Autowired
    PartyService partyService;
    @Autowired
    PartyRepository partyRepository;

    public Party createParty(Account manager) {
        Party party = new Party();

        partyService.createNewParty(party, manager);
        return party;
    }

}
