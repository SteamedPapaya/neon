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

    public PartyFactory(PartyService partyService, PartyRepository partyRepository) {
        this.partyService = partyService;
        this.partyRepository = partyRepository;
    }

    public Party createParty(String path, Account manager) {
        Party party = new Party();
        party.setPath(path);
        partyService.createNewParty(party, manager);
        return party;
    }

}