package com.mouken.party;

import com.mouken.domain.Account;
import com.mouken.domain.Party;
import com.mouken.party.form.PartyDescriptionForm;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class PartyService {

    private final PartyRepository partyRepository;
    private final ModelMapper modelMapper;

    public Party createNewParty(Party party, Account account) {
        log.info("createNewParty");

        Party newParty = partyRepository.save(party);
        log.info("newParty Path = {}", newParty.getPath());
        newParty.addManager(account);
        return newParty;
    }

    public Party getParty(String path) {
        Party party = this.partyRepository.findByPath(path);
        if (party == null) {
            throw new IllegalArgumentException(path + "에 해당하는 스터디가 없습니다.");
        }

        return party;
    }

    public Party getPartyToUpdate(Account account, String path) {
        Party party = this.getParty(path);
        if (!account.isManagerOf(party)) {
            throw new AccessDeniedException("You're inaccessible.");
        }
        return party;
    }

    public void updatePartyDescription(Party party, PartyDescriptionForm partyDescriptionForm) {
        modelMapper.map(partyDescriptionForm, party);
    }

}
