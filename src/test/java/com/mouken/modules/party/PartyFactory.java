package com.mouken.modules.party;

import com.mouken.modules.account.Account;
import com.mouken.modules.party.db.PartyRepository;
import com.mouken.modules.party.domain.Party;
import com.mouken.modules.party.service.PartyService;
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
